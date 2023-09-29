package gate.sql;

import gate.Person;
import gate.error.ConstraintViolationException;
import gate.error.NotFoundException;
import gate.type.ID;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class LinkTest
{

	public LinkTest()
	{
	}

	@BeforeAll
	public static void setUp() throws ConstraintViolationException, SQLException
	{
		TestDataSource.getInstance().setUp();

	}

	@Test
	public void testSelectByGQN() throws SQLException, NotFoundException
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			Person person = link
				.select(Person.class)
				.properties("=id", "name")
				.parameters(ID.valueOf(1))
				.orElseThrow(NotFoundException::new);

			assertEquals("Person 1", person.getName());
		}
	}

	@AfterAll
	public static void clean()
	{
		TestDataSource.getInstance().clean();
	}
}
