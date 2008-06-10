package org.londonwicket.osiv;

import javax.persistence.EntityManager;

import org.londonwicket.osiv.jpa.Identifiable;
import org.londonwicket.osiv.jpa.JpaThreadLocal;

public abstract class UpdateBeanDataForm<T extends Identifiable> extends BeanDataForm<T> {

	public UpdateBeanDataForm(String id, T bean) {
		super(id, bean);
	}

	@Override
	protected void onSubmit(T bean) {
		EntityManager em = JpaThreadLocal.get();
		em.merge(bean);
		em.flush();	
	}

}
