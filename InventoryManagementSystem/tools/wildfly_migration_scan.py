#!/usr/bin/env python3
"""Evidence-only scanner for Spring Boot -> WildFly migration planning.

This script scans the repository and writes:
- Markdown report with file/line findings
- JSON findings for automation

It does not modify source code.
"""

from __future__ import annotations

import argparse
import json
import re
from dataclasses import asdict, dataclass
from pathlib import Path
from typing import Dict, Iterable, List, Tuple


@dataclass
class Rule:
    name: str
    category: str
    severity: str
    target: str
    rationale: str
    pattern: str
    applies_to: Tuple[str, ...]


@dataclass
class Finding:
    file: str
    line: int
    match: str
    rule_name: str
    category: str
    severity: str
    target: str
    rationale: str


RULES: List[Rule] = [
    Rule(
        name="spring-boot-parent",
        category="build-runtime",
        severity="high",
        target="Replace Spring Boot parent with standard Maven parent + WildFly BOM/dependencies",
        rationale="Boot parent and plugin model a standalone boot runtime, not a container-first WAR model.",
        pattern=r"<groupId>org\\.springframework\\.boot</groupId>|spring-boot-starter-parent",
        applies_to=("pom.xml",),
    ),
    Rule(
        name="spring-boot-plugin",
        category="build-runtime",
        severity="high",
        target="Package as WAR and deploy to WildFly; remove/limit boot repackaging plugin",
        rationale="Executable JAR packaging is not the default deployment style for WildFly.",
        pattern=r"spring-boot-maven-plugin",
        applies_to=("pom.xml",),
    ),
    Rule(
        name="spring-boot-starters",
        category="framework-coupling",
        severity="high",
        target="Replace Spring starters with Jakarta EE APIs and WildFly-provided capabilities",
        rationale="Spring starters are framework-coupled and require mapping to Jakarta EE equivalents.",
        pattern=r"<groupId>org\\.springframework(\\.boot|\\.cloud)?</groupId>|spring-boot-starter|spring-cloud",
        applies_to=("pom.xml",),
    ),
    Rule(
        name="boot-entrypoint",
        category="runtime-bootstrap",
        severity="high",
        target="Switch from SpringApplication bootstrap to servlet/Jakarta EE deployment bootstrap",
        rationale="WildFly controls application lifecycle; SpringApplication main bootstrap is framework-specific.",
        pattern=r"SpringApplication\\.run|@SpringBootApplication",
        applies_to=(".java",),
    ),
    Rule(
        name="spring-web-annotations",
        category="web-layer",
        severity="high",
        target="Convert to JAX-RS: @Path, @GET/@POST/@PUT/@DELETE, jakarta.ws.rs.*",
        rationale="Spring MVC controller annotations are not native JAX-RS annotations.",
        pattern=r"@RestController|@RequestMapping|@GetMapping|@PostMapping|@PutMapping|@DeleteMapping|org\\.springframework\\.web\\.bind",
        applies_to=(".java",),
    ),
    Rule(
        name="spring-di",
        category="dependency-injection",
        severity="medium",
        target="Replace Spring component model with CDI: @ApplicationScoped/@RequestScoped and @Inject",
        rationale="Spring stereotypes and @Autowired should be mapped to CDI in WildFly.",
        pattern=r"@Autowired|@Service|@Repository|@Component|org\\.springframework\\.stereotype",
        applies_to=(".java",),
    ),
    Rule(
        name="spring-transactions",
        category="transactions",
        severity="medium",
        target="Use jakarta.transaction.Transactional where appropriate",
        rationale="Transaction boundaries should use Jakarta transaction APIs in app server model.",
        pattern=r"org\\.springframework\\.transaction\\.annotation\\.Transactional",
        applies_to=(".java",),
    ),
    Rule(
        name="spring-security",
        category="security",
        severity="high",
        target="Replace Spring Security filter-chain config with Jakarta Security / WildFly Elytron integration",
        rationale="Spring Security infrastructure does not map directly to WildFly security subsystems.",
        pattern=r"org\\.springframework\\.security|SecurityFilterChain|HttpSecurity",
        applies_to=(".java", "pom.xml"),
    ),
    Rule(
        name="spring-kafka",
        category="messaging",
        severity="high",
        target="Replace Spring Kafka listeners/templates with JMS or Kafka client integration strategy for WildFly",
        rationale="Spring Kafka listener model is framework-specific and requires replacement plan.",
        pattern=r"org\\.springframework\\.kafka|@KafkaListener|KafkaTemplate",
        applies_to=(".java", "pom.xml"),
    ),
    Rule(
        name="spring-data-table-annotation",
        category="data-model",
        severity="medium",
        target="Use jakarta.persistence.Table consistently with JPA entities",
        rationale="Spring Data relational table annotations should be standardized for JPA/Jakarta persistence.",
        pattern=r"org\\.springframework\\.data\\.relational\\.core\\.mapping\\.Table",
        applies_to=(".java",),
    ),
]

