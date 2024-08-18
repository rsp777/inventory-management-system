package com.pawar.inventory.repository.inventory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.inventory.constants.LocationClassConstants;
import com.pawar.inventory.constants.LpnFacilityStatusContants;
import com.pawar.inventory.exceptions.InventoryNotFoundException;
import com.pawar.inventory.model.Inventory;
import com.pawar.inventory.model.Item;
import com.pawar.inventory.model.Location;
import com.pawar.inventory.model.Lpn;
import com.pawar.inventory.repository.item.ItemRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

@Repository
public class InventoryRepositoryImpl implements InventoryRepository {

	private final static Logger logger = LoggerFactory.getLogger(InventoryRepositoryImpl.class);
	private EntityManager entityManager;
	private final HttpClient httpClient;
	private final ObjectMapper objectMapper;

	// LpnService lpnService;
	//
	// @Autowired
	// LocationService locationService;
	
	@Autowired
	private ItemRepository itemRepository;
	

	public InventoryRepositoryImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
		httpClient = HttpClients.createDefault();
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
	}

	@Override
	public Inventory createInventory(Lpn lpn, Session currentSession) {

		logger.info("Inbound inventory : " + lpn);

		Session currentSession1 = entityManager.unwrap(Session.class);
		Query<Inventory> query = currentSession1.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1", Inventory.class);
		query.executeUpdate();

		Inventory inventory = new Inventory();
		inventory.setLpn(lpn);
		inventory.setItem(lpn.getItem());
		inventory.setOn_hand_qty(lpn.getQuantity());
		inventory.setCreated_dttm(lpn.getCreated_dttm());
		inventory.setLast_updated_dttm(lpn.getLast_updated_dttm());
		inventory.setCreated_source(lpn.getCreated_source());
		inventory.setLast_updated_source(lpn.getLast_updated_source());

		currentSession1.saveOrUpdate(inventory);
		logger.info("Successfully inserted into inventory : " + inventory);

		return inventory;
	}

	@Override
	public String createReserveInventory(Lpn lpn, Location location) throws ParseException, IOException {
		String responseResv = "";
		logger.info("Reserve inventory : " + lpn + " " + location);
		String lpnName = lpn.getLpn_name();
		String locnBrcd = location.getLocn_brcd();
		Lpn fetchedLpn = fetchLpn(lpnName);
		Location fetchedLocation = fetchLocation(locnBrcd);
		Inventory inventory = getInventoryByLpn(lpnName);
		logger.info("Fetched Lpn" + fetchedLpn);
		logger.info("Fetched Location" + fetchedLocation);
		logger.info("Inventory fetched from : " + inventory);

		Session currentSession = currentSession();
		setForeignChecks(currentSession, 1, Inventory.class);

		boolean canFitInLocation = canFitInLocation(fetchedLpn, fetchedLocation, LocationClassConstants.RESERVE);
		if (canFitInLocation) {
			String locnClass = fetchedLocation.getLocn_class();
			updateLocation(fetchedLocation, inventory, currentSession);
			updateInventory(fetchedLpn, fetchedLocation, locnClass, currentSession);
			updateLpn(fetchedLpn, inventory, LpnFacilityStatusContants.PUTAWAY);
			logger.info("LPN to locate to a Reserve Location successfully : " + inventory.getLpn().getLpn_name());
			saveData(currentSession, fetchedLocation, fetchedLpn, inventory);

			logger.info("LPN located to a Reserve Location successfully : " + inventory);
			String invLpn = inventory.getLpn().getLpn_name();
			String resvLocn = inventory.getLocation().getLocn_brcd();
			responseResv = "LPN " + invLpn + " located to a Reserve Location successfully : " + resvLocn;
			return responseResv;
		}

		else {

			logger.info("Lpn can't fit in the Reserve Location");
			String invLpn = fetchedLpn.getLpn_name();
			String resvLocn = fetchedLocation.getLocn_brcd();
			responseResv = "LPN " + invLpn + " can't fit in the Reserve Location : " + resvLocn;
			logger.debug("LPN  : " + invLpn);
			logger.debug("Reserve Location  : " + resvLocn);
			logger.info("Response : " + responseResv);
			return responseResv;
		}

	}

	@Override
	public String createActiveInventory(Lpn lpn, Location location) throws ClientProtocolException, IOException {
		String lpnName = lpn.getLpn_name();
		Lpn fetchedLpn = fetchLpn(lpnName);
		String locnBrcd = location.getLocn_brcd();
		Location fetchedLocation = fetchLocation(locnBrcd);
		logger.info("Fetched Lpn" + fetchedLpn);
		logger.info("Fetched Location" + fetchedLocation);
		String reponseInv = "";
		int availableSpaceOnLocation = (int) (fetchedLocation.getMax_qty() - fetchedLocation.getOccupied_qty());
		if (canFitInLocation(fetchedLpn, fetchedLocation, LocationClassConstants.ACTIVE)) {

			Session currentSession = currentSession();
			setForeignChecks(currentSession, 1, Inventory.class);

			List<Inventory> existingInventories = getExistingInventories(fetchedLocation.getLocn_brcd(),
					fetchedLocation.getLocn_class(), currentSession);

			logger.info("Existing ActiveInv : " + existingInventories);
			Inventory inventory;
			if (!existingInventories.isEmpty()) {
				inventory = updateExistingInventories(fetchedLocation, existingInventories, fetchedLpn);

			} else {
				logger.info("Inventory do not exists, Creating New");
				Item item = fetchedLpn.getItem();
				String locnClass = fetchedLocation.getLocn_class();
				int lpn_qty = fetchedLpn.getQuantity();
				String created_source = "Inventory Management System";
				inventory = createNewActiveInventory(item, fetchedLocation, locnClass, lpn_qty, created_source);
			}
			updateLpn(fetchedLpn, inventory, LpnFacilityStatusContants.CONSUMED_TO_ACTIVE);
			updateLocation(fetchedLocation, inventory, currentSession);
			deleteByInventoryLpn(fetchedLpn.getLpn_name());

			logger.info("Occupied qty : " + fetchedLocation.getOccupied_qty() + "   +   " + fetchedLpn.getQuantity()
					+ "  =  " + fetchedLocation.getOccupied_qty() + fetchedLpn.getQuantity());
			saveData(currentSession, fetchedLocation, fetchedLpn, inventory);
			logger.info("LPN located to a Active Location successfully : " + inventory);

			reponseInv = "LPN " + fetchedLpn.getLpn_name() + " located to a Active Location "
					+ fetchedLocation.getLocn_brcd() + " successfully";
			return reponseInv;

		} else {

			if (availableSpaceOnLocation != 0) {

				reponseInv = "Lpn " + fetchedLpn.getLpn_name() + " quantity " + fetchedLpn.getQuantity()
						+ " exceeded the max qty of the location" + fetchedLocation.getLocn_brcd() + " Max Quantity : "
						+ fetchedLocation.getMax_qty();
				logger.info(reponseInv);
				return reponseInv;

			}
			reponseInv = "Active Location : " + fetchedLocation.getLocn_brcd() + " is full. Available Space : "
					+ availableSpaceOnLocation + " LPN Quantity : " + fetchedLpn.getQuantity();

			logger.info(reponseInv);
			return reponseInv;
		}

	}

	@Override
	public Location checkActiveInventory(Lpn lpn) {
		Session currentSession = currentSession();
		Location location = new Location();
		if (lpn != null) {
//			Item fetchedItem = itemRepository.findItemByname(null)
			Item fetchedItem = getInventoryByLpn(lpn.getLpn_name()).getItem();
			int itemId = fetchedItem.getItem_id();
			String locn_class = LocationClassConstants.ACTIVE;

			Query<Inventory> query = currentSession.createQuery(
					"from Inventory i where i.item.item_id = :item_id and i.locn_class = :locn_class", Inventory.class);
			query.setParameter("item_id", itemId);
			query.setParameter("locn_class", locn_class);
			List<Inventory> existingInventories = query.getResultList();
			Inventory inventory = query.getSingleResult();
			location = inventory.getLocation();
			return location;
		}
		return location;
	}

	public void saveData(Session currentSession, Location location, Lpn lpn, Inventory inventory) {
		currentSession.merge(location);
		currentSession.merge(lpn);
		currentSession.merge(inventory);
	}

	public void updateLocation(Location location, Inventory inventory, Session currentSession) {
		Location prevLocation = inventory.getLocation();
		String prevLocnClass = inventory.getLocn_class();
		if (prevLocnClass != null && prevLocation != null) {
			logger.info("prevLocnClass : {}", prevLocnClass);
			if (prevLocation != location) {
				prevLocation.setCurr_vol(prevLocation.getCurr_vol() - (inventory.getItem().getUnit_volume() * inventory.getOn_hand_qty()));
				prevLocation.setOccupied_qty((prevLocation.getOccupied_qty() - inventory.getOn_hand_qty()));
				location.setCurr_vol(location.getCurr_vol() + (inventory.getItem().getUnit_volume() * inventory.getOn_hand_qty()));
				location.setOccupied_qty(location.getOccupied_qty() + inventory.getOn_hand_qty());
			}
			else {
				logger.info("inventory : {}",inventory);
				location.setCurr_vol(location.getCurr_vol() + (inventory.getOn_hand_qty() * inventory.getItem().getUnit_volume()));
				location.setOccupied_qty(location.getOccupied_qty() + inventory.getOn_hand_qty());
			}
		} else {
			location.setCurr_vol(location.getCurr_vol() + inventory.getLpn().getVolume());
			location.setOccupied_qty(location.getOccupied_qty() + inventory.getOn_hand_qty());
		}
		prevLocation.setLast_updated_dttm(LocalDateTime.now());
		prevLocation.setLast_updated_source(inventory.getLast_updated_source());
		location.setLast_updated_dttm(LocalDateTime.now());
		location.setLast_updated_source(inventory.getLast_updated_source());
		logger.info("Previous Location : {}", prevLocation);
		logger.info("Updated Location : {}", location);
		currentSession.merge(prevLocation);
		currentSession.merge(location);
	}

	public void updateLpn(Lpn lpn, Inventory inventory, int lpnFacilityStatus) {
		lpn.setLpn_facility_status(lpnFacilityStatus);
		lpn.setLast_updated_dttm(LocalDateTime.now());
		lpn.setLast_updated_source(inventory.getLast_updated_source());
	}

	public Inventory createNewActiveInventory(Item item, Location fetchedLocation, String locnClass, int lpn_qty,
			String created_source) {
		Inventory inventory = new Inventory();
		inventory.setItem(item);
		inventory.setLocation(fetchedLocation);
		inventory.setLocn_class(locnClass);
		inventory.setOn_hand_qty(lpn_qty);
		inventory.setCreated_dttm(LocalDateTime.now());
		inventory.setCreated_source(created_source);
		inventory.setLast_updated_dttm(LocalDateTime.now());
		inventory.setLast_updated_source(created_source);
		return inventory;
	}

	private Inventory updateExistingInventories(Location fetchedLocation, List<Inventory> existingInventories,
			Lpn fetchedLpn) {
		Inventory inventory = existingInventories.get(0);
		logger.info("Active Inventories : " + inventory);

		boolean isSpaceAvailableOnLocation = locationSpaceCheck(fetchedLocation, fetchedLpn, inventory);
		if (isSpaceAvailableOnLocation) {
			inventory.setOn_hand_qty(inventory.getOn_hand_qty() + fetchedLpn.getQuantity());
		} else {
			logger.info("No Space for the lpn : " + fetchedLpn.getLpn_name());
		}
		return inventory;
	}

	public boolean locationSpaceCheck(Location fetchedLocation, Lpn fetchedLpn, Inventory inventory) {
		String locnClass = fetchedLocation.getLocn_class();
		String locnBrcd = fetchedLocation.getLocn_brcd();
		float max_qty = fetchedLocation.getMax_qty();
		float invn_on_hand_qty = inventory.getOn_hand_qty();
		int lpn_quantity = fetchedLpn.getQuantity();
		boolean value = (max_qty) >= (invn_on_hand_qty + lpn_quantity);
		if (value) {
			if (locnClass.equals("A")) {
				logger.info("Active Location : " + locnBrcd + " has space : " + max_qty + " - " + invn_on_hand_qty
						+ " = " + (max_qty - invn_on_hand_qty));
			} else {
				logger.info("Reserve Location : " + locnBrcd + " has space : " + max_qty + " - " + invn_on_hand_qty
						+ " = " + (max_qty - invn_on_hand_qty));
			}
		}
		return value;
	}

	public List<Inventory> getExistingInventories(String locn_brcd, String locn_class, Session currentSession) {
		Query<Inventory> query = currentSession.createQuery(
				"from Inventory i where i.location.locnBrcd = :locnBrcd and i.location.locnClass = :locnClass",
				Inventory.class);
		query.setParameter("locnBrcd", locn_brcd);
		query.setParameter("locnClass", locn_class);
		List<Inventory> existingInventories = query.getResultList();
		return existingInventories;
	}

	public Session currentSession() {
		Session currentSession = entityManager.unwrap(Session.class);
		return currentSession;
	}

	public <T> void setForeignChecks(Session currentSession, int value, Class<T> class1) {
		Query<Inventory> query = currentSession.createNativeQuery("SET FOREIGN_KEY_CHECKS = " + value + "",
				Inventory.class);
		query.executeUpdate();
	}

	public Lpn fetchLpn(String lpnName) throws ClientProtocolException, IOException {
		String serviceName = "lpns";
		Lpn mappedLpn = fetchData(serviceName, lpnName, Lpn.class);
		return mappedLpn;
	}

	public Location fetchLocation(String locnBrcd) throws ClientProtocolException, IOException {
		String serviceName = "locations";
		Location mappedLocation = fetchData(serviceName, locnBrcd, Location.class);
		return mappedLocation;
	}

	public boolean canFitInLocation(Lpn lpn, Location location, String locnClass) {
		return lpn.getLength() <= location.getLength() && lpn.getWidth() <= location.getWidth()
				&& lpn.getHeight() <= location.getHeight() && lpn.getQuantity() <= availableSpaceOnLocation(location)
				&& lpn.getVolume() <= location.getMax_volume() && location.getLocn_class().equals(locnClass)
				&& lpn.getLpn_facility_status() != LpnFacilityStatusContants.CONSUMED_TO_ACTIVE;
	}

	public int availableSpaceOnLocation(Location location) {
		int availableSpaceOnLocation = (int) (location.getMax_qty() - location.getOccupied_qty());
		return availableSpaceOnLocation;
	}

	public <T> T fetchData(String serviceName, String value, Class<T> class1)
			throws ClientProtocolException, IOException {
		String json = fetch(serviceName, value);
		logger.info("json data :" + json);
		T t = returnType(json, class1);
		return t;
	}

	public String fetch(String serviceName, String value) throws ClientProtocolException, IOException {
		String url = getUrl(serviceName) + value;
		String json = restGetCall(url);
		return json;
	}

	public <T> T returnType(String json, Class<T> class1) throws JsonMappingException, JsonProcessingException {
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		T t = objectMapper.readValue(json, class1);
		return t;
	}

	public String restGetCall(String url) throws ClientProtocolException, IOException {
		logger.info("URL :" + url);
		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		logger.info("Response : " + response.getStatusLine());
		HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		return json;
	}

	public String getUrl(String serviceName) {
		if (serviceName != null) {
			String url = "http://localhost:8085/" + serviceName + "/list/by-name/";
			return url;
		}
		return null;
	}

	@Override
	public Iterable<Inventory> viewAllInventory() {
		Session currentSession = entityManager.unwrap(Session.class);
		Query<Inventory> query = currentSession.createQuery("from Inventory", Inventory.class);
		logger.info("Query : " + query.getQueryString());

		List<Inventory> listInventories = query.getResultList();
		for (Iterator<Inventory> iterator = listInventories.iterator(); iterator.hasNext();) {
			Inventory inventory = (Inventory) iterator.next();
			logger.info("Inventory Data : " + inventory);
		}
		return listInventories;
	}

	@Override
	public Inventory getInventoryByLpn(String lpn_name) {
		logger.info("" + lpn_name);
		Session currentSession = entityManager.unwrap(Session.class);
		Query<Inventory> query = currentSession.createQuery("from Inventory i where i.lpn.lpn_name = :lpn_name",
				Inventory.class);
		query.setParameter("lpn_name", lpn_name);

		try {
			Inventory inventoryCase = query.getSingleResult();
			logger.info("inventoryCase : " + inventoryCase);
			return inventoryCase;
		} catch (NoResultException e) {
			// Handle the exception here
			return null;
		}
	}

	@Override
	public List<Inventory> getInventorybyItem(Item item) {
		logger.info("" + item);
		Session currentSession = entityManager.unwrap(Session.class);
		Query<Inventory> query = currentSession.createQuery("from Inventory i where i.item.description = :description",
				Inventory.class);
		query.setParameter("description", item.getDescription());

		try {
			return query.getResultList();
		} catch (NoResultException e) {
			// Handle the exception here
			return null;
		}
	}

	@Override
	public List<Inventory> getInventoryByLocation(Location location) {
		logger.info("" + location);
		Session currentSession = entityManager.unwrap(Session.class);
		Query<Inventory> query = currentSession.createQuery("from Inventory i where i.location.locnBrcd = :locnBrcd",
				Inventory.class);
		query.setParameter("locnBrcd", location.getLocn_brcd());

		try {
			return query.getResultList();
		} catch (NoResultException e) {
			// Handle the exception here
			return null;
		}
	}

	@Override
	public void deleteByInventoryLpn(String lpn_name) {
		logger.info("Inventory to delete for : " + lpn_name);
		Session currentSession = entityManager.unwrap(Session.class);
		Inventory inventory = getInventoryByLpn(lpn_name);
		currentSession.delete(inventory);
	}

	public Inventory updateInventoryQty(Inventory inventory, int adjustQty) {

		logger.info("Inventory : " + inventory);
		logger.info("Quantity To be Adjusted: " + adjustQty);
		Session currentSession1 = entityManager.unwrap(Session.class);
		Query<Inventory> query = currentSession1.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1", Inventory.class);
		query.executeUpdate();

		inventory.setOn_hand_qty(inventory.getOn_hand_qty() - adjustQty);
		logger.info("Inventory New Quantity : " + inventory.getOn_hand_qty());
		currentSession1.saveOrUpdate(inventory);
		logger.info("Successfully updated quantity into inventory : " + inventory);
		return inventory;
	}

	@Override
	public void updateInventory(Lpn lpn, Location location, String locnClass, Session currentSession) {
		Inventory fetchedInventory = getInventoryByLpn(lpn.getLpn_name());
		if (lpn != null) {
//			fetchedInventory.setLpn(lpn);
			fetchedInventory.setItem(lpn.getItem());
			fetchedInventory.setOn_hand_qty(lpn.getQuantity());
//			fetchedInventory.setCreated_dttm(lpn.getCreated_dttm());
			fetchedInventory.setLast_updated_dttm(LocalDateTime.now());
//			fetchedInventory.setCreated_source(lpn.getCreated_source());
			fetchedInventory.setLast_updated_source(lpn.getLast_updated_source());
			if (location != null && locnClass != null) {
				fetchedInventory.setLocation(location);
				fetchedInventory.setLocn_class(locnClass);
			} else {
				fetchedInventory.setLocation(null);
				fetchedInventory.setLocn_class(null);
			}
			logger.info("Updating Inventory : " + fetchedInventory);
			currentSession.merge(fetchedInventory);
			logger.info("Updated Inventory : " + fetchedInventory);
		}
	}
}
