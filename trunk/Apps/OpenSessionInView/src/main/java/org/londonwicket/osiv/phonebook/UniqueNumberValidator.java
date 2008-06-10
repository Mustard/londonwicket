package org.londonwicket.osiv.phonebook;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.londonwicket.osiv.jpa.JpaThreadLocal;

public class UniqueNumberValidator extends AbstractValidator {

	@Override
	protected void onValidate(IValidatable validatable) {
		String number  = (String) validatable.getValue();
		if(number != null){
			if(JpaThreadLocal.get().find(PhoneBookEntry.class, number) != null)
				this.error(validatable);
		}
	}

}
