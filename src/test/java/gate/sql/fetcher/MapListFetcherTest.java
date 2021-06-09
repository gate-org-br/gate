package gate.sql.fetcher;

import gate.Person;
import gate.error.ConstraintViolationException;
import gate.sql.Link;
import gate.sql.select.Select;
import gate.sql.TestDataSource;
import gate.sql.condition.Condition;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class MapListFetcherTest
{

	@BeforeClass
	public static void setUp() throws ConstraintViolationException, SQLException
	{
		TestDataSource.getInstance().setUp();
	}

	@Test
	public void test01() throws SQLException
	{
		try (Link connection = TestDataSource.getInstance().getLink())
		{

			List<Map<String, Object>> results = connection
				.from(Select.from(Person.class)
					.properties("id", "name", "birthdate")
					.where(Condition.TRUE))
				.fetchMapList();

			results.forEach(e -> Assert.assertEquals(e.get("name"), String.format("Person %s", e.get("id").toString())));

		}
	}

	@AfterClass
	public static void clean()
	{
		TestDataSource.getInstance().clean();
	}

}
