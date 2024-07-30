package com.pawar.inventory.repository.inventory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

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

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.inventory.constants.LpnFacilityStatusContants;
import com.pawar.inventory.model.Inventory;
import com.pawar.inventory.model.Item;
import com.pawar.inventory.model.Location;
import com.pawar.inventory.model.Lpn;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

@Repository
public class InventoryRepositoryImpl implements InventoryRepository {

	private final static Logger logger = Logger.getLogger(InventoryRepositoryImpl.class.getName());
	private EntityManager entityManager;
	private final HttpClient httpClient;
	private final ObjectMapper objectMapper;

	// LpnService lpnService;
	//
	// @Autowired
	// LocationService locationService;

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
		String url = "http://localhost:8085/lpns/list/by-name/" + lpn.getLpn_name();
		logger.info("URL :" + url);
		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		Lpn fetchedLpn = objectMapper.readValue(json, Lpn.class);
		logger.info("" + fetchedLpn);

		String url2 = "http://localhost:8085/locations/list/by-name/" + location.getLocn_brcd();
		logger.info("URL :" + url2);
		HttpGet request2 = new HttpGet(url2);
		HttpResponse response2 = httpClient.execute(request2);
		logger.info("Response : " + response2.getStatusLine());
		HttpEntity entity2 = response2.getEntity();
		String json2 = EntityUtils.toString(entity2);
		logger.info("json location data :" + json2);

		objectMapper.setSerializationInclusion(Include.NON_NULL);
		Location fetchedLocation = objectMapper.readValue(json2, Location.class);
		logger.info("" + fetchedLocation);

		Session currentSession = entityManager.unwrap(Session.class);
		Query<Inventory> query = currentSession.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1", Inventory.class);
		query.executeUpdate();
		int availableSpaceOnLocation = (int) (fetchedLocation.getMax_qty() - fetchedLocation.getOccupied_qty());
		Inventory inventory = getInventoryByLpn(fetchedLpn.getLpn_name());
		logger.info("Lpn fetched from Lpn : " + inventory);

		if (fetchedLpn.getLength() <= fetchedLocation.getLength() && fetchedLpn.getWidth() <= fetchedLocation.getWidth()
				&& fetchedLpn.getHeight() <= fetchedLocation.getHeight()
				&& fetchedLpn.getQuantity() <= availableSpaceOnLocation
				&& fetchedLpn.getVolume() <= fetchedLocation.getMax_volume()
				&& fetchedLocation.getLocn_class().equals("R")
				&& fetchedLpn.getLpn_facility_status() != LpnFacilityStatusContants.CONSUMED_TO_ACTIVE) {

			inventory.setLocation(fetchedLocation);
			inventory.setLocn_class(fetchedLocation.getLocn_class());
			fetchedLpn.setLpn_facility_status(LpnFacilityStatusContants.PUTAWAY);
			fetchedLocation.setCurr_vol(fetchedLpn.getVolume() + fetchedLocation.getCurr_vol());
			fetchedLocation.setOccupied_qty(fetchedLocation.getOccupied_qty() + fetchedLpn.getQuantity());
			fetchedLocation.setLast_updated_dttm(LocalDateTime.now());
			fetchedLocation.setLast_updated_source("Lpn Management");
			logger.info("LPN to locate to a Reserve Location successfully : " + inventory.getLpn().getLpn_name());

			currentSession.merge(fetchedLocation);
			currentSession.merge(fetchedLpn);
			currentSession.merge(inventory);

			logger.info("LPN located to a Reserve Location successfully : " + inventory);
			String invLpn = inventory.getLpn().getLpn_name();
			String resvLocn = inventory.getLocation().getLocn_brcd();
			responseResv = "LPN " + invLpn + " located to a Reserve Location successfully : " + resvLocn;
//			logger.info("responseResv : "+responseResv);
			return responseResv;
		}

