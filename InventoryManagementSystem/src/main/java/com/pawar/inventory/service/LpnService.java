package com.pawar.inventory.service;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pawar.inventory.exceptions.CategoryNotFoundException;
import com.pawar.inventory.exceptions.ItemNotFoundException;
import com.pawar.inventory.exceptions.LpnNotFoundException;
import com.pawar.inventory.model.ASN;
import com.pawar.inventory.model.Item;
import com.pawar.inventory.model.Lpn;
import com.pawar.inventory.repository.lpn.LpnRepository;

@Service
public class LpnService {

	private final static Logger logger = Logger.getLogger(LpnService.class.getName());

	@Autowired
	private LpnRepository lpnRepository;
	
	
	
	@Transactional
	public Lpn createLpn(Lpn lpn, Item item) throws ItemNotFoundException, CategoryNotFoundException, LpnNotFoundException {

		return lpnRepository.createLpn(lpn,item);

	}
	

	@Transactional
	public Lpn getLpnByName(String lpn_name) throws LpnNotFoundException {

		return lpnRepository.getLpnByName(lpn_name);
	}

	@Transactional
	public Iterable<Lpn> getfindAllLpns() {
		// TODO Auto-generated method stub
		return lpnRepository.getfindAllLpns();
	}
	
	@Transactional
	public Lpn findLpnById(int lpn_id) {
		// TODO Auto-generated method stub
		return lpnRepository.findLpnById(lpn_id);
	}
	
	@Transactional
	public Lpn updateLpnByLpnId(int lpn_id, Lpn lpn) throws ItemNotFoundException, CategoryNotFoundException {
		// TODO Auto-generated method stub
		return lpnRepository.updateLpnByLpnId(lpn_id,lpn);
	}
	
	@Transactional
	public Lpn updateLpnByLpnBarcode(String lpn_name, Lpn lpn,int adjustQty) throws ItemNotFoundException, CategoryNotFoundException, LpnNotFoundException {
		// TODO Auto-generated method stub
		return lpnRepository.updateLpnByLpnBarcode(lpn_name,lpn,adjustQty);
	}
	
	@Transactional
	public Lpn deleteLpnByLpnId(int lpn_id) {
		// TODO Auto-generated method stub
		return lpnRepository.deleteLpnByLpnId(lpn_id);
	}
	
	@Transactional
	public Lpn deleteLpnByLpnBarcode(String lpn_name) throws LpnNotFoundException {
		logger.info(lpn_name);
		return lpnRepository.deleteLpnByLpnBarcode(lpn_name);
	}
	
	@Transactional
	public Lpn deallocateLpn(String lpnName) throws LpnNotFoundException {
		// TODO Auto-generated method stub
		return lpnRepository.deallocateLpn(lpnName);
	}

}
