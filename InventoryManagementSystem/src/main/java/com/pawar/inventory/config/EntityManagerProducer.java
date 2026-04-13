package com.pawar.inventory.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class EntityManagerProducer {

	@Produces
	@PersistenceContext(unitName = "InventoryPU")
	private EntityManager entityManager;
}
