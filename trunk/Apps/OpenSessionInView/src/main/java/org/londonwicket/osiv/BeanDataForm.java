package org.londonwicket.osiv;

import org.londonwicket.osiv.jpa.Identifiable;

public abstract class BeanDataForm<T extends Identifiable> extends DataForm {
	private T bean;

	public BeanDataForm(String id, T bean) {
		super(id);
		this.bean = bean;
	}
	
	protected T getBean(){
		return bean;
	}

	@Override
	protected void doSubmit() {
		onSubmit(bean);
	}

	protected abstract void onSubmit(T bean);

}
