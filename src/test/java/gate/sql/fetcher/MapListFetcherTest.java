package gate.sql.fetcher;

import gate.Person;
import gate.error.ConstraintViolationException;
import gate.sql.Link;
import gate.sql.TestDataSource;
import gate.sql.condition.Condition;
import gate.sql.select.Select;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class MapListFetcherTest
{

	@BeforeAll
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

			results.forEach(e -> assertEquals(e.get("name"), String.format("Person %s", e.get("id").toString())));

		}
	}

	@AfterAll
	public static void clean()
	{
		TestDataSource.getInstance().clean();
	}

}
