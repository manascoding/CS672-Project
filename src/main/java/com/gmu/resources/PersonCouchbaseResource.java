package com.gmu.resources;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.gmu.db.PersonCouchbaseDAO;

@Path("/personcouchbase")
@Produces(MediaType.APPLICATION_JSON)
public class PersonCouchbaseResource extends PersonResource {

	public PersonCouchbaseResource(PersonCouchbaseDAO personDao) {
		this.setPersonDAO(personDao);
	}
	
}
