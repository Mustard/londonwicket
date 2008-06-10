package org.londonwicket.osiv.jpa;

import java.io.Serializable;

public interface Identifiable<ID extends Serializable> extends Serializable {
	public ID getId();
}
