package org.londonwicket.osiv.phonebook;

import java.util.Iterator;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.londonwicket.osiv.jpa.JpaThreadLocal;

public class NumberProvider implements IDataProvider {
	public Iterator iterator(int first, int results) {
		return JpaThreadLocal.get().createQuery(
				"from PhoneBookEntry as entry ORDER BY entry.name")
				.setFirstResult(first).setMaxResults(results).getResultList()
				.iterator();
	}

	public int size() {
		int results = 0;
		Number obj = (Number) JpaThreadLocal.get().createNativeQuery(
				"SELECT count(phone_number) from phonebook").getResultList()
				.get(0);
		results = obj.intValue();
		return results;
	}

	public IModel model(Object modelObject) {
		return new BeanDetachableModel(modelObject);
	}

	public void detach() {
		// no detach behaviour needed
	}

	private class BeanDetachableModel extends LoadableDetachableModel {
		private Object bean;

		public BeanDetachableModel(Object object) {
			this.bean = object;
		}

		@Override
		protected Object load() {
			return bean;
		}
	}
}