package org.londonwicket.osiv.jpa;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class PersistenceManagerFactory {
	private Map<String, EntityManagerFactory> entityManagerFactories = new HashMap<String, EntityManagerFactory>();
	private static PersistenceManagerFactory _instance;

	private PersistenceManagerFactory() {

	}

	public static synchronized PersistenceManagerFactory getInstance() {
		if (_instance == null)
			_instance = new PersistenceManagerFactory();
		return _instance;
	}

	public EntityManager getEntityManager(String persistenceUnit) {
		EntityManagerFactory factory = entityManagerFactories
				.get(persistenceUnit);
		if (factory == null || !factory.isOpen()) {
			factory = Persistence.createEntityManagerFactory(persistenceUnit);
			synchronized (entityManagerFactories) {
				entityManagerFactories.put(persistenceUnit, factory);
			}
		}
		EntityManager manager = factory.createEntityManager();
		return manager;
	}

}
