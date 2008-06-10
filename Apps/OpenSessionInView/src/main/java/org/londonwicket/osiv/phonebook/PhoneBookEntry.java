package org.londonwicket.osiv.phonebook;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.londonwicket.osiv.jpa.Identifiable;

@Entity
@Table(name="phonebook")
public class PhoneBookEntry implements Identifiable<String> {
	@Id
	@Column(name="phone_number")
	private String number;
	@Column(name="account_holder")
	private String name;
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getId() {
		return number;
	}
}
