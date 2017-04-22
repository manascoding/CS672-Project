package com.gmu;

import java.util.List;
import java.util.stream.Collectors;

import com.gmu.api.Person;
import com.gmu.db.PersonSqlDAO;
import com.gmu.db.PersonCouchbaseDAO;
import com.gmu.db.PersonDAO;
import com.gmu.db.PersonFileReader;
import com.gmu.resources.PersonCouchbaseResource;
import com.gmu.resources.PersonResource;
import com.gmu.resources.PersonSqlResource;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.assets.AssetsBundle;

public class CS672Application extends Application<CS672Configuration> {

    public static void main(final String[] args) throws Exception {
        new CS672Application().run(args);
    }

    @Override
    public String getName() {
        return "CS672";
    }

    @Override
    public void initialize(final Bootstrap<CS672Configuration> bootstrap) {
    	bootstrap.addBundle(new AssetsBundle());
    }

    @Override
    public void run(final CS672Configuration configuration,
                    final Environment environment) {
    	PersonSqlDAO sharedSqlDao = new PersonSqlDAO();
    	sharedSqlDao.initializeConnection();
        sharedSqlDao.clearAndCreateDatabase();
        
        PersonCouchbaseDAO sharedCouchbaseDao = new PersonCouchbaseDAO("default");
        sharedCouchbaseDao.initializeConnection();
        sharedCouchbaseDao.clearAndCreateDatabase();
        
        // Load the people from the csv into the db
        PersonFileReader fileReader = new PersonFileReader(sharedSqlDao, sharedCouchbaseDao);
    	fileReader.readPeopleFromFile("src/main/resources/assets/500k.csv", 100);
    	
    	// Run sample queries if needed
//    	runSampleQueries(sharedSqlDao);
//    	runSampleQueries(sharedCouchbaseDao);
    	
    	environment.jersey().register(new PersonSqlResource(sharedSqlDao));
    	environment.jersey().register(new PersonCouchbaseResource(sharedCouchbaseDao));
    }

	private void runSampleQueries(PersonDAO sharedDao) {
		// run sample queries
    	System.out.println("Printing Out All People");
    	List<Person> allPeople = sharedDao.allPeople();
    	System.out.println("DB SIZE: " + allPeople.size());
    	allPeople.stream().forEach(p -> {
    		System.out.println(p.toString());
    	});
    	
    	// Get all even ones to check email query.
    	List<String> evenEmails = allPeople.stream()
    			.filter(p -> p.getSequenceId() % 2 == 0)
    			.map(p -> p.getEmail())
    			.collect(Collectors.toList());
    	System.out.println("Printing Out Even People Email");
    	evenEmails.stream().forEach(p -> {
    		System.out.println(p.toString());
    	});
    	
    	List<Person> evenPeople = sharedDao.peopleWithEmails(evenEmails);
    	System.out.println("Printing Out Even People");
    	evenPeople.stream().forEach(p -> {
    		System.out.println(p.toString());
    	});
    	
    	// Get all the even people by ids
    	List<String> evenIds = allPeople.stream()
    			.filter(p -> p.getSequenceId() % 2 == 0)
    			.map(p -> p.getId().toString())
    			.collect(Collectors.toList());
    	System.out.println("Printing Out Even People Email");
    	evenIds.stream().forEach(p -> {
    		System.out.println(p.toString());
    	});
    	
    	List<Person> evenPeopleById = sharedDao.peopleWithIds(evenIds);
    	System.out.println("Printing Out Even People By Id");
    	evenPeopleById.stream().forEach(p -> {
    		System.out.println(p.toString());
    	});
    	
    	// Check the people within age query
    	long startTime = System.nanoTime();
    	List<Person> under30People = sharedDao.peopleWithinAge(0, 30);
    	long endTime = System.nanoTime();
    	long durationUnder30 = (endTime - startTime) / 1000000L; //divide by 1000000 to get milliseconds.
    	System.out.println("Printing Out Adults Under 30. Count: " + under30People.size() + " Took: " + durationUnder30 + " milliseconds");
    	under30People.stream().forEach(p -> {
    		System.out.println(p.toString());
    	});
    	
    	// Check people with a dollar value
    	startTime = System.nanoTime();
    	List<Person> under5kPeople = sharedDao.peopleWithinDollarAmount(0, 5000);
    	endTime = System.nanoTime();
    	long durationUnder5k = (endTime - startTime) / 1000000L; //divide by 1000000 to get milliseconds.
    	System.out.println("Printing Out People With Dollar Of 5K or less. Count: " + under5kPeople.size() + " Took: " + durationUnder5k + " milliseconds");
    	
    	under5kPeople.stream().forEach(p -> {
    		System.out.println(p.toString());
    	});
	}

}
