param(
    [string]$Root = ".",
    [string]$OutputMd = "reports/wildfly-migration-report.md",
    [string]$OutputJson = "reports/wildfly-migration-findings.json"
)

$ErrorActionPreference = "Stop"

$resolvedRoot = (Resolve-Path $Root).Path

$excludeDirs = @("target", ".git", ".idea", ".vscode", "node_modules", "build", "logs")

function Test-InExcludedDir {
    param([string]$FullPath)
    foreach ($dir in $excludeDirs) {
        if ($FullPath -match "[\\/]$([regex]::Escape($dir))([\\/]|$)") {
            return $true
        }
    }
    return $false
}

$rules = @(
    @{ Name="spring-boot-parent"; Category="build-runtime"; Severity="high"; Target="Replace Spring Boot parent with standard Maven parent + WildFly dependencies"; Rationale="Boot parent models a standalone runtime"; Pattern="<groupId>org\.springframework\.boot</groupId>|spring-boot-starter-parent"; Extensions=@("pom.xml") },
    @{ Name="spring-boot-plugin"; Category="build-runtime"; Severity="high"; Target="Package as WAR for WildFly; remove boot repackaging"; Rationale="Executable JAR packaging is not container-first"; Pattern="spring-boot-maven-plugin"; Extensions=@("pom.xml") },
    @{ Name="spring-boot-starters"; Category="framework-coupling"; Severity="high"; Target="Map Spring starters to Jakarta EE/WildFly capabilities"; Rationale="Spring starters are framework-coupled"; Pattern="<groupId>org\.springframework(\.boot|\.cloud)?</groupId>|spring-boot-starter|spring-cloud"; Extensions=@("pom.xml") },
    @{ Name="boot-entrypoint"; Category="runtime-bootstrap"; Severity="high"; Target="Replace SpringApplication bootstrap with container deployment model"; Rationale="WildFly controls lifecycle"; Pattern="SpringApplication\.run|@SpringBootApplication"; Extensions=@(".java") },
    @{ Name="spring-web-annotations"; Category="web-layer"; Severity="high"; Target="Convert to JAX-RS annotations and jakarta.ws.rs APIs"; Rationale="Spring MVC annotations are framework-specific"; Pattern="@RestController|@RequestMapping|@GetMapping|@PostMapping|@PutMapping|@DeleteMapping|org\.springframework\.web\.bind"; Extensions=@(".java") },
    @{ Name="spring-di"; Category="dependency-injection"; Severity="medium"; Target="Use CDI annotations and @Inject"; Rationale="Spring DI annotations should map to CDI"; Pattern="@Autowired|@Service|@Repository|@Component|org\.springframework\.stereotype"; Extensions=@(".java") },
    @{ Name="spring-transactions"; Category="transactions"; Severity="medium"; Target="Use jakarta.transaction.Transactional"; Rationale="Use Jakarta transaction APIs in app server"; Pattern="org\.springframework\.transaction\.annotation\.Transactional"; Extensions=@(".java") },
    @{ Name="spring-security"; Category="security"; Severity="high"; Target="Replace Spring Security with Jakarta Security/Elytron mapping"; Rationale="Spring security stack does not map directly"; Pattern="org\.springframework\.security|SecurityFilterChain|HttpSecurity"; Extensions=@(".java", "pom.xml") },
    @{ Name="spring-kafka"; Category="messaging"; Severity="high"; Target="Replace Spring Kafka listener/template model with target integration"; Rationale="Spring Kafka annotations and templates are framework-specific"; Pattern="org\.springframework\.kafka|@KafkaListener|KafkaTemplate"; Extensions=@(".java", "pom.xml") },
    @{ Name="spring-data-table-annotation"; Category="data-model"; Severity="medium"; Target="Use jakarta.persistence.Table for JPA entities"; Rationale="Spring Data relational Table annotation should be standardized"; Pattern="org\.springframework\.data\.relational\.core\.mapping\.Table"; Extensions=@(".java") }
)

$refactorPatterns = @(
    @{ Key="controller-raw-map-payload"; Pattern="Map<String,\s*Object>\s+payload"; Recommendation="Use typed DTOs at API boundary; avoid generic map payload parsing in controllers." },
    @{ Key="controller-local-object-mapper"; Pattern="new\s+ObjectMapper\s*\("; Recommendation="Avoid local ObjectMapper construction; use shared serializer and typed contracts." },
    @{ Key="broad-exception-handling"; Pattern="catch\s*\(Exception\s+\w+\)"; Recommendation="Replace broad catch with specific exceptions and centralized exception mapping." },
    @{ Key="repository-external-integration"; Pattern="org\.apache\.http\.client|HttpClient|HttpGet|HttpDelete"; Recommendation="Move external integration out of repository to service/gateway abstraction." },
    @{ Key="field-injection"; Pattern="@Autowired"; Recommendation="Refactor to constructor injection for testability and explicit dependencies." }
)

$allFiles = Get-ChildItem -Path $resolvedRoot -Recurse -File | Where-Object {
    if (Test-InExcludedDir -FullPath $_.FullName) { return $false }
    if ($_.Name -eq "pom.xml") { return $true }
    return @(".java", ".properties", ".xml", ".yml", ".yaml") -contains $_.Extension
}

$findings = @()
$refactorCandidates = @()

