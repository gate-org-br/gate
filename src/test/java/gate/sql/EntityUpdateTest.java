package gate.sql;

import gate.Contact;
import gate.Person;
import gate.error.AppException;
import gate.error.ConstraintViolationException;
import gate.error.NotFoundException;
import gate.type.ID;
import java.sql.SQLException;
import java.text.ParseException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class EntityUpdateTest
{

	@BeforeAll
	public static void setUp() throws ConstraintViolationException, SQLException
	{
		TestDataSource.setUp();
	}

	@Test
	public void test() throws ConstraintViolationException, ParseException, SQLException, NotFoundException, AppException
	{
		try (Link link = TestDataSource.INSTANCE.getLink())
		{
			Contact expected
				= new Contact()
					.setId(ID.valueOf(1))
					.setType(Contact.Type.PHONE)
					.setVal("99999999")
					.setPerson(new Person()
						.setId(1));

			new ContactUpdate(link, ID.valueOf(1))
				.setType(expected.getType())
				.setPerson(expected.getPerson())
				.setVal(expected.getVal())
				.execute();

			Contact result = link
				.select(Contact.class)
				.properties("=id", "type", "val", "person.id")
				.matching(expected)
				.orElseThrow(NotFoundException::new);

			assertEquals(expected.getVal(), result.getVal());
			assertEquals(expected.getType(), result.getType());
			assertEquals(expected.getId(), result.getId());
			assertEquals(expected.getPerson().getId(), result.getPerson().getId());
		}
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

		public ContactUpdate setVal(String val)
		{
			return (ContactUpdate) set("val", val);
		}

		public ContactUpdate setPerson(Person person)
		{
			return (ContactUpdate) set("Person$id", person != null ? person.getId() : null);
		}
	}
}
