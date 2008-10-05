package org.wicketwarp.data.dao.impl;

import org.wicketwarp.data.dao.interfaces.EventDao;
import org.wicketwarp.data.dataobjects.Event;

public class EventDaoImp extends AbstractDaoImpl<Event> implements EventDao {

	public EventDaoImp()
	{
		super(Event.class);
	}
}
