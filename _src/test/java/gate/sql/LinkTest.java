package gate.sql;

import gate.Person;
import gate.error.NotFoundException;
import gate.sql.select.SelectTest;
import gate.type.ID;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author davins
 */
public class LinkTest
{

	public LinkTest()
	{
	}

	@BeforeClass
	public static void setUp()
	{
		TestDataSource.getInstance().setUp();

	}

	@Test
	public void testSelectByGQN()
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			Person person = link
				.select(Person.class)
				.properties("=id", "name")
				.parameters(new ID(1))
				.orElseThrow(NotFoundException::new);

			Assert.assertEquals("Person 1", person.getName());
		} catch (NotFoundException e)
		{
			Assert.fail(e.getMessage());
		} catch (SQLException ex)
		{
			Logger.getLogger(SelectTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@AfterClass
	public static void clean()
	{
		TestDataSource.getInstance().clean();
	}
}