		else {
			logger.info("IF CONDITION : " + (fetchedLpn.getLength() <= fetchedLocation.getLength()
					&& fetchedLpn.getWidth() <= fetchedLocation.getWidth()
					&& fetchedLpn.getHeight() <= fetchedLocation.getHeight()
					&& fetchedLpn.getQuantity() <= availableSpaceOnLocation
					&& fetchedLpn.getVolume() <= fetchedLocation.getMax_volume()
					&& fetchedLocation.getLocn_class() == "R"));

			logger.info("Length : " + (fetchedLpn.getLength() <= fetchedLocation.getLength()));
			logger.info("Width : " + (fetchedLpn.getWidth() <= fetchedLocation.getWidth()));
			logger.info("Height : " + (fetchedLpn.getHeight() <= fetchedLocation.getHeight()));
			logger.info("Available Space on Location : " + (fetchedLpn.getQuantity() <= availableSpaceOnLocation));
			logger.info("Volume : " + (fetchedLpn.getVolume() <= fetchedLocation.getMax_volume()));
			logger.info("Location Class : " + (fetchedLocation.getLocn_class().equals("R")));
			logger.info("Fetched Location : " + (fetchedLocation.getLocn_class()));

			logger.info("Lpn can't fit in the Reserve Location");
			String invLpn = fetchedLpn.getLpn_name();
			String resvLocn = fetchedLocation.getLocn_brcd();
			responseResv = "LPN " + invLpn + " can't fit in the Reserve Location : " + resvLocn;
			logger.info("LPN  : " + invLpn);
			logger.info("Reserve Location  : " + resvLocn);
			logger.info("Response : " + responseResv);
			return responseResv;
		}

	}

	@Override
	public String createActiveInventory(Lpn lpn, Location location) throws ClientProtocolException, IOException {
		logger.info("Active inventory : " + lpn + " " + location);
		String url = "http://localhost:8085/lpns/list/by-name/" + lpn.getLpn_name();
		logger.info("URL :" + url);
		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		Lpn fetchedLpn = objectMapper.readValue(json, Lpn.class);
		logger.info("Fetched Lpn" + fetchedLpn);

		String url2 = "http://localhost:8085/locations/list/by-name/" + location.getLocn_brcd();
		logger.info("URL :" + url2);
		HttpGet request2 = new HttpGet(url2);
		HttpResponse response2 = httpClient.execute(request2);
		logger.info("Response : " + response2.getStatusLine());
		HttpEntity entity2 = response2.getEntity();
		String json2 = EntityUtils.toString(entity2);
		logger.info("json location data :" + json2);

		objectMapper.setSerializationInclusion(Include.NON_NULL);
		Location fetchedLocation = objectMapper.readValue(json2, Location.class);
		logger.info("" + fetchedLocation);
		String reponseInv = "";
		int availableSpaceOnLocation = (int) (fetchedLocation.getMax_qty() - fetchedLocation.getOccupied_qty());
		if (fetchedLpn.getLength() <= fetchedLocation.getLength() && fetchedLpn.getWidth() <= fetchedLocation.getWidth()
				&& fetchedLpn.getHeight() <= fetchedLocation.getHeight()
				&& fetchedLpn.getQuantity() <= availableSpaceOnLocation
				&& fetchedLpn.getVolume() <= fetchedLocation.getMax_volume()
				&& fetchedLocation.getLocn_class().equals("A") && fetchedLpn.getLpn_facility_status() != 96) {

			Session currentSession = entityManager.unwrap(Session.class);
			Query<Inventory> query = currentSession.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1", Inventory.class);
			query.executeUpdate();

			query = currentSession.createQuery(
					"from Inventory i where i.location.locnBrcd = :locnBrcd and i.location.locnClass = :locnClass",
					Inventory.class);
			query.setParameter("locnBrcd", fetchedLocation.getLocn_brcd());
			query.setParameter("locnClass", fetchedLocation.getLocn_class());

			List<Inventory> existingInventories = query.getResultList();
			logger.info("Existing ActiveInv : " + existingInventories);
			Inventory inventory;

			if (!existingInventories.isEmpty()) {
				// If an active inventory already exists, update the on_hand_qty

				inventory = existingInventories.get(0);
				logger.info("Existing Active Inventories : " + inventory);

				logger.info("Active Location : " + fetchedLocation.getLocn_brcd() + " has space : "
						+ fetchedLocation.getMax_qty() + " - " + inventory.getOn_hand_qty() + " = "
						+ (fetchedLocation.getMax_qty() - inventory.getOn_hand_qty()));
				if ((fetchedLocation.getMax_qty()) >= (inventory.getOn_hand_qty() + fetchedLpn.getQuantity())) {
					inventory.setOn_hand_qty(inventory.getOn_hand_qty() + fetchedLpn.getQuantity());
				} else {
					logger.info("No Space for the lpn : " + fetchedLpn);
				}

				logger.info("Active Inventory already exists, Updating On hand quantity");

			} else {
				// If there is no active inventory, create a new one
				logger.info("Active Inventory do not exists, Creating New");
				inventory = new Inventory();
				inventory.setItem(fetchedLpn.getItem());
				inventory.setLocation(fetchedLocation);
				inventory.setLocn_class(fetchedLocation.getLocn_class());
				inventory.setOn_hand_qty(fetchedLpn.getQuantity());
				inventory.setCreated_dttm(LocalDateTime.now());
				inventory.setCreated_source("Inventory Management System");
			}
			fetchedLpn.setLpn_facility_status(LpnFacilityStatusContants.CONSUMED_TO_ACTIVE);
			inventory.setLast_updated_dttm(LocalDateTime.now());
			inventory.setLast_updated_source("Inventory Management System");
			fetchedLocation.setCurr_vol(fetchedLpn.getVolume() + fetchedLocation.getCurr_vol());
			logger.info("Occupied qty : " + fetchedLocation.getOccupied_qty() + "   +   " + fetchedLpn.getQuantity()
					+ "  =  " + fetchedLocation.getOccupied_qty() + fetchedLpn.getQuantity());
			fetchedLocation.setOccupied_qty(fetchedLocation.getOccupied_qty() + fetchedLpn.getQuantity());
			fetchedLocation.setLast_updated_dttm(inventory.getLast_updated_dttm());
			fetchedLocation.setLast_updated_source(inventory.getLast_updated_source());

//			if (inventory.getLocation().getLocn_class().equals('R')) {
			deleteByInventoryLpn(fetchedLpn.getLpn_name());

//			}

			String url3 = "http://localhost:8085/lpns/delete/by-name/" + fetchedLpn.getLpn_name();
			logger.info("URL :" + url3);
			HttpDelete request3 = new HttpDelete(url3);
			logger.info("request3 : " + request3);
			HttpResponse response3 = httpClient.execute(request3);
			logger.info("response3 : " + response3.getEntity());

			logger.info("Response : " + response3.getStatusLine());
//			HttpEntity entity3 = response3.getEntity();
//			String json3 = EntityUtils.toString(entity3);
			logger.info("json Lpn deleted data :" + fetchedLpn.getLpn_name());

			// lpnService.deleteLpnByLpnBarcode(fetchedLpn.getLpn_name());
			currentSession.saveOrUpdate(inventory);
			currentSession.merge(fetchedLocation);
			currentSession.merge(fetchedLpn);
			logger.info("LPN located to a Active Location successfully : " + inventory);

			getInventoryByLpn(fetchedLpn.getLpn_name());
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
			logger.info("" + (fetchedLpn.getLength() <= fetchedLocation.getLength()
					&& fetchedLpn.getWidth() <= fetchedLocation.getWidth()
					&& fetchedLpn.getHeight() <= fetchedLocation.getHeight()
					&& fetchedLpn.getQuantity() <= availableSpaceOnLocation
					&& fetchedLpn.getVolume() <= fetchedLocation.getMax_volume()
					&& fetchedLocation.getLocn_class().equals("A")));

			logger.info("Length : " + (fetchedLpn.getLength() <= fetchedLocation.getLength()));
			logger.info("Width : " + (fetchedLpn.getWidth() <= fetchedLocation.getWidth()));
			logger.info("Height : " + (fetchedLpn.getHeight() <= fetchedLocation.getHeight()));
			logger.info("Quantity : " + (fetchedLpn.getQuantity() <= fetchedLocation.getMax_qty()));
			logger.info("Quantity 2: " + (fetchedLocation.getOccupied_qty() <= fetchedLocation.getMax_qty()));
			logger.info("Volume : " + (fetchedLpn.getVolume() <= fetchedLocation.getMax_volume()));
			logger.info("Location Class : " + (fetchedLocation.getLocn_class().equals("A")));
			logger.info("Fetched Location : " + (fetchedLocation.getLocn_class()));
			return reponseInv;
		}

	}

	@Override
	public Iterable<Inventory> viewAllInventory() {
		Session currentSession = entityManager.unwrap(Session.class);
		Query<Inventory> query = currentSession.createQuery("from Inventory", Inventory.class);
		logger.info("Query : " + query.toString());

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
	public void updateInventory(Lpn lpn) {
		Inventory fetchedInventory = getInventoryByLpn(lpn.getLpn_name());
		Session currentSession1 = entityManager.unwrap(Session.class);
		Query<Inventory> query = currentSession1.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1", Inventory.class);
		query.executeUpdate();
		if (lpn != null) {
//			fetchedInventory.setLpn(lpn);
			fetchedInventory.setItem(lpn.getItem());
			fetchedInventory.setOn_hand_qty(lpn.getQuantity());
			fetchedInventory.setCreated_dttm(lpn.getCreated_dttm());
			fetchedInventory.setLast_updated_dttm(lpn.getLast_updated_dttm());
			fetchedInventory.setCreated_source(lpn.getCreated_source());
			fetchedInventory.setLast_updated_source(lpn.getLast_updated_source());	
			fetchedInventory.setLocation(null);
			fetchedInventory.setLocn_class(null);
			logger.info("Updating Inventory : "+fetchedInventory);
			currentSession1.merge(fetchedInventory);
			logger.info("Updated Inventory : "+fetchedInventory);
		}
	}

}
