package com.gmu.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.*;

import com.gmu.api.Person;
import com.gmu.db.PersonDAO;

@Produces(MediaType.APPLICATION_JSON)
public abstract class PersonResource {
	
	private PersonDAO personDAO;

	
	public PersonDAO getPersonDAO() {
		return personDAO;
	}

	public void setPersonDAO(PersonDAO personDAO) {
		this.personDAO = personDAO;
	}

	@GET
    public List<Person> listPeople() {
        return personDAO.allPeople();
    }
	
    @POST
    public Person createPerson(Person person) {
        return personDAO.insertPerson(person);
    }
    
    @POST
    @Path("/searchEmails")
    public List<Person> peopleWithEmails(List<String> emails) {
    	return personDAO.peopleWithEmails(emails);
    }
    
    @GET
    @Path("/searchAge")
    public List<Person> peopleWithinAge(
    		@QueryParam("lowerBound") int lowerBound, @QueryParam("upperBound") int upperBound) {
    	return personDAO.peopleWithinAge(lowerBound, upperBound);
    }
    
    @GET
    @Path("/searchDollar")
    public List<Person> peopleWithinDollarAmount(
    		@QueryParam("lowerBound") int lowerBound, @QueryParam("upperBound") int upperBound) {
    	return personDAO.peopleWithinDollarAmount(lowerBound, upperBound);
    }

}
