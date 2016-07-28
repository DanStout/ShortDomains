package ca.danielstout.shortdomains.person;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

public class PersonService
{
	private static final Logger log = LoggerFactory.getLogger(PersonService.class);
	private final Sql2o db;

	@Inject
	public PersonService(Sql2o sql2o)
	{
		db = sql2o;
	}

	public List<Person> getPeople()
	{
		String sql = "select * from person;";
		try (Connection con = db.open())
		{
			return con.createQuery(sql)
				.executeAndFetch(Person.class);
		}
	}

	public void addPerson(Person p)
	{
		String sql = "insert into person(name) values (:name);";
		try (Connection con = db.open())
		{
			con.createQuery(sql)
				.addParameter("name", p.getName())
				.executeUpdate();
		}

	}

	public boolean deletePerson(long id)
	{
		String sql = "delete from person where id = :id;";
		try (Connection con = db.open())
		{
			int result = con.createQuery(sql)
				.addParameter("id", id)
				.executeUpdate()
				.getResult();
			log.debug("Result: {}", result);
		}
		return true;
	}
}
