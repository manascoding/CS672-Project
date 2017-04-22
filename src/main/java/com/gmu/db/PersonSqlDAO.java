package com.gmu.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.gmu.api.Person;


public class PersonSqlDAO implements PersonDAO {
	
	private static final int QUERY_TIMEOUT = 60;
	
	@Override
	public void initializeConnection() {
		// Load sqlite
        try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void clearAndCreateDatabase() {

		Connection connection = null;
		try {
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:person.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(QUERY_TIMEOUT);

			// Drop old table
			statement.executeUpdate("DROP TABLE IF EXISTS person");

			// Create new table
			statement.executeUpdate("CREATE TABLE person ("
					+ "id STRING PRIMARY KEY, "
					+ "sequence INTEGER, "
					+ "firstName STRING, "
					+ "lastName STRING, "
					+ "age INTEGER, "
					+ "birthday STRING, "
					+ "email STRING, "
					+ "street STRING, "
					+ "city STRING, "
					+ "state STRING, "
					+ "zip STRING, "
					+ "dollar INTEGER,"
					+ "type STRING);");

			// Create indexes
			statement.executeUpdate("CREATE INDEX IF NOT EXISTS age_index ON person (age);");
			statement.executeUpdate("CREATE INDEX IF NOT EXISTS email_index ON person (email);");

		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(connection != null) {
					connection.close();
				}
			} catch(SQLException e) {
				// connection failed to close.
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public Person insertPerson(Person person) {
		Connection connection = null;
		try {
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:person.db");
			PreparedStatement statement = connection.prepareStatement("INSERT INTO person"
					+ " (id, sequence, firstName, lastName, age, "
					+ "birthday, email, street, city, state,"
					+ " zip, dollar, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			statement.setQueryTimeout(QUERY_TIMEOUT);
			statement.setString(1, person.getId().toString());
			statement.setInt(2, person.getSequenceId());
			statement.setString(3, person.getFirstName());
			statement.setString(4, person.getLastName());
			statement.setInt(5, person.getAge());
			statement.setString(6, person.getBirthday());
			statement.setString(7, person.getEmail());
			statement.setString(8, person.getStreet());
			statement.setString(9, person.getCity());
			statement.setString(10, person.getState());
			statement.setString(11, person.getZip());
			statement.setInt(12, person.getDollar());
			statement.setString(13, person.getType());
			statement.executeUpdate();

		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(connection != null) {
					connection.close();
				}
			} catch(SQLException e) {
				// connection failed to close.
				e.printStackTrace();
			}
		}
		return person;
	}
	
	@Override
	public List<Person> peopleWithinAge(int lowerBound, int upperBound) {
		
		Connection connection = null;
		List<Person> people = null;
		try {
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:person.db");
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM person WHERE age >= ? AND age <= ? ORDER BY age ASC;");
			statement.setInt(1, lowerBound);
			statement.setInt(2, upperBound);
			statement.setQueryTimeout(QUERY_TIMEOUT);      
			ResultSet set = statement.executeQuery();
			people = new ArrayList<>();
			while (set.next()) {				
				Person person = this.personFromResultSet(set);
				people.add(person);
			}

		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(connection != null) {
					connection.close();
				}
			} catch(SQLException e) {
				// connection failed to close.
				e.printStackTrace();
			}
		}
		return people;
	}
	
	@Override
	public List<Person> peopleWithinDollarAmount(int lowerBound, int upperBound) {
		Connection connection = null;
		List<Person> people = null;
		try {
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:person.db");
			PreparedStatement statement = connection.prepareStatement(
					"SELECT * FROM person WHERE dollar >= ? AND dollar <= ? ORDER BY dollar ASC;");
			statement.setInt(1, lowerBound);
			statement.setInt(2, upperBound);
			statement.setQueryTimeout(QUERY_TIMEOUT);     
			ResultSet set = statement.executeQuery();
			people = new ArrayList<>();
			while (set.next()) {				
				Person person = this.personFromResultSet(set);
				people.add(person);
			}

		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(connection != null) {
					connection.close();
				}
			} catch(SQLException e) {
				// connection failed to close.
				e.printStackTrace();
			}
		}
		return people;
	}
	
	@Override
	public List<Person> peopleWithEmails(List<String> emails) {
		
		if (emails == null) {
			throw new IllegalArgumentException("Email list cannot be null");
		}
		if (emails.isEmpty()) {
			return new ArrayList<>();
		}
		Connection connection = null;
		List<Person> people = null;
		try {
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:person.db");
			
			String query = "SELECT * FROM person WHERE email IN (";
			Iterator<String> iterator = emails.iterator();
			while (iterator.hasNext()) {
				iterator.next();
				if (iterator.hasNext()) {
					query += "?, ";
				} else {
					query += "?);";
				}
			}
			PreparedStatement statement = connection.prepareStatement(query);
			Iterator<String> iterator2 = emails.iterator();
			int parameterIndex = 1;
			while (iterator2.hasNext()) {
				String email = iterator2.next();
				statement.setString(parameterIndex, email);
				parameterIndex++;
			}
			ResultSet rs = statement.executeQuery();
			statement.setQueryTimeout(QUERY_TIMEOUT);
			people = new ArrayList<>();
			while (rs.next()) {				
				Person person = this.personFromResultSet(rs);
				people.add(person);
			}

		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(connection != null) {
					connection.close();
				}
			} catch(SQLException e) {
				// connection failed to close.
				e.printStackTrace();
			}
		}
		return people;
	}
	
