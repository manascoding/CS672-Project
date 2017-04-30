package com.gmu.db;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.UUID;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.gmu.api.Person;

public class PersonFileReader {

	private PersonDAO personSqlDAO;
	private PersonDAO personCouchbaseDAO;
	
	public PersonFileReader(PersonDAO personSqlDAO, PersonDAO personCouchbaseDAO) {
		this.personSqlDAO = personSqlDAO;
		this.personCouchbaseDAO = personCouchbaseDAO;
	}
	
	public void readPeopleFromFile(String fileName) {
		this.readPeopleFromFile(fileName, Integer.MAX_VALUE, 0);
	}
	
	public void readPeopleFromFile(String fileName, int count, int skip) {
		
		try {
			System.out.println("Working Directory = " +
				      System.getProperty("user.dir"));
			Reader in = new FileReader(fileName);
			// Retrieve the people from the csv file
			Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
			int counter = 0;
			for (CSVRecord record : records) {
				if (record.isConsistent()) {
					if (counter >= count) {
						break;
					}
					if (skip > 0) {
						skip--;
					} else {
						String id = record.get("guid");
					    String seq = record.get("seq");
					    String firstName = record.get("first");
					    String lastName = record.get("last");
					    String age = record.get("age");
					    String birthday = record.get("birthday");
					    String email = record.get("email");
					    String street = record.get("street");
					    String city = record.get("city");
					    String state = record.get("state");
					    String zip = record.get("zip");
					    String dollar = record.get("dollar");
					    String type = record.get("pick");
					    
					    Person person = new Person();
					    person.setId(UUID.fromString(id));
					    person.setSequenceId(Integer.parseInt(seq));
					    person.setFirstName(firstName);
					    person.setLastName(lastName);
					    person.setAge(Integer.parseInt(age));
					    person.setBirthday(birthday);
					    person.setEmail(email);
					    person.setStreet(street);
					    person.setCity(city);
					    person.setState(state);
					    person.setZip(zip);
					    // Decided to use this field as a number so removing all the unnecessary parts	
					    person.setDollar(
					    		(int)Math.round(Double.parseDouble(dollar.replaceAll("\\$", "").replaceAll(",", "")))); 
					    person.setType(type);
					    
					    System.out.println(person.toString());
					    // Insert the person in the database
					    this.personSqlDAO.insertPerson(person);
					    this.personCouchbaseDAO.insertPerson(person);
					    
					    counter++;
					}
				}
			}
			System.out.println("Added: " + counter + " records into DB.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
