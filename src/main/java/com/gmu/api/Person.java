package com.gmu.api;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Person {

	public Person () {
		
	}
	
	@JsonProperty
	private UUID id;
	
	@JsonProperty
	private int sequenceId;
	
	@JsonProperty
	private String firstName;
	
	@JsonProperty
	private String lastName;
	
	@JsonProperty
	private Integer age;
	
	@JsonProperty
	private String birthday;
	
	@JsonProperty
	private String email;
	
	@JsonProperty
	private String street;
	
	@JsonProperty
	private String city;
	
	@JsonProperty
	private String state;
	
	@JsonProperty
	private String zip;
	
	@JsonProperty
	private Integer dollar;
	
	@JsonProperty
	private String type;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public int getSequenceId() {
		return sequenceId;
	}

	public void setSequenceId(int sequenceId) {
		this.sequenceId = sequenceId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public Integer getDollar() {
		return dollar;
	}

	public void setDollar(Integer dollar) {
		this.dollar = dollar;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Person [id=" + id + ", sequenceId=" + sequenceId + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", age=" + age + ", birthday=" + birthday + ", email=" + email + ", street=" + street + ", city="
				+ city + ", state=" + state + ", zip=" + zip + ", dollar=" + dollar + ", type=" + type + "]";
	}
	
}
