package org.wicketwarp.data.dao.interfaces;

import java.io.Serializable;
import java.util.List;

import org.wicketwarp.data.dataobjects.DomainObject;

public interface Dao<T extends DomainObject>
{
	void delete(T o);

	T load(Serializable id);

	void save(T o);

	List<T> findAll();

	int countAll();
}

