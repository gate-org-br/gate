package gate.sql.fetcher;

import gate.Person;
import gate.sql.Link;
import gate.sql.select.Select;
import gate.sql.TestDataSource;
import gate.sql.condition.Condition;
import gate.sql.select.SelectTest;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class MapListFetcherTest
{

	@BeforeClass
	public static void setUp()
	{
		TestDataSource.getInstance().setUp();
	}

	@Test
	public void test01()
	{
		try (Link connection = TestDataSource.getInstance().getLink())
		{

			List<Map<String, Object>> results = connection
				.from(Select.from(Person.class)
					.properties("id", "name", "birthdate")
					.where(Condition.TRUE))
				.fetchMapList();

			results.forEach(e -> Assert.assertEquals(e.get("name"), String.format("Person %s", e.get("id").toString())));

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
