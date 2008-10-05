package org.wicketwarp.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.wicketwarp.config.modules.Welcome;
import org.wicketwarp.data.dao.interfaces.EventDao;
import org.wicketwarp.data.dataobjects.Event;

import com.google.inject.Inject;

public class EventPage extends WebPage {
	
	@Inject
	private EventDao eventDao;
	
	@Inject @Welcome String welcome;

	public EventPage(final PageParameters pp)
	{
		add(new Label("label", welcome));
		
		Form eventForm = new Form("eventForm", new CompoundPropertyModel(new Event()));
		eventForm.add(new TextField("title").setRequired(true));
		eventForm.add(new DateTextField("date").setRequired(true).add(new DatePicker()));
		
		final WebMarkupContainer wmc = new WebMarkupContainer("listContainer");
		
		wmc.add(new ListView("list", new PropertyModel(this, "eventDao.findAll")){

			@Override
			protected void populateItem(ListItem item) {
				Event event = (Event) item.getModelObject();
				item.add(new Label("eventName", event.getTitle()));
				item.add(new Label("eventDate", event.getDate().toString()));
			}
			
		});
		wmc.setOutputMarkupId(true);
		add(wmc);
		
		eventForm.add(new AjaxSubmitLink("submit"){
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				Event event = (Event) form.getModelObject();
				Event newEvent = new Event();
				newEvent.setDate(event.getDate());
				newEvent.setTitle(event.getTitle());
				eventDao.save(newEvent);
				target.addComponent(wmc);
			}
		});
		
		
		add(eventForm);
		
	}
}
