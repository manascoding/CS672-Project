package com.gmu.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.error.DocumentAlreadyExistsException;
import com.couchbase.client.java.view.Stale;
import com.couchbase.client.java.view.ViewQuery;
import com.couchbase.client.java.view.ViewResult;
import com.couchbase.client.java.view.ViewRow;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmu.api.Person;

public class PersonCouchbaseDAO implements PersonDAO {

	private static final String ALL_DOCS = "all_docs";
	private String bucketName;
	private Bucket bucket;
	
	public PersonCouchbaseDAO(String bucketName) {
		this.bucketName = bucketName;
	}
	
	public void initializeConnection() {
		if (this.bucket == null) {
			CouchbaseCluster cluster = CouchbaseCluster.create();
			this.bucket = cluster.openBucket(this.bucketName);
		}
	}
	
	private String key(Person person) {
		return "person::" + person.getId().toString();
	}
	
	private JsonObject jsonObjectFromPerson(Person person) {
		ObjectMapper mapper = new ObjectMapper();
		String json;
		try {
			json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(person);
			JsonObject jsonObject = JsonObject.fromJson(json);
			return jsonObject;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Person personFromJsonObject(JsonObject jsonObject) {
		ObjectMapper mapper = new ObjectMapper();
		String values = jsonObject.toString();
		Person inPerson = null;
		try {
			inPerson = mapper.readValue(values, Person.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inPerson;
	}
	
	public Person insertPerson(Person person) {
		if (person == null) {
			throw new IllegalArgumentException("Person cannot be null");
		}
		JsonObject jsonObject = this.jsonObjectFromPerson(person);
		JsonDocument document = JsonDocument.create(this.key(person), jsonObject);
		try {
			JsonDocument inserted = this.bucket.insert(document);
			if (inserted != null) {
				return this.personFromJsonObject(inserted.content());
			}
		} catch (DocumentAlreadyExistsException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public void clearAndCreateDatabase() {
		ViewResult result = this.bucket.query(ViewQuery.from(ALL_DOCS, "all_docs").stale(Stale.FALSE));
		int counter = 0;
        for (ViewRow row : result) {
        	counter ++;
            bucket.remove(row.id());
        }
        System.out.println(counter);
	}

	@Override
	public List<Person> peopleWithinAge(int lowerBound, int upperBound) {
		ViewResult result = this.bucket.query(
				ViewQuery.from(ALL_DOCS, "by_age")
				.startKey(lowerBound)
				.endKey(upperBound));
		List<Person> people = new ArrayList<>();
        for (ViewRow row : result) {
            JsonDocument document = bucket.get(row.value().toString());
            if (document != null) {
				 Person person = this.personFromJsonObject(document.content());
				 if (person != null) {
					 people.add(person);
				 }
			}
        }
		return people;
	}

	@Override
	public List<Person> peopleWithinDollarAmount(int lowerBound, int upperBound) {
		// There is no view query so we need to search all the documents. 
		ViewResult result = this.bucket.query(ViewQuery.from(ALL_DOCS, "all_docs"));
		List<Person> people = new ArrayList<>();
        for (ViewRow row : result) {
            JsonDocument document = bucket.get(row.value().toString());
            if (document != null) {
				 Person person = this.personFromJsonObject(document.content());
				 if (person != null) {
					 if (person.getDollar() >= lowerBound && person.getDollar() <= upperBound) {
						 people.add(person);
					 }
				 }
			}
        }
        return people;
	}

	@Override
	public List<Person> peopleWithEmails(List<String> emails) {
		JsonArray keys = JsonArray.from(emails);
		ViewResult result = this.bucket.query(ViewQuery.from(ALL_DOCS, "by_email").keys(keys));
		List<Person> people = new ArrayList<>();
        for (ViewRow row : result) {
            JsonDocument document = bucket.get(row.value().toString());
            if (document != null) {
				 Person person = this.personFromJsonObject(document.content());
				 if (person != null) {
					 people.add(person);
				 }
			}
        }
		return people;
	}

	@Override
	public List<Person> allPeople() {
		List<Person> people = new ArrayList<>();
		ViewResult result = this.bucket.query(ViewQuery.from(ALL_DOCS, "all_docs"));
        for (ViewRow row : result) {
        	JsonDocument document = bucket.get(row.value().toString());
            if (document != null) {
				 Person person = this.personFromJsonObject(document.content());
				 if (person != null) {
					 people.add(person);
				 }
			}
        }
        return people;
	}
	
	public String getBucketName() {
		return bucketName;
	}
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

}
