package gate.sql.update;

import gate.Contact;
import gate.Person;
import gate.error.ConstraintViolationException;
import gate.error.NotFoundException;
import gate.sql.Link;
import gate.sql.TestDataSource;
import gate.sql.condition.Condition;
import gate.type.Date;
import gate.type.DateInterval;
import gate.type.ID;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;

public class UpdateTest
{

	@BeforeClass
	public static void setUp() throws ConstraintViolationException, SQLException
	{
		TestDataSource.getInstance().setUp();
	}

	@Test
	public void testString() throws SQLException, ConstraintViolationException
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			ID id = new ID(1);
			String name = "Jonh";
			Date birthdate = Date.of(31, 12, 2005);
			DateInterval contract = new DateInterval(Date.of(1, 1, 2010), Date.of(31, 12, 2010));

			assertEquals(1, link.prepare("update Person set name = ?, birthdate = ?, contract$date1 = ?, contract$date2 = ? where id = ?")
				.parameters(name, birthdate, contract, id)
				.execute());

			Optional<Object[]> optional
				= link.from("select id, name, birthdate, contract$date1, contract$date2 from Person where id = ?")
					.parameters(id)
					.fetchArray(ID.class, String.class, Date.class, DateInterval.class);
			if (optional.isPresent())
			{
				Object[] result = optional.get();
				assertEquals(id, result[0]);
				assertEquals(name, result[1]);
				assertEquals(birthdate, result[2]);
				assertEquals(contract, result[3]);
			} else
				fail("No result found");
		}
	}

	@Test
	public void testResourceFile() throws SQLException, ConstraintViolationException
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			ID id = new ID(2);
			String name = "Paul";
			Date birthdate = Date.of(20, 11, 2004);
			DateInterval contract = new DateInterval(Date.of(1, 1, 2008), Date.of(31, 12, 2010));

			assertEquals(1, link
				.prepare(getClass().getResource("UpdateTest/Update.sql"))
				.parameters(name, birthdate, contract, id)
				.execute());

			Optional<Object[]> optional
				= link
					.from("select id, name, birthdate, contract$date1, contract$date2 from Person where id = ?")
					.parameters(id)
					.fetchArray(ID.class, String.class, Date.class, DateInterval.class);
			if (optional.isPresent())
			{
				Object[] result = optional.get();
				assertEquals(id, result[0]);
				assertEquals(name, result[1]);
				assertEquals(birthdate, result[2]);
				assertEquals(contract, result[3]);
			} else
				fail("No result found");
		}
	}

	@Test
	public void testCompiledTableBuilder() throws ConstraintViolationException, SQLException
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			ID id = new ID(3);
			String name = "Richard";
			Date birthdate = Date.of(9, 8, 2000);
			DateInterval contract = new DateInterval(Date.of(1, 1, 2012), Date.of(31, 12, 2014));

			assertEquals(1, link
				.prepare(Update
					.table("Person")
					.set(String.class, "name", name)
					.set(Date.class, "birthdate", birthdate)
					.set(DateInterval.class, "contract", contract)
					.where(Condition.of("id").eq(id)))
				.execute());

			Optional<Object[]> optional
				= link
					.from("select id, name, birthdate, contract$date1, contract$date2 from Person where id = ?")
					.parameters(id)
					.fetchArray(ID.class, String.class, Date.class, DateInterval.class);
			if (optional.isPresent())
			{
				Object[] result = optional.get();
				assertEquals(id, result[0]);
				assertEquals(name, result[1]);
				assertEquals(birthdate, result[2]);
				assertEquals(contract, result[3]);
			} else
				fail("No result found");
		}
	}

	@Test
	public void testGenericTableBuilder() throws SQLException, ConstraintViolationException
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			ID id = new ID(4);
			String name = "Thomas";
			Date birthdate = Date.of(7, 8, 2000);
			DateInterval contract = new DateInterval(Date.of(2, 1, 2012), Date.of(4, 12, 2014));

			assertEquals(1, link
				.prepare(Update
					.table("Person")
					.set(String.class, "name")
					.set(Date.class, "birthdate")
					.set(DateInterval.class, "contract")
					.where(Condition.of("id").eq()))
				.parameters(name, birthdate, contract, id)
				.execute());

			Optional<Object[]> optional
				= link
					.from("select id, name, birthdate, contract$date1, contract$date2 from Person where id = ?")
					.parameters(id)
					.fetchArray(ID.class, String.class, Date.class, DateInterval.class);
			if (optional.isPresent())
			{
				Object[] result = optional.get();
				assertEquals(id, result[0]);
				assertEquals(name, result[1]);
				assertEquals(birthdate, result[2]);
				assertEquals(contract, result[3]);
			} else
				fail("No result found");
		}
	}

	@Test
	public void testTypedBuilder() throws SQLException, ConstraintViolationException
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			int id = 5;
			String name = "Maria";
			LocalDate birthdate = LocalDate.of(2001, 7, 19);
			DateInterval contract = new DateInterval(Date.of(1, 1, 2010), Date.of(31, 12, 2010));

			Person person = new Person()
				.setId(id)
				.setName(name)
				.setBirthdate(birthdate)
				.setContract(contract);

			assertEquals(1, link
				.prepare(Update
					.type(Person.class)
					.set("id", "name", "birthdate", "contract"))
				.value(person)
				.execute());

			Optional<Object[]> optional
				= link
					.from("select id, name, birthdate, contract$date1, contract$date2 from Person where id = ?")
					.parameters(id)
					.fetchArray(int.class, String.class, LocalDate.class, DateInterval.class);
			if (optional.isPresent())
			{
				Object[] result = optional.get();
				assertEquals(id, result[0]);
				assertEquals(name, result[1]);
				assertEquals(birthdate, result[2]);
				assertEquals(contract, result[3]);
			} else
				fail("No result found");
		}
	}

	@Test
	public void testFullTypedBuilder() throws SQLException, ConstraintViolationException
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			int id = 6;
			String name = "Newton";
			LocalDate birthdate = LocalDate.of(2001, 7, 19);
			DateInterval contract = new DateInterval(Date.of(1, 1, 2010), Date.of(31, 12, 2010));

			Person person = new Person()
				.setId(id)
				.setName(name)
				.setBirthdate(birthdate)
				.setContract(contract);

			assertEquals(1, link
				.prepare(Update
					.type(Person.class))
				.value(person)
				.execute());

			Optional<Object[]> optional
				= link
					.from("select id, name, birthdate, contract$date1, contract$date2 from Person where id = ?")
					.parameters(id)
					.fetchArray(int.class, String.class, LocalDate.class, DateInterval.class);
			if (optional.isPresent())
			{
				Object[] result = optional.get();
				assertEquals(id, result[0]);
				assertEquals(name, result[1]);
				assertEquals(birthdate, result[2]);
				assertEquals(contract, result[3]);
			} else
				fail("No result found");
		}
	}

	@Test
	public void testGQN() throws SQLException, ConstraintViolationException
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			int id = 7;
			String name = "Fred";
			LocalDate birthdate = LocalDate.of(2001, 7, 19);
			DateInterval contract = new DateInterval(Date.of(1, 1, 2010), Date.of(31, 12, 2010));

			Person person = new Person()
				.setId(id)
				.setName(name)
				.setBirthdate(birthdate)
				.setContract(contract);

			assertEquals(1, link
				.update(Person.class)
				.properties("=id", "name", "birthdate", "contract")
				.execute(person));

			Optional<Object[]> optional
				= link
					.from("select id, name, birthdate, contract$date1, contract$date2 from Person where id = ?")
					.parameters(id)
					.fetchArray(int.class, String.class, LocalDate.class, DateInterval.class);
			if (optional.isPresent())
			{
				Object[] result = optional.get();
				assertEquals(id, result[0]);
				assertEquals(name, result[1]);
				assertEquals(birthdate, result[2]);
				assertEquals(contract, result[3]);
			} else
				fail("No result found");
		}
	}

	@Test
	public void testFullGQN() throws SQLException, ConstraintViolationException
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			int id = 8;
			String name = "Alfred";
			LocalDate birthdate = LocalDate.of(2001, 7, 19);
			DateInterval contract = new DateInterval(Date.of(1, 1, 2010), Date.of(31, 12, 2010));

			Person person = new Person()
				.setId(id)
				.setName(name)
				.setBirthdate(birthdate)
				.setContract(contract);

			assertEquals(1, link
				.update(Person.class)
				.execute(person));

			Optional<Object[]> optional
				= link
					.from("select id, name, birthdate, contract$date1, contract$date2 from Person where id = ?")
					.parameters(id)
					.fetchArray(int.class, String.class, LocalDate.class, DateInterval.class);
			if (optional.isPresent())
			{
				Object[] result = optional.get();
				assertEquals(id, result[0]);
				assertEquals(name, result[1]);
				assertEquals(birthdate, result[2]);
				assertEquals(contract, result[3]);
			} else
				fail("No result found");
		} 
	}

	@Test
	public void testGQN2() throws ConstraintViolationException, ParseException, SQLException, NotFoundException
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			Contact expected
				= new Contact()
					.setId(new ID(1))
					.setType(Contact.Type.PHONE)
					.setValue("99999999")
					.setPerson(new Person()
						.setId(1));

			assertEquals(1, link
				.update(Contact.class)
				.properties("=id", "type", "value", "person.id")
				.execute(expected));

			Contact result = link
				.select(Contact.class)
				.properties("=id", "type", "value", "person.id")
				.matching(expected)
				.orElseThrow(NotFoundException::new);

			assertEquals(expected.getValue(), result.getValue());
			assertEquals(expected.getType(), result.getType());
			assertEquals(expected.getId(), result.getId());
			assertEquals(expected.getPerson().getId(), result.getPerson().getId());
		}
	}

	@AfterClass
	public static void clean()
	{
		TestDataSource.getInstance().clean();
	}
}
