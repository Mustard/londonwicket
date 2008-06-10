package org.londonwicket.osiv;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

public abstract class DataForm extends Form {

	@Override
	protected void onSubmit() {
		doSubmit();
		((DataRequestCycle)this.getRequestCycle()).onSubmit();
		afterSubmit();
	}

	/**
	 * Called before a commit, appropriate for any persisting logic in a form
	 */
	protected abstract void doSubmit();
	
	/**
	 * Called after a database write has been done, appropriate for any form redirects or ajax refreshes on the page based on data written
	 * to db.
	 */
	protected abstract void afterSubmit();

	public DataForm(String id) {
		super(id);
	}
	
	public DataForm(String id, Model model) {
		super(id, model);
	}

}
