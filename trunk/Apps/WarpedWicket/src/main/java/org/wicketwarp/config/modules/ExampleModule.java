package org.wicketwarp.config.modules;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.wicketwarp.data.dao.impl.EventDaoImp;
import org.wicketwarp.data.dao.interfaces.EventDao;
import org.wicketwarp.data.dataobjects.Event;

import com.google.inject.AbstractModule;

public class ExampleModule extends AbstractModule {

	@Override
	protected void configure() {
		
		////hibernate stuff
		AnnotationConfiguration annotationConfiguration = new AnnotationConfiguration();
		annotationConfiguration.configure();
		annotationConfiguration.addAnnotatedClass(Event.class);
		
		//bind Hibernate configuration
		bind(Configuration.class).toInstance(annotationConfiguration);

		//bind DAO
		bind(EventDao.class).to(EventDaoImp.class);
		
	}
}
