package com.pawar.inventory.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pawar.inventory.exceptions.GrpAlreadyExistsException;
import com.pawar.inventory.model.Grp;
import com.pawar.inventory.service.CategoryService;
import com.pawar.inventory.service.GrpService;

@RestController
@RequestMapping("/grp")
//@EnableJpaRepositories
@EnableJdbcRepositories
public class GrpController {
	
	private final static Logger logger = LoggerFactory.getLogger(GrpController.class.getName());


	public GrpService grpService;
	
	 @Autowired
	    public GrpController(GrpService grpService) {
	        this.grpService = grpService;
	    }
	
	
	@PostMapping("/add")
	public ResponseEntity<?> createGrp(@RequestBody Grp grp){
		logger.info("Grp : "+grp.getGrpDesc());
		Grp newGrp;
		try {
			newGrp = grpService.createGrp(grp);
			logger.info("New Grp is now created : "+newGrp);
			return ResponseEntity.ok("Grp Added Successfully : "+newGrp.getGrpName());
		} catch (GrpAlreadyExistsException e) {
			logger.info("GrpAlreadyExistsException");
			return new ResponseEntity<String>("Grp Already Exists: " + grp.getGrpDesc(), HttpStatus.CONFLICT);

		}
		
		
	}
	
	@GetMapping("/list")
	public Iterable<Grp> getGrps(){
		Iterable<Grp> grps = grpService.getfindAllGrps();
		return grps;
	}
	
	@GetMapping("/list/by-name/{grp_name}")
	public Grp getGrpByName(@PathVariable  String grp_name){
		Grp grp = grpService.getGrpByName(grp_name);
		return grp;
	}
	
	@GetMapping("/list/by-id/{grp_id}")
	public Grp getGrpById(@PathVariable int grp_id){
		Grp grp = grpService.getGrpById(grp_id);
		return grp;
	}
	
	@PutMapping("/update/by-id/{grp_id}")
	public Grp updateGrpById(@PathVariable int grp_id,@RequestBody Grp grp){
		logger.info("Update this grp : "+grp);
		grp = grpService.updateGrpById(grp_id,grp);
		return grp;
	}
	
	@PutMapping("/update/by-name/{grp_name}")
	public ResponseEntity<?> updateGrpByName(@PathVariable String grp_name,@RequestBody Grp grp){
		logger.info("Update this grp : "+grp);
		grp = grpService.updateGrpByName(grp_name,grp);
		return ResponseEntity.ok("Grp Updated Successfully");
	}
	
	@DeleteMapping("/delete/by-id/{grp_id}")
	public Grp deleteGrpById(@PathVariable int grp_id){
		Grp grp = grpService.deleteGrpById(grp_id);
		return grp;
	}
	
	@DeleteMapping("/delete/by-name/{grp_name}")
	public Grp deleteGrpByName(@PathVariable String grp_name){
		Grp grp = grpService.deleteGrpByName(grp_name);
		return grp;
	}
	
}
