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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.inventory.model.Category;
import com.pawar.inventory.model.Inventory;
import com.pawar.inventory.model.Item;
import com.pawar.inventory.model.Location;
import com.pawar.inventory.model.Lpn;
import com.pawar.inventory.repository.location.LocationRepository;
import com.pawar.inventory.repository.lpn.LpnRepository;
import com.pawar.inventory.service.LocationService;
import com.pawar.inventory.service.LpnService;

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
	public Inventory createInventory(Lpn lpn) {

		logger.info("Inbound inventory : " + lpn);

		Session currentSession = entityManager.unwrap(Session.class);
		Query<Inventory> query = currentSession.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1", Inventory.class);
		query.executeUpdate();

		Inventory inventory = new Inventory();
		inventory.setLpn(lpn);
		inventory.setItem(lpn.getItem());
		inventory.setOn_hand_qty(lpn.getQuantity());
		inventory.setCreated_dttm(lpn.getCreated_dttm());
		inventory.setLast_updated_dttm(lpn.getLast_updated_dttm());
		inventory.setCreated_source(lpn.getCreated_source());
		inventory.setLast_updated_source(lpn.getLast_updated_source());

		currentSession.saveOrUpdate(inventory);
		logger.info("Successfully inserted into inventory : " + inventory);

		return inventory;
	}

	@Override
	public Inventory createReserveInventory(Lpn lpn, Location location) throws ParseException, IOException {
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

		Inventory inventory = getInventoryByLpn(fetchedLpn.getLpn_name());

		if (fetchedLpn.getLength() <= fetchedLocation.getLength() && fetchedLpn.getWidth() <= fetchedLocation.getWidth()
				&& fetchedLpn.getHeight() <= fetchedLocation.getHeight()
				&& fetchedLpn.getQuantity() <= fetchedLocation.getMax_qty()
				&& fetchedLpn.getVolume() <= fetchedLocation.getMax_volume()
				&& fetchedLocation.getLocn_class() == "R") {

			inventory.setLocation(fetchedLocation);
			inventory.setLocn_class(fetchedLocation.getLocn_class());
			fetchedLocation.setCurr_vol(fetchedLpn.getVolume() + fetchedLocation.getCurr_vol());
			fetchedLocation.setOccupied_qty(fetchedLpn.getQuantity());
			fetchedLocation.setLast_updated_dttm(inventory.getLast_updated_dttm());
			fetchedLocation.setLast_updated_source(inventory.getLast_updated_source());

			currentSession.update(inventory);
			currentSession.update(fetchedLocation);
			logger.info("LPN located to a Reserve Location successfully : " + inventory);
			return inventory;
		} else {
			logger.info("Lpn can't fit in the Reserve Location");
		}

		return null;
	}

	@Override
	public Inventory createActiveInventory(Lpn lpn, Location location) throws ClientProtocolException, IOException {
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

		if (fetchedLpn.getLength() <= fetchedLocation.getLength() && fetchedLpn.getWidth() <= fetchedLocation.getWidth()
				&& fetchedLpn.getHeight() <= fetchedLocation.getHeight()
				&& fetchedLpn.getQuantity() <= fetchedLocation.getMax_qty()
				&& fetchedLpn.getVolume() <= fetchedLocation.getMax_volume()
				&& fetchedLocation.getLocn_class() == "A") {

			Session currentSession = entityManager.unwrap(Session.class);
			Query<Inventory> query = currentSession.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1", Inventory.class);
			query.executeUpdate();

			query = currentSession.createQuery(
					"from Inventory i where i.location.locnBrcd = :locnBrcd and i.item.itemName = :itemName",
					Inventory.class);
			query.setParameter("locnBrcd", fetchedLocation.getLocn_brcd());
			query.setParameter("itemName", fetchedLpn.getItem().getItem_name());
			List<Inventory> existingInventories = query.getResultList();

			Inventory inventory;

			if (!existingInventories.isEmpty()) {
				// If an active inventory already exists, update the on_hand_qty

				inventory = existingInventories.get(0);
				logger.info("" + inventory);

				logger.info("Active Location : " + location.getLocn_brcd() + " has space : "
						+ (location.getMax_qty() - inventory.getOn_hand_qty()));
				inventory.setOn_hand_qty(inventory.getOn_hand_qty() + fetchedLpn.getQuantity());

				logger.info("Active Inventory already exists, Updating On hand quantity");
				return null;

			} else {
				// If there is no active inventory, create a new one
				logger.info("Active Inventory do not exists, Creating New");
				inventory = new Inventory();
				inventory.setItem(fetchedLpn.getItem());
				inventory.setLocation(fetchedLocation);
				inventory.setLocn_class(fetchedLocation.getLocn_class());
				inventory.setOn_hand_qty(inventory.getOn_hand_qty() + fetchedLpn.getQuantity());
				inventory.setCreated_dttm(LocalDateTime.now());
				inventory.setCreated_source("Inventory Management System");
			}

			inventory.setLast_updated_dttm(LocalDateTime.now());
			inventory.setLast_updated_source("Inventory Management System");
			fetchedLocation.setCurr_vol(fetchedLpn.getVolume() + fetchedLocation.getCurr_vol());
			fetchedLocation.setOccupied_qty(fetchedLpn.getQuantity());
			fetchedLocation.setLast_updated_dttm(inventory.getLast_updated_dttm());
			fetchedLocation.setLast_updated_source(inventory.getLast_updated_source());
			deleteByInventoryLpn(fetchedLpn.getLpn_name());

			String url3 = "http://localhost:8085/lpns/delete/by-name/" + fetchedLpn.getLpn_name();
			logger.info("URL :" + url3);
			HttpDelete request3 = new HttpDelete(url3);
			HttpResponse response3 = httpClient.execute(request3);
			logger.info("Response : " + response3.getStatusLine());
			HttpEntity entity3 = response3.getEntity();
			String json3 = EntityUtils.toString(entity3);
			logger.info("json Lpn deleted data :" + json3);

			// lpnService.deleteLpnByLpnBarcode(fetchedLpn.getLpn_name());
			currentSession.saveOrUpdate(inventory);
			currentSession.merge(fetchedLocation);

			logger.info("LPN located to a Active Location successfully : " + inventory);

			getInventoryByLpn(fetchedLpn.getLpn_name());

			return inventory;

		} else {
			logger.info("Lpn can't consume to Active location");
			logger.info("Active Location : " + location.getLocn_brcd() + " is full");
			// logger.info(""+(fetchedLpn.getLength() <= fetchedLocation.getLength() &&
			// fetchedLpn.getWidth() <= fetchedLocation.getWidth()
			// && fetchedLpn.getHeight() <= fetchedLocation.getHeight()
			// && fetchedLpn.getQuantity() <= fetchedLocation.getMax_qty()
			// && fetchedLpn.getVolume() <= fetchedLocation.getMax_volume()
			// && fetchedLocation.getLocn_class() == "A"));

			// logger.info("Length : "+(fetchedLpn.getLength() <=
			// fetchedLocation.getLength()));
			// logger.info("Width : "+(fetchedLpn.getWidth() <=
			// fetchedLocation.getWidth()));
			// logger.info("Height : "+(fetchedLpn.getHeight() <=
			// fetchedLocation.getHeight()));
			// logger.info("Quantity : "+(fetchedLpn.getQuantity() <=
			// fetchedLocation.getMax_qty()));
			// logger.info("Volume : "+(fetchedLpn.getVolume() <=
			// fetchedLocation.getMax_volume()));
			// logger.info("Location Class :
			// "+(fetchedLocation.getLocn_class().equals("A")));
			// logger.info("Fetched Location : "+(fetchedLocation.getLocn_class()));
		}

		return null;
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
			return query.getSingleResult();
		} catch (NoResultException e) {
			// Handle the exception here
			return null;
		}
	}

	@Override
	public List<Inventory> getInventorybyItem(Item item) {
		logger.info("" + item);
		Session currentSession = entityManager.unwrap(Session.class);
		Query<Inventory> query = currentSession.createQuery("from Inventory i where i.item.itemName = :itemName",
				Inventory.class);
		query.setParameter("itemName", item.getItem_name());

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

}
