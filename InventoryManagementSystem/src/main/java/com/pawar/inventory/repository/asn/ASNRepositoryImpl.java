package com.pawar.inventory.repository.asn;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.pawar.inventory.constants.AsnStatusConstants;
import com.pawar.inventory.constants.LpnFacilityStatusContants;
import com.pawar.inventory.entity.ASNDto;
import com.pawar.inventory.exceptions.ASNNotFoundException;
import com.pawar.inventory.exceptions.CategoryNotFoundException;
import com.pawar.inventory.exceptions.ItemNotFoundException;
import com.pawar.inventory.exceptions.LpnNotFoundException;
import com.pawar.inventory.model.ASN;
import com.pawar.inventory.model.Inventory;
import com.pawar.inventory.model.Lpn;
import com.pawar.inventory.repository.inventory.InventoryRepository;
import com.pawar.inventory.repository.lpn.LpnRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

@Repository
public class ASNRepositoryImpl implements ASNRepository {

	private final static Logger logger = LoggerFactory.getLogger(ASNRepositoryImpl.class);
	private EntityManager entityManager;

	@Autowired
	@Lazy
	LpnRepository lpnRepository;

	@Autowired
	InventoryRepository inventoryRepository;

	public ASNRepositoryImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public ASN createASN(ASN asn) throws ItemNotFoundException, CategoryNotFoundException, LpnNotFoundException,
			NoResultException, ASNNotFoundException {
		List<Lpn> lpns = asn.getLpns();
		boolean isASNPresent = isASNPresent(asn);
		Session currentSession = entityManager.unwrap(Session.class);
		logger.info("isASNPresent : {}", isASNPresent);
		if (!isASNPresent) {
			boolean isLpnsPresent = isLpnsPresent(lpns);
			if (!isLpnsPresent) {

				asn.setAsnStatus(AsnStatusConstants.IN_TRANSIT);
				ASN savedASN = currentSession.merge(asn);
				for (Lpn lpn : lpns) {
					lpn.setAsn(asn);
					lpnRepository.createLpn(lpn, lpn.getItem(), asn.getAsnBrcd());
				}
				return savedASN;
			} else {
				logger.error("Lpns are already present is WMS : {}", lpns);
			}

		} else {
			logger.error("ASN is already present is WMS : {}", asn.getAsnBrcd());
		}
		return null;
	}

