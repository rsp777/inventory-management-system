package com.pawar.inventory.repository.location;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pawar.inventory.model.Category;
import com.pawar.inventory.model.Item;
import com.pawar.inventory.model.Location;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

@Repository
public class LocationRepositoryImpl implements LocationRepository {

	private final static Logger logger = Logger.getLogger(LocationRepositoryImpl.class.getName());
	private EntityManager entityManager;

	public LocationRepositoryImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public Location addLocation(Location location) {
		if (location != null) {

			logger.info("Location to be created : " + location);
			if (location.getLocn_brcd() == null || location.getLocn_class() == null) {
				logger.info("Location barcode and class cannot be null.");
				return null;
			}

			if (location.getLength() <= 0 || location.getWidth() <= 0 || location.getHeight() <= 0
					|| location.getMax_volume() <= 0 || location.getMax_weight() <= 0) {
				logger.info("Location dimensions, volume and weight must be greater than zero.");
				return null;
			}

			Session currentSession = entityManager.unwrap(Session.class);
			Query<Category> query = currentSession.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1", Category.class);
			location.setCreated_dttm(LocalDateTime.now());
			location.setLast_updated_dttm(LocalDateTime.now());
			System.out.println("location.getLast_updated_source() != null : " + location.getLast_updated_source() != null);
			if (location.getLast_updated_source() != null && location.getCreated_source() != null) {
				location.setCreated_source(location.getCreated_source());
				location.setLast_updated_source(location.getLast_updated_source());
			} else {
				location.setCreated_source("Location Management");
				location.setLast_updated_source("Location Management");
			}
			query.executeUpdate();
			currentSession.saveOrUpdate(location);
			logger.info("Location Saved : "+location);
			return location;
		}
		return null;
	}

	@Override
	public Iterable<Location> getfindAlllocations() {
		Session currentSession = entityManager.unwrap(Session.class);
		Query<Location> query = currentSession.createQuery("from Location", Location.class);
		logger.info("Query : " + query.toString());
		List<Location> listLocations = query.getResultList();
		for (Iterator<Location> iterator = listLocations.iterator(); iterator.hasNext();) {
			Location location = (Location) iterator.next();
			logger.info("location Data : " + location);
		}
		return listLocations;
	}

	@Override
	public Location findLocationByBarcode(String location_name) {
		logger.info("" + location_name);
		Session currentSession = entityManager.unwrap(Session.class);
		Query<Location> query = currentSession.createQuery("from Location where locnBrcd = :locnBrcd", Location.class);
		query.setParameter("locnBrcd", location_name);

		try {
			logger.info("Query : " + query.getSingleResult());
			return query.getSingleResult();
		} catch (NoResultException e) {
			// Handle the exception here
			return null;
		}
	}

	@Override
	public Location findLocationById(int locn_id) {
		Session currentSession = entityManager.unwrap(Session.class);
		Query<Location> query = currentSession.createQuery("from Location where locn_id = :locn_id", Location.class);
		query.setParameter("locn_id", locn_id);

		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			// Handle the exception here
			return null;
		}
	}

	@Override
	public Location updateLocationByLocationId(int locn_id, Location location) {
		Session currentSession = entityManager.unwrap(Session.class);
		Location existingLocation = findLocationById(locn_id);
		logger.info("" + existingLocation);
		existingLocation.setLocn_brcd(location.getLocn_brcd());
		existingLocation.setLocn_class(location.getLocn_class());
		existingLocation.setLength(location.getLength());
		existingLocation.setWidth(location.getWidth());
		existingLocation.setHeight(location.getHeight());
		existingLocation.setCurr_vol(location.getCurr_vol());
		existingLocation.setCurr_weight(location.getCurr_vol());
		existingLocation.setMax_qty(location.getMax_qty());
		existingLocation.setMax_volume(location.getMax_volume());
		existingLocation.setMax_weight(location.getMax_weight());
		existingLocation.setOccupied_qty(location.getOccupied_qty());
		existingLocation.setLast_updated_dttm(LocalDateTime.now());
		existingLocation.setLast_updated_source("IMS");
		currentSession.saveOrUpdate(existingLocation);
		logger.info("Item updated : " + existingLocation);
		return existingLocation;
	}

	@Override
	public Location updateLocationByLocationBarcode(String locn_brcd, Location location) {
		Session currentSession = entityManager.unwrap(Session.class);
		Location existingLocation = findLocationByBarcode(locn_brcd);
		logger.info("" + existingLocation);
		existingLocation.setLocn_brcd(location.getLocn_brcd());
		existingLocation.setLocn_class(location.getLocn_class());
		existingLocation.setLength(location.getLength());
		existingLocation.setWidth(location.getWidth());
		existingLocation.setHeight(location.getHeight());
		// existingLocation.setCurr_vol(location.getCurr_vol());
		// existingLocation.setCurr_weight(location.getCurr_vol());
		existingLocation.setMax_qty(location.getMax_qty());
		existingLocation.setMax_volume(location.getMax_volume());
		existingLocation.setMax_weight(location.getMax_weight());
		// existingLocation.setOccupied_qty(location.getOccupied_qty());
		existingLocation.setLast_updated_dttm(LocalDateTime.now());
		existingLocation.setLast_updated_source("IMS");
		currentSession.saveOrUpdate(existingLocation);
		logger.info("Location updated : " + existingLocation);
		return existingLocation;
	}

	@Override
	public Location deleteLocationByLocationId(int locn_id) {
		Location location = findLocationById(locn_id);
		logger.info("Location to be delete for : " + location);
		Session currentSession = entityManager.unwrap(Session.class);
		currentSession.delete(location);
		return location;
	}

	@Override
	public Location deleteLocationByLocationBarcode(String locn_brcd) {
		Location location = findLocationByBarcode(locn_brcd);
		logger.info("Location to be delete for : " + location);
		Session currentSession = entityManager.unwrap(Session.class);
		currentSession.delete(location);
		return location;
	}
}
