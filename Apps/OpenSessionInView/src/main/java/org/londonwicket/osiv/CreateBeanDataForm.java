package org.londonwicket.osiv;

import javax.persistence.EntityManager;

import org.londonwicket.osiv.jpa.Identifiable;
import org.londonwicket.osiv.jpa.JpaThreadLocal;

public abstract class CreateBeanDataForm<T extends Identifiable> extends BeanDataForm<T> {

	public CreateBeanDataForm(String id, T bean) {
		super(id, bean);
	}

	@Override
	protected void onSubmit(T bean) {
		EntityManager em = JpaThreadLocal.get();
		em.persist(bean);
		em.flush();
	}

}