	@Override
	public List<Person> peopleWithIds(List<String> ids) {
		
		if (ids == null) {
			throw new IllegalArgumentException("Id list cannot be null");
		}
		
		if (ids.isEmpty()) {
			return new ArrayList<>();
		}
		Connection connection = null;
		List<Person> people = null;
		try {
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:person.db");
			
			String query = "SELECT * FROM person WHERE id IN (";
			Iterator<String> iterator = ids.iterator();
			while (iterator.hasNext()) {
				iterator.next();
				if (iterator.hasNext()) {
					query += "?, ";
				} else {
					query += "?);";
				}
			}
			PreparedStatement statement = connection.prepareStatement(query);
			Iterator<String> iterator2 = ids.iterator();
			int parameterIndex = 1;
			while (iterator2.hasNext()) {
				String id = iterator2.next();
				statement.setString(parameterIndex, id);
				parameterIndex++;
			}
			ResultSet rs = statement.executeQuery();
			statement.setQueryTimeout(QUERY_TIMEOUT);
			people = new ArrayList<>();
			while (rs.next()) {				
				Person person = this.personFromResultSet(rs);
				people.add(person);
			}

		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(connection != null) {
					connection.close();
				}
			} catch(SQLException e) {
				// connection failed to close.
				e.printStackTrace();
			}
		}
		return people;
	}
	
	@Override
	public List<Person> allPeople() {
		Connection connection = null;
		List<Person> people = null;
		try {
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:person.db");
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM person;");
			statement.setQueryTimeout(QUERY_TIMEOUT);      
			ResultSet set = statement.executeQuery();
			people = new ArrayList<>();
			while (set.next()) {				
				Person person = this.personFromResultSet(set);
				people.add(person);
			}

		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(connection != null) {
					connection.close();
				}
			} catch(SQLException e) {
				// connection failed to close.
				e.printStackTrace();
			}
		}
		return people;
	}
	
	private Person personFromResultSet(ResultSet set) throws SQLException {
		Person person = new Person();
		person.setId(UUID.fromString(set.getString("id")));
		person.setSequenceId(set.getInt("sequence"));
		person.setFirstName(set.getString("firstName"));
		person.setLastName(set.getString("lastName"));
		person.setAge(set.getInt("age"));
		person.setBirthday(set.getString("birthday"));
		person.setEmail(set.getString("email"));
		person.setStreet(set.getString("street"));
		person.setCity(set.getString("city"));
		person.setState(set.getString("state"));
		person.setZip(set.getString("zip"));
		person.setDollar(set.getInt("dollar"));
		person.setType(set.getString("type"));
		return person;
	}

}
