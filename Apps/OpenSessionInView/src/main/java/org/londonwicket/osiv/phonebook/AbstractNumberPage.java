package org.londonwicket.osiv.phonebook;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.PageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

public abstract class AbstractNumberPage extends WebPage {
	private TextField numberField;

	public AbstractNumberPage(PhoneBookEntry entry){
		add(new PageLink("back", NumbersPage.class));
		add(new Label("header", getHeader()));
		Form form = getForm("form", entry);
		add(form);
		form.add(new FeedbackPanel("feedback"));
		numberField = new TextField("number", new PropertyModel(entry, "number"));
		numberField.add(new PhoneNumberValidator());
		numberField.setRequired(true);
		form.add(numberField);
		form.add(new TextField("name", new PropertyModel(entry, "name")));
		form.add(new Button("submit", new Model(getSubmitLabel())));
		
	}
	
	protected TextField getNumberField() {
		return numberField;
	}

	protected abstract String getSubmitLabel();

	protected abstract String getHeader();

	protected abstract Form getForm(String id, PhoneBookEntry entry);

}
