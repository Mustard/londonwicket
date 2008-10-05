package org.wicketwarp.config.modules;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.wicketwarp.data.dao.impl.EventDaoImp;
import org.wicketwarp.data.dao.interfaces.EventDao;
import org.wicketwarp.data.dataobjects.Event;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.wideplay.warp.persist.PersistenceService;
import com.wideplay.warp.persist.TransactionStrategy;
import com.wideplay.warp.persist.UnitOfWork;

public class Module extends AbstractModule {

	@Override
	protected void configure() {

		//warp persist stuff
		install(PersistenceService.usingHibernate()
				.across(UnitOfWork.REQUEST)
				.transactedWith(TransactionStrategy.LOCAL)
				.buildModule());
		bind(Initializer.class).asEagerSingleton();

		////hibernate stuff
		AnnotationConfiguration annotationConfiguration = new AnnotationConfiguration();
		annotationConfiguration.configure();
		annotationConfiguration.addAnnotatedClass(Event.class);
		bind(Configuration.class).toInstance(annotationConfiguration);

		//dao stuff
		bind(EventDao.class).to(EventDaoImp.class);
		
		//bind a constant
		bindConstant().annotatedWith(Welcome.class).to("Hi there, welcome to Warped Wicket");
		
	}
	@Singleton
	public static class Initializer {
		@Inject
		Initializer(com.wideplay.warp.persist.PersistenceService service) {
			service.start();
		}
	}
}
