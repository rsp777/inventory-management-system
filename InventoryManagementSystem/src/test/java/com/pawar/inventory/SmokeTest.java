package com.pawar.inventory;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SmokeTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void actuatorHealthReturns200() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    void atLeastOneRestEndpointReturns200() {
        List<String> endpoints = Arrays.asList("/inventory/list", "/category/list", "/grp/list");
        boolean ok = false;
        for (String e : endpoints) {
            try {
                ResponseEntity<String> r = restTemplate.getForEntity(e, String.class);
                if (r.getStatusCode().is2xxSuccessful()) {
                    ok = true;
                    break;
                }
            } catch (Exception ex) {
                // ignore and try next
            }
        }
        assertTrue(ok, "No application REST endpoint returned 2xx (checked endpoints list)");
    }

}
