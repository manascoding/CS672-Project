package com.gmu.resources;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.gmu.db.PersonSqlDAO;

@Path("/personsql")
@Produces(MediaType.APPLICATION_JSON)
public class PersonSqlResource extends PersonResource {
	
	public PersonSqlResource(PersonSqlDAO personDao) {
		this.setPersonDAO(personDao);
	}

}
