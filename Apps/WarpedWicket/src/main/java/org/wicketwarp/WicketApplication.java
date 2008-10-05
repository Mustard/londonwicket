package org.wicketwarp;

import org.apache.wicket.guice.GuiceComponentInjector;
import org.apache.wicket.protocol.http.WebApplication;
import org.wicketwarp.config.modules.Module;
import org.wicketwarp.data.dataobjects.Event;

import com.google.inject.Guice;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * @see wicket.myproject.Start#main(String[])
 */
public class WicketApplication extends WebApplication
{    
	/**
	 * Constructor
	 */
	public WicketApplication()
	{
		addComponentInstantiationListener(new GuiceComponentInjector(this, Guice.createInjector(new Module())));
	}

	@Override
	protected void init() {
		super.init();

		mountBookmarkablePage("/event", Event.class);

	}

	/**
	 * @see wicket.Application#getHomePage()
	 */
	public Class getHomePage()
	{
		return HomePage.class;
	}

}
