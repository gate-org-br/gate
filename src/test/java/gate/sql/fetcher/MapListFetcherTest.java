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
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MapListFetcherTest
{

	@BeforeEach
	public void setUp() throws ConstraintViolationException, SQLException
	{
		TestDataSource.setUp();
	}

	@Test
	public void test01() throws SQLException
	{
		try (Link connection = TestDataSource.INSTANCE.getLink())
		{

			List<Map<String, Object>> results = connection
				.from(Select.from(Person.class)
					.properties("id", "name", "birthdate")
					.where(Condition.TRUE))
				.fetchMapList();

			results.forEach(e -> assertEquals(e.get("name"), String.format("Person %s", e.get("id").toString())));

		}
	}

}
