package org.londonwicket.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.internal.HtmlHeaderContainer;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.RequestUtils;
import org.londonwicket.geodata.GetLocations;
import org.londonwicket.geodata.LocationData;

/**
 * Homepage
 */
public class GoogleMapsPage extends WebPage {

	private static final long serialVersionUID = 1L;

	//this is the ajax behaviour which is called to update the info window panel
	private AbstractDefaultAjaxBehavior infoAjax;

	//model object for the info window panel
	private LocationData locationData = new LocationData();

	private final InfoWindowPanel infoPanel;

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters
	 *            Page parameters
	 */
	public GoogleMapsPage(final PageParameters parameters) {

		//you could load your api key from the database or a properties file
		add(HeaderContributor.forJavaScript("http://www.google.com/jsapi?key=ABCDEFG"));

		add(HeaderContributor.forJavaScript(GoogleMapsPage.class, "googlemaps.js"));


		infoPanel = new InfoWindowPanel("infoPanel", new PropertyModel(this, "locationData"));
		infoPanel.setOutputMarkupId(true);
		add(infoPanel);

		infoAjax = new AbstractDefaultAjaxBehavior(){

			@Override
			public void renderHead(IHeaderResponse response) {
				// TODO Auto-generated method stub
				super.renderHead(response);
				
				String fn = "function updateInfoWindow(marker) { " + getCallbackScript() + "};";
				System.out.println(fn);
				response.renderJavascript(fn, "repaintWindow");
			}
			
			public CharSequence getCallbackUrl(boolean onlyTargetActivePage) {
				String suffix = "&id=' + marker[\"id\"] + '";
				return super.getCallbackUrl(onlyTargetActivePage) + suffix;
			}

			@Override
			protected CharSequence getSuccessScript() {
				return "marker.openInfoWindowHtml(document.getElementById('"+infoPanel.getMarkupId()+"').innerHTML);";
			}

			public void respond(AjaxRequestTarget target) {
				//get the request
				Request request = RequestCycle.get().getRequest();

				//get the id from the ajax request
				int id = Integer.parseInt(request.getParameter("id"));

				//update the model object
				locationData = GetLocations.getLocationData(id);

				//refresh the info window
				target.addComponent(infoPanel);
			}

		};
		infoPanel.add(infoAjax);
		
	}
	
	@Override
	public void renderHead(HtmlHeaderContainer container) {
		super.renderHead(container);
		
		//output the get markers page url into the javascript so it doesnt have to be hard coded
		container.getHeaderResponse().renderJavascript("var markersUrl = '" + urlFor(GetMarkersPage.class, null) + "';", "getMarkersPageUrl");
	}

}
