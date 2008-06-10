package org.londonwicket.app;

import org.apache.wicket.protocol.http.WebApplication;
import org.londonwicket.pages.GetMarkersPage;
import org.londonwicket.pages.GoogleMapsPage;

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
	}
	
	@Override
	protected void init() {
		// TODO Auto-generated method stub
		super.init();
		
		mountBookmarkablePage("/markers", GetMarkersPage.class);
		
	}
	
	/**
	 * @see wicket.Application#getHomePage()
	 */
	public Class getHomePage()
	{
		return GoogleMapsPage.class;
	}

}