foreach ($file in $allFiles) {
    $relativePath = $file.FullName.Substring($resolvedRoot.Length).TrimStart('\\').Replace('\\', '/')
    $content = Get-Content -Path $file.FullName

    foreach ($rule in $rules) {
        $applies = $false
        foreach ($ext in $rule.Extensions) {
            if ($ext.StartsWith(".")) {
                if ($file.Extension -eq $ext) { $applies = $true }
            } else {
                if ($file.Name -eq $ext) { $applies = $true }
            }
        }
        if (-not $applies) { continue }

        $matches = $content | Select-String -Pattern $rule.Pattern
        foreach ($m in $matches) {
            $findings += [PSCustomObject]@{
                file = $relativePath
                line = [int]$m.LineNumber
                match = $m.Line.Trim()
                rule_name = $rule.Name
                category = $rule.Category
                severity = $rule.Severity
                target = $rule.Target
                rationale = $rule.Rationale
            }
        }
    }

    if ($file.Extension -eq ".java") {
        foreach ($rp in $refactorPatterns) {
            $matches = $content | Select-String -Pattern $rp.Pattern
            foreach ($m in $matches) {
                $refactorCandidates += [PSCustomObject]@{
                    file = $relativePath
                    line = [int]$m.LineNumber
                    pattern = $rp.Key
                    match = $m.Line.Trim()
                    recommendation = $rp.Recommendation
                }
            }
        }
    }
}

$findings = $findings | Sort-Object severity, category, file, line, rule_name
$refactorCandidates = $refactorCandidates | Sort-Object pattern, file, line

$summaryBySeverity = @{}
$summaryByCategory = @{}
foreach ($f in $findings) {
    if (-not $summaryBySeverity.ContainsKey($f.severity)) { $summaryBySeverity[$f.severity] = 0 }
    if (-not $summaryByCategory.ContainsKey($f.category)) { $summaryByCategory[$f.category] = 0 }
    $summaryBySeverity[$f.severity]++
    $summaryByCategory[$f.category]++
}

$mdLines = @()
$mdLines += "# WildFly Migration Evidence Report"
$mdLines += ""
$mdLines += "This report is generated from exact repository matches only."
$mdLines += ""
$mdLines += "Total migration findings: $($findings.Count)"
$mdLines += "Total refactor candidates: $($refactorCandidates.Count)"
$mdLines += ""
$mdLines += "## Summary by Severity"
if ($summaryBySeverity.Count -eq 0) {
    $mdLines += "- none: 0"
} else {
    foreach ($k in ($summaryBySeverity.Keys | Sort-Object)) {
        $mdLines += "- ${k}: $($summaryBySeverity[$k])"
    }
}
$mdLines += ""
$mdLines += "## Summary by Category"
if ($summaryByCategory.Count -eq 0) {
    $mdLines += "- none: 0"
} else {
    foreach ($k in ($summaryByCategory.Keys | Sort-Object)) {
        $mdLines += "- ${k}: $($summaryByCategory[$k])"
    }
}
$mdLines += ""
$mdLines += "## Migration Findings"
if ($findings.Count -eq 0) {
    $mdLines += "No findings matched current rule set."
} else {
    $currentFile = ""
    foreach ($f in $findings) {
        if ($currentFile -ne $f.file) {
            $currentFile = $f.file
            $mdLines += ""
            $mdLines += "### $currentFile"
        }
        $mdLines += "- L$($f.line) [$($f.severity)] ($($f.category)) $($f.rule_name): '$($f.match)'"
        $mdLines += "  - Why: $($f.rationale)"
        $mdLines += "  - Target: $($f.target)"
    }
}

$mdLines += ""
$mdLines += "## Refactor Candidates (Business Logic Abstraction)"
if ($refactorCandidates.Count -eq 0) {
    $mdLines += "No refactor candidates matched current rule set."
} else {
    $currentFile = ""
    foreach ($c in $refactorCandidates) {
        if ($currentFile -ne $c.file) {
            $currentFile = $c.file
            $mdLines += ""
            $mdLines += "### $currentFile"
        }
        $mdLines += "- L$($c.line) ($($c.pattern)): '$($c.match)'"
        $mdLines += "  - Recommendation: $($c.recommendation)"
    }
}

$mdLines += ""
$mdLines += "## Prioritized Next Actions"
$mdLines += "1. Convert build packaging from Spring Boot executable model to WildFly WAR deployment model."
$mdLines += "2. Convert Spring MVC controllers to JAX-RS resources with DTO contracts."
$mdLines += "3. Replace Spring DI/transactions/security with CDI, Jakarta Transactions, and WildFly security integration."
$mdLines += "4. Extract mixed business/integration logic from repositories and controllers into service/gateway layers."

$outputMdPath = Join-Path $resolvedRoot $OutputMd
$outputJsonPath = Join-Path $resolvedRoot $OutputJson

New-Item -ItemType Directory -Force -Path (Split-Path -Parent $outputMdPath) | Out-Null
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $outputJsonPath) | Out-Null

Set-Content -Path $outputMdPath -Value $mdLines -Encoding UTF8

$payload = [PSCustomObject]@{
    summary = [PSCustomObject]@{
        by_category = $summaryByCategory
        by_severity = $summaryBySeverity
    }
    findings = $findings
    refactor_candidates = $refactorCandidates
}

$payload | ConvertTo-Json -Depth 8 | Set-Content -Path $outputJsonPath -Encoding UTF8

Write-Output "Scan completed. Findings: $($findings.Count), refactor candidates: $($refactorCandidates.Count)"
Write-Output "Markdown: $outputMdPath"
Write-Output "JSON: $outputJsonPath"
