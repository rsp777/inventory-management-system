package com.pawar.inventory.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pawar.inventory.api.dto.AsnReceiveRequest;
import com.pawar.inventory.exceptions.ASNNotFoundException;
import com.pawar.inventory.exceptions.LpnNotFoundException;
import com.pawar.inventory.model.ASN;
import com.pawar.inventory.service.ASNService;

@RestController
@RequestMapping("/asn")
public class AsnController {

    private static final Logger logger = LoggerFactory.getLogger(AsnController.class);

    private final ASNService asnService;

    public AsnController(ASNService asnService) {
        this.asnService = asnService;
    }

    @PostMapping("/receive")
    public ResponseEntity<?> receiveManualAsn(@RequestBody AsnReceiveRequest request) {
        logger.info("Payload : {}", request);
        if (request == null || request.getAsn() == null) {
            return ResponseEntity.badRequest().body("Invalid request payload");
        }

        ASN asn = request.getAsn();
        List<com.pawar.inventory.model.Lpn> lpns = asn.getLpns();
        try {
            String response = asnService.receiveAsn(asn, lpns);
            return ResponseEntity.ok(response);
        } catch (ASNNotFoundException e) {
            return ResponseEntity.status(404).body("ASN Not Found: " + e.getMessage());
        } catch (LpnNotFoundException e) {
            return ResponseEntity.status(404).body("Lpn Not Found: " + e.getMessage());
        }
    }

    @GetMapping("/list/by-name/{asn_name}")
    public ResponseEntity<ASN> getAsnByName(@PathVariable("asn_name") String asnName) {
        try {
            ASN asn = asnService.getASNByName(asnName);
            return ResponseEntity.ok(asn);
        } catch (jakarta.persistence.NoResultException | ASNNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/list/category/{category}")
    public ResponseEntity<?> getAsnByCategory(@PathVariable("category") String category) {
        try {
            List<ASN> asn = asnService.getAsnByCategory(category);
            return ResponseEntity.ok(asn);
        } catch (jakarta.persistence.NoResultException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
