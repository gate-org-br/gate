/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.sql;

import com.mysql.cj.jdbc.MysqlDataSource;
import gate.Contact;
import gate.Person;
import gate.error.ConstraintViolationException;
import gate.sql.insert.Insert;
import gate.type.ID;
import gate.type.LocalDateInterval;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;

/**
 *
 * @author davins
 */
public class TestDataSource
{

	private static volatile TestDataSource instance;

	public static TestDataSource getInstance()
	{
		TestDataSource testDataSource = TestDataSource.instance;
		if (testDataSource == null)
			synchronized (TestDataSource.class)
		{
			testDataSource = TestDataSource.instance;
			if (testDataSource == null)
				TestDataSource.instance = testDataSource = new TestDataSource();
		}
		return testDataSource;
	}

	private final MysqlDataSource DATA_SOURCE
		= new MysqlDataSource();

	private TestDataSource()
	{
		DATA_SOURCE.setDatabaseName("gatetest");
		DATA_SOURCE.setServerName("localhost");
	}

	public Link getLink() throws SQLException
	{
		return new Link(DATA_SOURCE.getConnection("gatetest", "gatetest"));
	}

	public void setUp() throws ConstraintViolationException, SQLException
	{
		try (Link link = getLink())
		{
			link.prepare("delete from Contact").execute();

			link.prepare("delete from Person").execute();

			List<Person> persons = new ArrayList<>();
			for (int i = 1; i <= 31; i++)
			{
				Person person = new Person();
				person.setId(i);
				person.setName("Person " + i);
				person.setBirthdate(LocalDate.of(2000, 12, i));
				person.setContract(LocalDateInterval.of(LocalDate.of(2000, 12, i), LocalDate.of(2020, 12, i)));

				Contact contact = new Contact();
				contact.setId(new ID(i));
				contact.setPerson(person);
				contact.setType(Contact.Type.PHONE);
				contact.setValue(String.valueOf(i));

				person.getContacts().add(contact);

				persons.add(person);
			}

			link
				.prepare(Insert.into(Person.class).set("id", "name", "birthdate", "contract"))
				.values(persons)
				.execute();

			link.prepare(Insert.into(Contact.class).set("id", "person.id", "type", "value"))
				.values(persons.stream().flatMap(e -> e.getContacts().stream()).collect(Collectors.toList()))
				.execute();

		}
	}

	public void clean()
	{
		try (Link link = getLink())
		{
			link.prepare("delete from Contact").execute();

			link.prepare("delete from Person").execute();
		} catch (SQLException | ConstraintViolationException ex)
		{
			Assert.fail(ex.getMessage());
		}
	}
}
