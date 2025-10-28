package com.pawar.inventory.repository.grp;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.pawar.inventory.exceptions.GrpAlreadyExistsException;
import com.pawar.inventory.model.Grp;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

@Repository
public class GrpRepositoryImpl implements GrpRepository {

	private EntityManager entityManager;
	private final Logger logger = LoggerFactory.getLogger(GrpRepositoryImpl.class.getName());

	public GrpRepositoryImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public Grp createGrp(Grp grp) throws GrpAlreadyExistsException {
		Grp fetchedGrp = getGrpByDesc(grp.getGrpDesc());
		if (fetchedGrp == null) {
			String grpBarcode = generateBarcode(grp);
			Session currentSession = entityManager.unwrap(Session.class);
			Query<Grp> query = currentSession.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1", Grp.class);
			query.executeUpdate();
			grp.setGrpName(grpBarcode);
			grp.setCreatedSource("INVN-MANAGE");
			grp.setLastUpdatedSource("INVN-MANAGE");
			grp.setCreatedDttm(LocalDateTime.now());
			grp.setLastUpdatedDttm(LocalDateTime.now());
			;
			currentSession.saveOrUpdate(grp);
			logger.info("Grp added to databasee : " + grp);
		}
		else {
			logger.info("Grp already exists");
			throw new GrpAlreadyExistsException();
		}
		return grp;
	}

	@Override
	public String generateBarcode(Grp grp) {
		Map categoryIdCounters = new HashMap<>();
		if (grp == null) {
			return null;
		}

		logger.info("Grp : {}", grp);

		String grpDesc = grp.getGrpDesc();
		String prefix = grpDesc.substring(0, 2).toUpperCase();

		if (prefix == null || prefix.isEmpty()) {
			throw new IllegalArgumentException("Invalid location group: " + grpDesc);
		}

//		categoryIdCounters.putIfAbsent(prefix, 0);
//		int categoryId = (int) categoryIdCounters.get(prefix);
//		categoryIdCounters.put(prefix, categoryId + 1);

		return prefix ;//+ "-" + String.format("%03d", categoryId);
	}

	@Override
	public Iterable<Grp> getfindAllGrps() {
		Session currentSession = entityManager.unwrap(Session.class);
		Query<Grp> query = currentSession.createQuery("from Grp", Grp.class);
		List<Grp> listCategories = query.getResultList();
		logger.info("" + listCategories);
		return listCategories;
	}

	@Override
	public Grp getGrpByName(String grpName) {
		Session currentSession = entityManager.unwrap(Session.class);
		Query<Grp> query = currentSession.createQuery("from Grp where grpName = :grpName", Grp.class);
		query.setParameter("grpName", grpName);

		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			// Handle the exception here
			return null;
		}
	}
	
	@Override
	public Grp getGrpByDesc(String grpDesc) {
		Session currentSession = entityManager.unwrap(Session.class);
		Query<Grp> query = currentSession.createQuery("from Grp where grpDesc = :grpDesc", Grp.class);
		query.setParameter("grpDesc", grpDesc);

		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			// Handle the exception here
			return null;
		}
	}

	@Override
	public Grp getGrpById(int id) {
		Session currentSession = entityManager.unwrap(Session.class);
		Query<Grp> query = currentSession.createQuery("from Grp where id = :id", Grp.class);
		query.setParameter("id", id);

		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			// Handle the exception here
			return null;
		}
	}

	@Override
	public Grp updateGrpById(int id, Grp grp) {
		Session currentSession = entityManager.unwrap(Session.class);
		Grp existingGrp = getGrpById(id);
		existingGrp.setGrpName(grp.getGrpName());
		currentSession.update(existingGrp);
		logger.info("Grp updated : " + existingGrp);
		return existingGrp;
	}

	@Override
	public Grp updateGrpByName(String grpName, Grp grp) {
		Session currentSession = entityManager.unwrap(Session.class);
		Grp existingcategory = getGrpByName(grpName);
		existingcategory.setGrpName(grp.getGrpName());
		currentSession.update(existingcategory);
		logger.info("Grp updated : " + existingcategory);
		return existingcategory;
	}

	@Override
	public Grp deleteGrpById(int id) {
		Grp grp = getGrpById(id);
		logger.info("Grp to delete for : " + grp);
		Session currentSession = entityManager.unwrap(Session.class);
		currentSession.delete(grp);
		return grp;
	}

	@Override
	public Grp deleteGrpByName(String grpName) {
		Grp grp = getGrpByName(grpName);
		logger.info("Grp to delete for : " + grp);
		Session currentSession = entityManager.unwrap(Session.class);
		currentSession.delete(grp);
		return grp;
	}

}
