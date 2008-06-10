package org.londonwicket.pages;

import java.io.IOException;
import java.util.List;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.html.WebPage;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.londonwicket.geodata.GetLocations;
import org.londonwicket.geodata.LocationData;

/**
 * This page is called by the ajax request in javascript and returns xml content
 * for the markers on the page.
 * 
 */
public class GetMarkersPage extends WebPage {

	public GetMarkersPage(final PageParameters params)
	{
	    double north = params.getDouble("north");
	    double south = params.getDouble("south");
	    double east = params.getDouble("east");
	    double west = params.getDouble("west");
	    
	    GetLocations getLoc = new GetLocations();
	    
	    List<LocationData> markerLocs = getLoc.getLocations(south, east, north, west);
	    
	    //make new document
	    final Document markers = new Document();
	    
	    Element root = new Element("xml");
	    
	    markers.setRootElement(root);
	    
	    for(LocationData aLoc : markerLocs)
	    {
	    	Element marker = new Element("marker");
	    	marker.setAttribute("lat", Double.toString(aLoc.getLat()));
	    	marker.setAttribute("lng", Double.toString(aLoc.getLng()));
	    	marker.setAttribute("id", Integer.toString(aLoc.getId()));
	    	root.addContent(marker);
	    }
	    
	    //document now constructed, output the contents of the document
	    getRequestCycle().setRequestTarget(new IRequestTarget() {
	    	@Override
	    	public void detach(RequestCycle requestCycle) {
	    	}
	    	@Override
	    	public void respond(RequestCycle requestCycle) {
	    		XMLOutputter output = new XMLOutputter();
	    		try {
					output.output(markers, requestCycle.getResponse().getOutputStream());
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
	    });
	}
}
