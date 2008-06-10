package org.londonwicket.osiv;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.wicket.Page;
import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.londonwicket.osiv.jpa.JpaThreadLocal;
import org.londonwicket.osiv.jpa.PersistenceManagerFactory;
import org.londonwicket.osiv.jpa.PersistenceUnit;



public class DataRequestCycle extends WebRequestCycle {
	public DataRequestCycle(WebApplication application,
			WebRequest request, Response response) {
		super(application, request, response);
	}

	private EntityTransaction transaction;
	private EntityManager entityManager;
	private boolean isException = false;

	@Override
	protected void onBeginRequest() {
		super.onBeginRequest();
		entityManager = PersistenceManagerFactory.getInstance().getEntityManager(PersistenceUnit.unitName);
		transaction = entityManager.getTransaction();
		transaction.begin();
		JpaThreadLocal.set(entityManager);	
	}
	

	public void onSubmit() {
		if (!isException) {
			transaction.commit();
			entityManager.clear();
			transaction = entityManager.getTransaction();
			transaction.begin();		
		}
	}

	@Override
	protected void onEndRequest() {
		super.onEndRequest();
		if(!isException){
			transaction.commit();
			entityManager.close();
		}
	}
	
	@Override
	public Page onRuntimeException(Page page, RuntimeException e) {
		e.printStackTrace();
		isException = true;
		transaction.rollback();
		entityManager.close();	
		
		return super.onRuntimeException(page, e);
	}

}
