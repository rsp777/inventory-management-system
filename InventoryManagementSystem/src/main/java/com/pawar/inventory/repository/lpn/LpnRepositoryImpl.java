package com.pawar.inventory.repository.lpn;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pawar.inventory.constants.AsnStatusConstants;
import com.pawar.inventory.constants.LpnFacilityStatusContants;
import com.pawar.inventory.exceptions.CategoryNotFoundException;
import com.pawar.inventory.exceptions.ItemNotFoundException;
import com.pawar.inventory.exceptions.LpnNotFoundException;
import com.pawar.inventory.model.ASN;
import com.pawar.inventory.model.Category;
import com.pawar.inventory.model.Inventory;
import com.pawar.inventory.model.Item;
import com.pawar.inventory.model.Location;
import com.pawar.inventory.model.Lpn;
import com.pawar.inventory.repository.asn.ASNRepository;
import com.pawar.inventory.repository.inventory.InventoryRepository;
import com.pawar.inventory.repository.item.ItemRepository;
import com.pawar.inventory.repository.item.ItemRepositoryImpl;
import com.pawar.inventory.repository.location.LocationRepository;
import com.pawar.inventory.service.InventoryService;
import com.pawar.inventory.service.LocationService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

@Repository
public class LpnRepositoryImpl implements LpnRepository {

	private final static Logger logger = Logger.getLogger(LpnRepositoryImpl.class.getName());
	private EntityManager entityManager;

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private LocationRepository locationRepository;
	
	@Autowired
	private ASNRepository asnRepository;

	public LpnRepositoryImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public Lpn createLpn(Lpn lpn, Item item)
			throws ItemNotFoundException, CategoryNotFoundException, LpnNotFoundException {

		Item fetchedItem = itemRepository.findItemByname(item.getItem_name());
		Lpn fetchedLpn = getLpnByName(lpn.getLpn_name());
		logger.info("" + fetchedItem);
		logger.info("fetchedLpn : " + fetchedLpn);
		if (fetchedItem.getItem_name() == null || fetchedItem.getDescription() == null
				|| fetchedItem.getCategory() == null) {
			logger.info("Item name, description and category cannot be null.");

		}

		if (fetchedItem.getUnit_length() <= 0 || fetchedItem.getUnit_width() <= 0 || fetchedItem.getUnit_height() <= 0
				|| fetchedItem.getUnit_volume() <= 0) {
			logger.info("Item dimensions and volume must be greater than zero.");

		}
		Session currentSession = entityManager.unwrap(Session.class);
		Query<Lpn> query = currentSession.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1", Lpn.class);
		query.executeUpdate();

		if (fetchedLpn != null && (fetchedLpn.getLpn_facility_status() == LpnFacilityStatusContants.CONSUMED_TO_ACTIVE
				|| fetchedLpn.getLpn_facility_status() == LpnFacilityStatusContants.CANCELLED)) {

			fetchedLpn.setItem(fetchedItem);
			fetchedLpn.setQuantity(lpn.getQuantity());
			setLpnStatus(fetchedLpn);
			fetchedLpn.setLength(fetchedItem.getUnit_length());
			fetchedLpn.setWidth(fetchedItem.getUnit_width());
			fetchedLpn.setHeight(fetchedItem.getUnit_height());
			fetchedLpn.setVolume(fetchedItem.getUnit_volume() * fetchedLpn.getQuantity());
			fetchedLpn.setCreated_source("IMS");
			fetchedLpn.setCreated_dttm(LocalDateTime.now());
			fetchedLpn.setLast_updated_dttm(LocalDateTime.now());
			fetchedLpn.setLast_updated_source("IMS");

			currentSession.merge(fetchedLpn);

			logger.info("Lpn successfully updated : " + fetchedLpn);
			if (fetchedLpn.getLpn_facility_status() == LpnFacilityStatusContants.CREATED) {
				inventoryRepository.createInventory(fetchedLpn, currentSession);
				logger.info("Inventory successfully created : " + fetchedLpn);
			}

		} else {
			lpn.setItem(fetchedItem);
			setLpnStatus(lpn);
			lpn.setLength(fetchedItem.getUnit_length());
			lpn.setWidth(fetchedItem.getUnit_width());
			lpn.setHeight(fetchedItem.getUnit_height());
			lpn.setVolume(fetchedItem.getUnit_height());
			lpn.setCreated_source("IMS");
			lpn.setCreated_dttm(LocalDateTime.now());
			lpn.setLast_updated_dttm(LocalDateTime.now());
			lpn.setLast_updated_source("IMS");

			logger.info("Lpn data : " + lpn);

			currentSession.saveOrUpdate(lpn);

			logger.info("Lpn successfully added : " + lpn);
			if (lpn.getLpn_facility_status() == LpnFacilityStatusContants.CREATED) {
				inventoryRepository.createInventory(lpn, currentSession);
				logger.info("Inventory successfully created : " + lpn);
			}				
		}

		return lpn;

	}
	

