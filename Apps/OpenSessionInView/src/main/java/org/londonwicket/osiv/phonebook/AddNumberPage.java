package org.londonwicket.osiv.phonebook;

import org.apache.wicket.markup.html.form.Form;
import org.londonwicket.osiv.CreateBeanDataForm;

public class AddNumberPage extends AbstractNumberPage{
	
	public AddNumberPage(){
		super(new PhoneBookEntry());
		this.getNumberField().add(new UniqueNumberValidator());
	}

	@Override
	protected Form getForm(String id, PhoneBookEntry entry) {
		return new CreateBeanDataForm<PhoneBookEntry>(id, entry){
			@Override
			protected void afterSubmit() {
				setResponsePage(NumbersPage.class);				
			}	
		};
	}

	@Override
	protected String getHeader() {
		return "Add new Phone Number";
	}

	@Override
	protected String getSubmitLabel() {
		return "Add";
	}

}