REFACTOR_PATTERNS: List[Tuple[str, str, str]] = [
    (
        r"Map<String,\\s*Object>\\s+payload",
        "controller-raw-map-payload",
        "Replace generic Map payload parsing with typed request DTOs and move parsing/validation to boundary layer.",
    ),
    (
        r"new\\s+ObjectMapper\\s*\\(",
        "controller-local-object-mapper",
        "Avoid per-method ObjectMapper construction; use container-managed JSON binding and typed DTOs.",
    ),
    (
        r"catch\\s*\\(Exception\\s+\\w+\\)",
        "broad-exception-handling",
        "Replace broad exception catches with specific exceptions and centralized exception mapping.",
    ),
    (
        r"org\\.apache\\.http\\.client|HttpClient|HttpGet|HttpDelete",
        "repository-external-integration",
        "External integration in repository indicates mixed concerns; extract integration client into a service/gateway.",
    ),
    (
        r"@Autowired\\s*[\\r\\n]+\\s*(private\\s+)?[A-Za-z0-9_<>\\[\\]]+\\s+[A-Za-z0-9_]+\\s*;",
        "field-injection",
        "Use constructor injection to improve testability and align with explicit dependency contracts.",
    ),
]

EXCLUDE_DIRS = {"target", ".git", ".idea", ".vscode", "node_modules", "build", "logs"}


def should_scan(path: Path, root: Path) -> bool:
    rel = path.relative_to(root)
    if any(part in EXCLUDE_DIRS for part in rel.parts):
        return False
    if path.name == "pom.xml":
        return True
    return path.suffix in {".java", ".properties", ".xml", ".yml", ".yaml"}


def discover_files(root: Path) -> Iterable[Path]:
    for path in root.rglob("*"):
        if path.is_file() and should_scan(path, root):
            yield path


def line_matches(lines: List[str], regex: re.Pattern[str]) -> Iterable[Tuple[int, str]]:
    for i, line in enumerate(lines, start=1):
        if regex.search(line):
            yield i, line.strip()


def run_rule_scan(root: Path) -> List[Finding]:
    findings: List[Finding] = []
    file_cache: Dict[Path, List[str]] = {}

    for file_path in discover_files(root):
        try:
            lines = file_path.read_text(encoding="utf-8", errors="ignore").splitlines()
        except OSError:
            continue
        file_cache[file_path] = lines

    for rule in RULES:
        regex = re.compile(rule.pattern)
        for file_path, lines in file_cache.items():
            applies = False
            if file_path.name in rule.applies_to:
                applies = True
            if any(file_path.suffix == suffix for suffix in rule.applies_to if suffix.startswith(".")):
                applies = True
            if not applies:
                continue
            for line_no, matched in line_matches(lines, regex):
                findings.append(
                    Finding(
                        file=str(file_path.relative_to(root)).replace("\\\\", "/"),
                        line=line_no,
                        match=matched,
                        rule_name=rule.name,
                        category=rule.category,
                        severity=rule.severity,
                        target=rule.target,
                        rationale=rule.rationale,
                    )
                )

    # Deterministic ordering
    findings.sort(key=lambda f: (f.severity, f.category, f.file, f.line, f.rule_name))
    return findings


def run_refactor_scan(root: Path) -> List[Dict[str, str]]:
    candidates: List[Dict[str, str]] = []
    patterns = [(re.compile(p), key, desc) for p, key, desc in REFACTOR_PATTERNS]

    for file_path in discover_files(root):
        if file_path.suffix != ".java":
            continue
        rel_path = str(file_path.relative_to(root)).replace("\\\\", "/")
        try:
            lines = file_path.read_text(encoding="utf-8", errors="ignore").splitlines()
        except OSError:
            continue

        for i, line in enumerate(lines, start=1):
            for regex, key, desc in patterns:
                if regex.search(line):
                    candidates.append(
                        {
                            "file": rel_path,
                            "line": str(i),
                            "pattern": key,
                            "match": line.strip(),
                            "recommendation": desc,
                        }
                    )

    candidates.sort(key=lambda c: (c["pattern"], c["file"], int(c["line"])))
    return candidates


