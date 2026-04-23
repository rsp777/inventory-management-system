package com.pawar.inventory.api;

import org.apache.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pawar.inventory.api.dto.InventoryMoveRequest;
import com.pawar.inventory.model.Location;
import com.pawar.inventory.model.Lpn;
import com.pawar.inventory.service.InventoryService;

import java.io.IOException;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/createReserve")
    public ResponseEntity<?> createReserveInventory(@RequestBody InventoryMoveRequest request) {
        var payload = request.getInventory();
        Lpn lpn = payload.getLpn();
        Location location = payload.getLocation();
        try {
            String response = inventoryService.createReserveInventory(lpn, location);
            return ResponseEntity.ok(response);
        } catch (ParseException | IOException e) {
            logger.error("Error in createReserveInventory", e);
            return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/createActive")
    public ResponseEntity<?> createActiveInventory(@RequestBody InventoryMoveRequest request) {
        var payload = request.getInventory();
        Lpn lpn = payload.getLpn();
        Location location = payload.getLocation();
        try {
            String response = inventoryService.createActiveInventory(lpn, location);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            logger.error("Error in createActiveInventory", e);
            return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
        }
    }
}
