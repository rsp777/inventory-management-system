package com.pawar.inventory.service;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pawar.inventory.controller.GrpController;
import com.pawar.inventory.exceptions.CategoryNotFoundException;
import com.pawar.inventory.exceptions.GrpAlreadyExistsException;
import com.pawar.inventory.exceptions.GrpNotFoundException;
import com.pawar.inventory.model.Category;
import com.pawar.inventory.model.Grp;
import com.pawar.inventory.repository.category.CategoryRepository;
import com.pawar.inventory.repository.grp.GrpRepository;

@Service
public class GrpService {

	private final static Logger logger = Logger.getLogger(GrpService.class.getName());

	@Autowired
	private GrpRepository grpRepository;

	@Autowired
	public GrpService(GrpRepository grpRepository) {
		this.grpRepository = grpRepository;
	}

	@Transactional
	public Grp createGrp(Grp grp) throws GrpAlreadyExistsException {
		Grp newGrp = new Grp();
		newGrp.setGrpDesc(grp.getGrpDesc());
		return grpRepository.createGrp(newGrp);
	}

	@Transactional
	public Iterable<Grp> getfindAllGrps() {
		return grpRepository.getfindAllGrps();
	}

	@Transactional
	public Grp getGrpByName(String grp_name) {
		return grpRepository.getGrpByName(grp_name);
	}

	@Transactional
	public Grp getGrpById(int grp_id) {
		return grpRepository.getGrpById(grp_id);
	}

	@Transactional
	public Grp updateGrpById(int grp_id, Grp grp) {
		return grpRepository.updateGrpById(grp_id, grp);
	}

	@Transactional
	public Grp updateGrpByName(String grp_name, Grp grp) {
		return grpRepository.updateGrpByName(grp_name, grp);
	}

	@Transactional
	public Grp deleteGrpById(int grp_id) {
		return grpRepository.deleteGrpById(grp_id);
	}

	@Transactional
	public Grp deleteGrpByName(String grp_name) {
		return grpRepository.deleteGrpByName(grp_name);
	}

	public boolean validateGrp(Grp grp) throws GrpNotFoundException {
		String grpName = grp.getGrpName();
		logger.info("Grp to validate : " + grpName);
		if (grpName != null) {
			return true;
		} else {
			return false;
		}
	}
}
