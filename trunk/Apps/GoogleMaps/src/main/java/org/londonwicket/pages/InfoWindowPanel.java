package org.londonwicket.pages;


import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

/**
 * A simple panel which will be used as the the content of the info window
 */
public class InfoWindowPanel extends Panel {

	public InfoWindowPanel(String id, final PropertyModel propertyModel)
	{
		super(id, propertyModel);
		
		add(new Label("title", new PropertyModel(this, "modelObject.title")));
		add(new Label("content", new PropertyModel(this, "modelObject.content")));
	}
}
