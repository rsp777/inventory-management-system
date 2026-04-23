package com.pawar.inventory.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pawar.inventory.exceptions.GrpAlreadyExistsException;
import com.pawar.inventory.model.Grp;
import com.pawar.inventory.service.GrpService;

@RestController
@RequestMapping("/grp")
public class GrpController {

    private static final Logger logger = LoggerFactory.getLogger(GrpController.class);

    private final GrpService grpService;

    public GrpController(GrpService grpService) {
        this.grpService = grpService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> createGrp(@RequestBody Grp grp) {
        logger.info("Grp : {}", grp.getGrpDesc());
        try {
            Grp newGrp = grpService.createGrp(grp);
            return ResponseEntity.ok("Grp Added Successfully : " + newGrp.getGrpName());
        } catch (GrpAlreadyExistsException e) {
            return ResponseEntity.status(409).body("Grp Already Exists: " + grp.getGrpDesc());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<Iterable<Grp>> getGrps() {
        return ResponseEntity.ok(grpService.getfindAllGrps());
    }

    @GetMapping("/list/by-name/{grp_name}")
    public ResponseEntity<Grp> getGrpByName(@PathVariable("grp_name") String grpName) {
        return ResponseEntity.ok(grpService.getGrpByName(grpName));
    }

    @GetMapping("/list/by-id/{grp_id}")
    public ResponseEntity<Grp> getGrpById(@PathVariable("grp_id") int grpId) {
        return ResponseEntity.ok(grpService.getGrpById(grpId));
    }

    @PutMapping("/update/by-id/{grp_id}")
    public ResponseEntity<Grp> updateGrpById(@PathVariable("grp_id") int grpId, @RequestBody Grp grp) {
        logger.info("Update this grp : {}", grp);
        return ResponseEntity.ok(grpService.updateGrpById(grpId, grp));
    }

    @PutMapping("/update/by-name/{grp_name}")
    public ResponseEntity<String> updateGrpByName(@PathVariable("grp_name") String grpName, @RequestBody Grp grp) {
        logger.info("Update this grp : {}", grp);
        grpService.updateGrpByName(grpName, grp);
        return ResponseEntity.ok("Grp Updated Successfully");
    }

    @DeleteMapping("/delete/by-id/{grp_id}")
    public ResponseEntity<Grp> deleteGrpById(@PathVariable("grp_id") int grpId) {
        return ResponseEntity.ok(grpService.deleteGrpById(grpId));
    }

    @DeleteMapping("/delete/by-name/{grp_name}")
    public ResponseEntity<Grp> deleteGrpByName(@PathVariable("grp_name") String grpName) {
        return ResponseEntity.ok(grpService.deleteGrpByName(grpName));
    }
}