	private void setLpnStatus(Lpn lpn) {
		if (lpn.getAsn() != null && lpn.getAsn().getAsnStatus() == AsnStatusConstants.IN_TRANSIT) {

			lpn.setLpn_facility_status(LpnFacilityStatusContants.IN_TRANSIT);

		} else {
			lpn.setLpn_facility_status(LpnFacilityStatusContants.CREATED);

		}
	}

	@Override
	public Iterable<Lpn> getfindAllLpns() {
		Session currentSession = entityManager.unwrap(Session.class);
		Query<Lpn> query = currentSession.createQuery("from Lpn", Lpn.class);
		logger.info("Query : " + query.toString());

		List<Lpn> listLpns = query.getResultList();
		for (Iterator<Lpn> iterator = listLpns.iterator(); iterator.hasNext();) {
			Lpn lpn = (Lpn) iterator.next();
			logger.info("Lpn Data : " + lpn);
		}
		return listLpns;
	}

	@Override
	public Lpn getLpnByName(String lpn_name) throws LpnNotFoundException {
		logger.info("" + lpn_name);
		Session currentSession = entityManager.unwrap(Session.class);
		Query<Lpn> query = currentSession.createQuery("from Lpn where lpn_name = :lpn_name", Lpn.class);
		query.setParameter("lpn_name", lpn_name);

		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			// Handle the exception here
			return null;
		}
	}

	@Override
	public Lpn findLpnById(int lpn_id) {
		Session currentSession = entityManager.unwrap(Session.class);
		Query<Lpn> query = currentSession.createQuery("from Lpn where lpn_id = :lpn_id", Lpn.class);
		query.setParameter("lpn_id", lpn_id);

		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			// Handle the exception here
			return null;
		}
	}

	@Override
	public Lpn updateLpnByLpnId(int lpn_id, Lpn lpn) throws ItemNotFoundException, CategoryNotFoundException {
		Session currentSession = entityManager.unwrap(Session.class);
		Lpn existingLpn = findLpnById(lpn_id);
		Item existingItem = itemRepository.findItemByname(lpn.getItem().getItem_name());
		logger.info("Existing Lpn : " + existingLpn);
		logger.info("Existing Item : " + existingItem);
		existingLpn.setLpn_facility_status(lpn.getLpn_facility_status());
		existingLpn.setLpn_name(lpn.getLpn_name());
		existingLpn.setItem(existingItem);
		existingLpn.setQuantity(lpn.getQuantity());
		existingLpn.setLength(lpn.getLength());
		existingLpn.setWidth(lpn.getWidth());
		existingLpn.setHeight(lpn.getHeight());
		existingLpn.setVolume(lpn.getVolume());
		existingLpn.setLast_updated_dttm(LocalDateTime.now());
		existingLpn.setLast_updated_source("IMS");

		currentSession.saveOrUpdate(existingLpn);

		logger.info("Lpn updated : " + existingLpn);

		return existingLpn;
	}

	@Override
	public Lpn updateLpnByLpnBarcode(String lpn_name, Lpn lpn, int adjustQty)
			throws ItemNotFoundException, CategoryNotFoundException, LpnNotFoundException {
		Session currentSession = entityManager.unwrap(Session.class);

		Inventory existingInventory = inventoryRepository.getInventoryByLpn(lpn_name);
		Location existingLocation = null;
		Lpn existingLpn = getLpnByName(lpn_name);
		Item existingItem = itemRepository.findItemByname(lpn.getItem().getDescription());
		logger.info("Existing Lpn : " + existingLpn);
		logger.info("Existing Item : " + existingItem);
		logger.info("Existing Inventory : " + existingInventory);
//		existingLpn.setLpn_facility_status(lpn.getLpn_facility_status());
//		existingLpn.setLpn_name(lpn.getLpn_name());
//		existingLpn.setItem(existingItem);
		int qty_adjusted = existingLpn.getQuantity() - adjustQty;
		logger.info("Lpn previous quantity : " + existingLpn.getQuantity());
		logger.info("Lpn new quantity : " + qty_adjusted);

		if (qty_adjusted != 0) {

			if (existingInventory.getLocation() != null) {
				existingLocation = locationRepository
						.findLocationByBarcode(existingInventory.getLocation().getLocn_brcd());

				logger.info("Existing Location : " + existingLocation);
				existingLpn.setQuantity(qty_adjusted);
				inventoryRepository.updateInventoryQty(existingInventory, adjustQty);
				locationRepository.updateOccupiedQty(existingLocation, adjustQty);
				logger.info("Lpn, Location and Inventory quantity Inventory adjusted : " + qty_adjusted);

			} else {
				existingLpn.setQuantity(qty_adjusted);
				inventoryRepository.updateInventoryQty(existingInventory, adjustQty);
				logger.info("Lpn and Inventory quantity Inventory adjusted : " + qty_adjusted);
			}
		} else {
			logger.info("qty_adjusted : " + qty_adjusted);
			existingLpn.setLpn_facility_status(LpnFacilityStatusContants.CANCELLED);
			existingLpn.setQuantity(qty_adjusted);
			inventoryRepository.deleteByInventoryLpn(lpn_name);
		}

		existingLpn.setLength(lpn.getLength());
		existingLpn.setWidth(lpn.getWidth());
		existingLpn.setHeight(lpn.getHeight());
		existingLpn.setVolume(lpn.getVolume());
		existingLpn.setLast_updated_dttm(LocalDateTime.now());
		existingLpn.setLast_updated_source("IMS");

		currentSession.saveOrUpdate(existingLpn);
		// currentSession.saveOrUpdate(existingInventory);
		// currentSession.saveOrUpdate(existingLocation);
		logger.info("Lpn updated : " + existingLpn);
		logger.info("Inventory updated : " + existingInventory);
		logger.info("Location updated : " + existingLocation);

		return existingLpn;
	}

	@Override
	public Lpn deleteLpnByLpnId(int lpn_id) {
		Lpn lpn = findLpnById(lpn_id);
		logger.info("Lpn to delete for : " + lpn);
		Session currentSession = entityManager.unwrap(Session.class);
		currentSession.delete(lpn);
		return lpn;
	}

	@Override
	public Lpn deleteLpnByLpnBarcode(String lpn_name) throws LpnNotFoundException {
		Lpn lpn = getLpnByName(lpn_name);
		logger.info("Lpn to delete for : " + lpn);
		Session currentSession = entityManager.unwrap(Session.class);
		currentSession.delete(lpn);
		return lpn;
	}

	@Override
	public Lpn deallocateLpn(String lpnName) throws LpnNotFoundException {
		Lpn lpn = getLpnByName(lpnName);

		if (lpn.getLpn_facility_status() == LpnFacilityStatusContants.ALLOCATED) {

		}

		return null;
	}

	
}
