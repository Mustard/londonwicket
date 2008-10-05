package org.wicketwarp.data.dao.impl;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.wicketwarp.data.dao.interfaces.Dao;
import org.wicketwarp.data.dataobjects.DomainObject;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.wideplay.warp.persist.Transactional;

@SuppressWarnings("unchecked")
public abstract class AbstractDaoImpl<T extends DomainObject> implements Dao<T> {
	
	private Class<T> domainClass;
	
	@Inject
	Provider<Session> session;

	public AbstractDaoImpl(Class<T> domainClass) {
		this.domainClass = domainClass;
	}
	
	@Transactional
	public void delete(T object)
	{ 
		session.get().delete(object);
	}
	
	public T load(Serializable id)
	{
		return (T) session.get().get(domainClass, id);
	}
	
	@Transactional
	public void save(T object)
	{
		session.get().saveOrUpdate(object);
	}
	
	public List<T> findAll()
	{
		Criteria criteria = session.get().createCriteria(domainClass);
		return (List<T>) criteria.list();
	}
	
	public int countAll() {
		Criteria criteria = session.get().createCriteria(domainClass);
		criteria.setProjection(Projections.rowCount());
		return (Integer) criteria.uniqueResult();
	}
	
}

