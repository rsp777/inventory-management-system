package com.pawar.inventory.api;

import java.util.List;

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

import com.pawar.inventory.api.dto.LpnRequest;
import com.pawar.inventory.exceptions.CategoryNotFoundException;
import com.pawar.inventory.exceptions.ItemNotFoundException;
import com.pawar.inventory.exceptions.LpnNotFoundException;
import com.pawar.inventory.model.Lpn;
import com.pawar.inventory.model.Item;
import com.pawar.inventory.service.LpnService;

@RestController
@RequestMapping("/lpns")
public class LpnController {

    private static final Logger logger = LoggerFactory.getLogger(LpnController.class);

    private final LpnService lpnService;

    public LpnController(LpnService lpnService) {
        this.lpnService = lpnService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createLpn(@RequestBody LpnRequest request) {
        logger.info("Payload : {}", request);
        if (request == null || request.getLpn() == null) {
            return ResponseEntity.badRequest().body("Invalid lpn payload");
        }

        Lpn lpn = request.getLpn();
        Item item = lpn.getItem();
        try {
            Lpn created = lpnService.createLpn(lpn, item);
            return ResponseEntity.ok(created);
        } catch (LpnNotFoundException e) {
            return ResponseEntity.status(404).body("LPN Not Found: " + e.getMessage());
        } catch (ItemNotFoundException e) {
            return ResponseEntity.status(404).body("Item Not Found: " + e.getMessage());
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.status(404).body("Category Not Found: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<Iterable<Lpn>> getfindAllLpns() {
        return ResponseEntity.ok(lpnService.getfindAllLpns());
    }

    @GetMapping("/list/by-name/{lpn_name}")
    public ResponseEntity<?> getLpnByName(@PathVariable("lpn_name") String lpnName) {
        try {
            Lpn lpn = lpnService.getLpnByName(lpnName);
            return ResponseEntity.ok(lpn);
        } catch (LpnNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/list/by-id/{lpn_id}")
    public ResponseEntity<Lpn> findLpnById(@PathVariable("lpn_id") int lpnId) {
        return ResponseEntity.ok(lpnService.findLpnById(lpnId));
    }

    @GetMapping("/list/category/{category}")
    public ResponseEntity<?> findLpnByCategory(@PathVariable("category") String category) {
        try {
            List<Lpn> lpn = lpnService.findLpnByCategory(category);
            return ResponseEntity.ok(lpn);
        } catch (LpnNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @PutMapping("/update/by-id/{lpn_id}")
    public ResponseEntity<Lpn> updateLpnByLpnId(@PathVariable("lpn_id") int lpnId, @RequestBody Lpn lpn) {
        try {
            return ResponseEntity.ok(lpnService.updateLpnByLpnId(lpnId, lpn));
        } catch (ItemNotFoundException | CategoryNotFoundException e) {
            return ResponseEntity.ok(lpn);
        }
    }

    @PutMapping("/update/by-name/{lpn_name}/{adjustQty}")
    public ResponseEntity<?> updateLpnByLpnBarcode(@PathVariable("lpn_name") String lpnName,
            @RequestBody Lpn lpn, @PathVariable("adjustQty") int adjustQty) {
        try {
            Lpn updatedLpn = lpnService.updateLpnByLpnBarcode(lpnName, lpn, adjustQty);
            return ResponseEntity.ok("Lpn updated successfully :" + updatedLpn);
        } catch (ItemNotFoundException | CategoryNotFoundException e) {
            return ResponseEntity.status(404).build();
        } catch (LpnNotFoundException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/delete/by-id/{lpn_id}")
    public ResponseEntity<Lpn> deleteLpnByLpnId(@PathVariable("lpn_id") int lpnId) {
        return ResponseEntity.ok(lpnService.deleteLpnByLpnId(lpnId));
    }

    @DeleteMapping("/delete/by-name/{lpn_name}")
    public ResponseEntity<?> deleteLpnByLpnBarcode(@PathVariable("lpn_name") String lpnName) {
        try {
            lpnService.deleteLpnByLpnBarcode(lpnName);
            return ResponseEntity.noContent().build();
        } catch (LpnNotFoundException e) {
            return ResponseEntity.status(500).build();
        }
    }
}