	@Override
	public ASN getASNByName(String asnBrcd) throws NoResultException, ASNNotFoundException {
		logger.info("asnBrcd : {}", asnBrcd);
		Session currentSession = entityManager.unwrap(Session.class);
		Query<ASN> query = currentSession.createQuery("from ASN where asnBrcd = :asnBrcd", ASN.class);
		query.setParameter("asnBrcd", asnBrcd);
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public boolean isASNPresent(ASN asn) throws NoResultException, ASNNotFoundException {
		if (asn == null) {
			return false;
		} else {
			String asnBrcd = asn.getAsnBrcd();
			ASN fetchedASN = getASNByName(asnBrcd);
			logger.info("fetchedASN : {}", fetchedASN);
			if (fetchedASN != null) {
				return true;
			}
		}
		return false;
	}

	public boolean isLpnsPresent(List<Lpn> lpns) throws LpnNotFoundException, NullPointerException {
		if (lpns == null || lpns.isEmpty()) {
			return false;
		}
		for (Lpn lpn : lpns) {
			if (lpn.getLpn_name() == null) {
				continue;
			}

			Lpn fetchedLpn = lpnRepository.getLpnByName(lpn.getLpn_name());
			if (fetchedLpn == null) {
				return false;
			}

		}
		return true;
	}

	@Override
	public String receiveAsn(ASN asn, List<Lpn> lpns)
			throws LpnNotFoundException, NoResultException, ASNNotFoundException {
		Session currentSession = entityManager.unwrap(Session.class);
		String response = "";
		if (asn.getAsnBrcd() != "" && asn.getAsnBrcd() != null && lpns != null) {
			ASN fetchedASN = getASNByName(asn.getAsnBrcd());
			if (fetchedASN.getAsnStatus() == AsnStatusConstants.IN_TRANSIT) {
				for (Lpn lpn : lpns) {
					Lpn fetchedLpn = lpnRepository.getLpnByName(lpn.getLpn_name());
					if (fetchedLpn.getLpn_facility_status() == LpnFacilityStatusContants.IN_TRANSIT) {
						fetchedLpn.setLpn_facility_status(LpnFacilityStatusContants.CREATED);
						fetchedLpn.setLast_updated_dttm(LocalDateTime.now());
						fetchedLpn.setLast_updated_source("Receiving");
						currentSession.merge(fetchedLpn);
						inventoryRepository.createInventory(fetchedLpn, currentSession);
					}
				}
				fetchedASN.setAsnStatus(AsnStatusConstants.RECEIVED);
				fetchedASN.setLast_updated_dttm(LocalDateTime.now());
				fetchedASN.setLast_updated_source("Receiving");

				currentSession.merge(fetchedASN);

				logger.info("ASN received successfully : {}", fetchedASN.getAsnBrcd());
				logger.debug("ASN received successfully : {}", fetchedASN);

				response = "ASN received successfully";
				return response;
			} else {
				logger.info("ASN is in incorrect status : {}", fetchedASN.getAsnStatus());
				response = "ASN is in incorrect status";
				return response;
			}
		} else {
			response = "ASN or Lpn is null";
			return response;
		}
	}

	@Override
	public List<ASN> getAsnByCategory(String category) {
		logger.info("Get ASN of Category : {}", category);
		Session currentSession = entityManager.unwrap(Session.class);
		Query<ASN> query = currentSession.createQuery(
				"from ASN a join fetch a.lpns l join fetch l.item i where i.category.categoryName = :category and l.asn_brcd is not null",
				ASN.class);
		query.setParameter("category", category);
		List<ASN> asnList = query.getResultList();

		try {
			return asnList;
		} catch (NoResultException e) {
			return null;
		}
	}

//
//	@Override
//	public Iterable<Lpn> getfindAllLpns() {
//		Session currentSession = entityManager.unwrap(Session.class);
//		Query<Lpn> query = currentSession.createQuery("from Lpn", Lpn.class);
//		logger.info("Query : " + query.toString());
//
//		List<Lpn> listLpns = query.getResultList();
//		for (Iterator<Lpn> iterator = listLpns.iterator(); iterator.hasNext();) {
//			Lpn lpn = (Lpn) iterator.next();
//			logger.info("Lpn Data : " + lpn);
//		}
//		return listLpns;
//	}
//
//	@Override
//	public Lpn getLpnByName(String lpn_name) throws LpnNotFoundException {
//		logger.info("" + lpn_name);
//		Session currentSession = entityManager.unwrap(Session.class);
//		Query<Lpn> query = currentSession.createQuery("from Lpn where lpn_name = :lpn_name", Lpn.class);
//		query.setParameter("lpn_name", lpn_name);
//
//		try {
//			return query.getSingleResult();
//		} catch (NoResultException e) {
//			// Handle the exception here
//			return null;
//		}
//	}
//
//	@Override
//	public Lpn findLpnById(int lpn_id) {
//		Session currentSession = entityManager.unwrap(Session.class);
//		Query<Lpn> query = currentSession.createQuery("from Lpn where lpn_id = :lpn_id", Lpn.class);
//		query.setParameter("lpn_id", lpn_id);
//
//		try {
//			return query.getSingleResult();
//		} catch (NoResultException e) {
//			// Handle the exception here
//			return null;
//		}
//	}
//
//	@Override
//	public Lpn updateLpnByLpnId(int lpn_id, Lpn lpn) throws ItemNotFoundException, CategoryNotFoundException {
//		Session currentSession = entityManager.unwrap(Session.class);
//		Lpn existingLpn = findLpnById(lpn_id);
//		Item existingItem = itemRepository.findItemByname(lpn.getItem().getItem_name());
//		logger.info("Existing Lpn : " + existingLpn);
//		logger.info("Existing Item : " + existingItem);
//		existingLpn.setLpn_facility_status(lpn.getLpn_facility_status());
//		existingLpn.setLpn_name(lpn.getLpn_name());
//		existingLpn.setItem(existingItem);
//		existingLpn.setQuantity(lpn.getQuantity());
//		existingLpn.setLength(lpn.getLength());
//		existingLpn.setWidth(lpn.getWidth());
//		existingLpn.setHeight(lpn.getHeight());
//		existingLpn.setVolume(lpn.getVolume());
//		existingLpn.setLast_updated_dttm(LocalDateTime.now());
//		existingLpn.setLast_updated_source("IMS");
//
//		currentSession.saveOrUpdate(existingLpn);
//
//		logger.info("Lpn updated : " + existingLpn);
//
//		return existingLpn;
//	}
//
//	@Override
//	public Lpn updateLpnByLpnBarcode(String lpn_name, Lpn lpn, int adjustQty)
//			throws ItemNotFoundException, CategoryNotFoundException, LpnNotFoundException {
//		Session currentSession = entityManager.unwrap(Session.class);
//
//		Inventory existingInventory = inventoryService.getInventoryByLpn(lpn_name);
//		Location existingLocation = null;
//		Lpn existingLpn = getLpnByName(lpn_name);
//		Item existingItem = itemRepository.findItemByname(lpn.getItem().getDescription());
//		logger.info("Existing Lpn : " + existingLpn);
//		logger.info("Existing Item : " + existingItem);
//		logger.info("Existing Inventory : " + existingInventory);
////		existingLpn.setLpn_facility_status(lpn.getLpn_facility_status());
////		existingLpn.setLpn_name(lpn.getLpn_name());
////		existingLpn.setItem(existingItem);
//		int qty_adjusted = existingLpn.getQuantity() - adjustQty;
//		logger.info("Lpn previous quantity : " + existingLpn.getQuantity());
//		logger.info("Lpn new quantity : " + qty_adjusted);
//
//		if (qty_adjusted != 0) {
//
//			if (existingInventory.getLocation() != null) {
//				existingLocation = locationService
//						.findLocationByBarcode(existingInventory.getLocation().getLocn_brcd());
//
//				logger.info("Existing Location : " + existingLocation);
//				existingLpn.setQuantity(qty_adjusted);
//				inventoryService.updateInventoryQty(existingInventory, adjustQty);
//				locationService.updateOccupiedQty(existingLocation, adjustQty);
//				logger.info("Lpn, Location and Inventory quantity Inventory adjusted : " + qty_adjusted);
//
//			} else {
//				existingLpn.setQuantity(qty_adjusted);
//				inventoryService.updateInventoryQty(existingInventory, adjustQty);
//				logger.info("Lpn and Inventory quantity Inventory adjusted : " + qty_adjusted);
//			}
//		} else {
//			logger.info("qty_adjusted : " + qty_adjusted);
//			existingLpn.setLpn_facility_status(LpnFacilityStatusContants.CANCELLED);
//			existingLpn.setQuantity(qty_adjusted);
//			inventoryService.deleteByInventoryLpn(lpn_name);
//		}
//
//		existingLpn.setLength(lpn.getLength());
//		existingLpn.setWidth(lpn.getWidth());
//		existingLpn.setHeight(lpn.getHeight());
//		existingLpn.setVolume(lpn.getVolume());
//		existingLpn.setLast_updated_dttm(LocalDateTime.now());
//		existingLpn.setLast_updated_source("IMS");
//
//		currentSession.saveOrUpdate(existingLpn);
//		// currentSession.saveOrUpdate(existingInventory);
//		// currentSession.saveOrUpdate(existingLocation);
//		logger.info("Lpn updated : " + existingLpn);
//		logger.info("Inventory updated : " + existingInventory);
//		logger.info("Location updated : " + existingLocation);
//
//		return existingLpn;
//	}
//
//	@Override
//	public Lpn deleteLpnByLpnId(int lpn_id) {
//		Lpn lpn = findLpnById(lpn_id);
//		logger.info("Lpn to delete for : " + lpn);
//		Session currentSession = entityManager.unwrap(Session.class);
//		currentSession.delete(lpn);
//		return lpn;
//	}
//
//	@Override
//	public Lpn deleteLpnByLpnBarcode(String lpn_name) throws LpnNotFoundException {
//		Lpn lpn = getLpnByName(lpn_name);
//		logger.info("Lpn to delete for : " + lpn);
//		Session currentSession = entityManager.unwrap(Session.class);
//		currentSession.delete(lpn);
//		return lpn;
//	}
//
//	@Override
//	public Lpn deallocateLpn(String lpnName) throws LpnNotFoundException {
//		Lpn lpn = getLpnByName(lpnName);
//
//		if (lpn.getLpn_facility_status() == LpnFacilityStatusContants.ALLOCATED) {
//
//		}
//
//		return null;
//	}

}
