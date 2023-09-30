package gate.sql;

import gate.Person;
import gate.error.ConstraintViolationException;
import gate.error.NotFoundException;
import gate.type.ID;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class LinkTest
{

	public LinkTest()
	{
	}

	@BeforeAll
	public static void setUp() throws ConstraintViolationException, SQLException
	{
		TestDataSource.setUp();

	}

	@Test
	public void testSelectByGQN() throws SQLException, NotFoundException
	{
		try (Link link = TestDataSource.INSTANCE.getLink())
		{
			Person person = link
				.select(Person.class)
				.properties("=id", "name")
				.parameters(ID.valueOf(1))
				.orElseThrow(NotFoundException::new);

			assertEquals("Person 1", person.getName());
		}
	}
}
