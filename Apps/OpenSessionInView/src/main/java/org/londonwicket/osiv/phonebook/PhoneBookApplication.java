package org.londonwicket.osiv.phonebook;

import org.londonwicket.osiv.DataWebApplication;

public class PhoneBookApplication extends DataWebApplication {

	@Override
	protected String getUnitName() {
		return "phonebook";
	}

	@Override
	public Class getHomePage() {
		return NumbersPage.class;
	}

}
