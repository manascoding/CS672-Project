package com.gmu.db;

import java.util.List;

import com.gmu.api.Person;

public interface PersonDAO {

	void initializeConnection();

	void clearAndCreateDatabase();

	Person insertPerson(Person person);

	List<Person> peopleWithinAge(int lowerBound, int upperBound);

	List<Person> peopleWithinDollarAmount(int lowerBound, int upperBound);

	List<Person> peopleWithEmails(List<String> emails);

	List<Person> allPeople();

}