def summarize(findings: List[Finding]) -> Dict[str, Dict[str, int]]:
    summary: Dict[str, Dict[str, int]] = {"by_category": {}, "by_severity": {}}
    for f in findings:
        summary["by_category"][f.category] = summary["by_category"].get(f.category, 0) + 1
        summary["by_severity"][f.severity] = summary["by_severity"].get(f.severity, 0) + 1
    return summary


def to_markdown(findings: List[Finding], refactor_candidates: List[Dict[str, str]]) -> str:
    summary = summarize(findings)
    lines: List[str] = []
    lines.append("# WildFly Migration Evidence Report")
    lines.append("")
    lines.append("This report is generated from exact repository matches only.")
    lines.append("")
    lines.append(f"Total migration findings: {len(findings)}")
    lines.append(f"Total refactor candidates: {len(refactor_candidates)}")
    lines.append("")

    lines.append("## Summary by Severity")
    for severity, count in sorted(summary["by_severity"].items()):
        lines.append(f"- {severity}: {count}")
    if not summary["by_severity"]:
        lines.append("- none: 0")
    lines.append("")

    lines.append("## Summary by Category")
    for category, count in sorted(summary["by_category"].items()):
        lines.append(f"- {category}: {count}")
    if not summary["by_category"]:
        lines.append("- none: 0")
    lines.append("")

    lines.append("## Migration Findings")
    if not findings:
        lines.append("No Spring-coupling findings matched the current rule set.")
    else:
        current_file = None
        for f in findings:
            if current_file != f.file:
                current_file = f.file
                lines.append("")
                lines.append(f"### {f.file}")
            lines.append(
                f"- L{f.line} [{f.severity}] ({f.category}) {f.rule_name}: `{f.match}`"
            )
            lines.append(f"  - Why: {f.rationale}")
            lines.append(f"  - Target: {f.target}")
    lines.append("")

    lines.append("## Refactor Candidates (Business Logic Abstraction)")
    if not refactor_candidates:
        lines.append("No refactor candidates matched the current rule set.")
    else:
        current_file = None
        for c in refactor_candidates:
            if current_file != c["file"]:
                current_file = c["file"]
                lines.append("")
                lines.append(f"### {c['file']}")
            lines.append(
                f"- L{c['line']} ({c['pattern']}): `{c['match']}`"
            )
            lines.append(f"  - Recommendation: {c['recommendation']}")

    lines.append("")
    lines.append("## Prioritized Next Actions")
    lines.append("1. Convert build packaging from Spring Boot executable JAR to WildFly WAR deployment model.")
    lines.append("2. Replace Spring MVC controllers with JAX-RS resources using typed DTO contracts.")
    lines.append("3. Replace Spring DI/transaction/security usage with CDI, Jakarta Transactions, and WildFly security model.")
    lines.append("4. Move integration and orchestration logic out of repositories/controllers into service and gateway abstractions.")

    return "\n".join(lines) + "\n"


def write_outputs(root: Path, findings: List[Finding], refactor_candidates: List[Dict[str, str]], output_md: Path, output_json: Path) -> None:
    payload = {
        "summary": summarize(findings),
        "findings": [asdict(f) for f in findings],
        "refactor_candidates": refactor_candidates,
    }

    output_md.parent.mkdir(parents=True, exist_ok=True)
    output_json.parent.mkdir(parents=True, exist_ok=True)

    output_md.write_text(to_markdown(findings, refactor_candidates), encoding="utf-8")
    output_json.write_text(json.dumps(payload, indent=2), encoding="utf-8")


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Scan Spring Boot project for WildFly migration evidence.")
    parser.add_argument("--root", default=".", help="Project root path to scan")
    parser.add_argument("--output-md", default="reports/wildfly-migration-report.md", help="Markdown report path")
    parser.add_argument("--output-json", default="reports/wildfly-migration-findings.json", help="JSON findings path")
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    root = Path(args.root).resolve()
    findings = run_rule_scan(root)
    refactors = run_refactor_scan(root)
    write_outputs(root, findings, refactors, root / args.output_md, root / args.output_json)
    print(f"Scan completed. Findings: {len(findings)}, refactor candidates: {len(refactors)}")
    print(f"Markdown: {(root / args.output_md)}")
    print(f"JSON: {(root / args.output_json)}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
