package gate.sql;

import gate.Contact;
import gate.Person;
import gate.error.AppException;
import gate.error.ConstraintViolationException;
import gate.error.NotFoundException;
import gate.type.ID;
import java.sql.SQLException;
import java.text.ParseException;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class EntityUpdateTest
{

	@BeforeClass
	public static void setUp() throws ConstraintViolationException, SQLException
	{
		TestDataSource.getInstance().setUp();
	}

	@Test
	public void test() throws ConstraintViolationException, ParseException, SQLException, NotFoundException, AppException
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

			new ContactUpdate(link, new ID(1))
				.setType(expected.getType())
				.setPerson(expected.getPerson())
				.setValue(expected.getValue())
				.execute();

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

	public static class ContactUpdate extends EntityUpdate
	{

		public ContactUpdate(Link link, ID id)
		{
			super(link, Contact.class, id);
		}

		public ContactUpdate setType(Contact.Type type)
		{
			return (ContactUpdate) set("type", type);
		}

		public ContactUpdate setValue(String value)
		{
			return (ContactUpdate) set("value", value);
		}

		public ContactUpdate setPerson(Person person)
		{
			return (ContactUpdate) set("Person$id", person != null ? person.getId() : null);
		}
	}
}
