package org.londonwicket.osiv.phonebook;

import org.apache.wicket.markup.html.form.Form;
import org.londonwicket.osiv.UpdateBeanDataForm;

public class UpdateNumberPage extends AbstractNumberPage{

	public UpdateNumberPage(PhoneBookEntry entry) {
		super(entry);
		this.getNumberField().setEnabled(false);
	}

	@Override
	protected Form getForm(String id, PhoneBookEntry entry) {
		return new UpdateBeanDataForm<PhoneBookEntry>(id, entry){
			@Override
			protected void afterSubmit() {
				setResponsePage(NumbersPage.class);				
			}	
		};
	}

	@Override
	protected String getHeader() {
		return "Update Phone Number";
	}

	@Override
	protected String getSubmitLabel() {
		return "Update";
	}

}
