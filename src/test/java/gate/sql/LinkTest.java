package gate.sql;

import gate.Person;
import gate.error.ConstraintViolationException;
import gate.error.NotFoundException;
import gate.type.ID;
import java.sql.SQLException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class LinkTest
{

	public LinkTest()
	{
	}

	@BeforeClass
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
				.parameters(new ID(1))
				.orElseThrow(NotFoundException::new);

			Assert.assertEquals("Person 1", person.getName());
		}
	}

	@AfterClass
	public static void clean()
	{
		TestDataSource.getInstance().clean();
	}
}
