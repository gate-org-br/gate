package gate.sql.fetcher;

import gate.error.ConstraintViolationException;
import gate.sql.Link;
import gate.sql.TestDataSource;
import gate.sql.condition.Condition;
import gate.sql.select.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapListFetcherTest
{

	@BeforeEach
	public void setUp() throws ConstraintViolationException, SQLException
	{
		TestDataSource.setUp();
	}

	@Test
	public void shouldFetchRowsAsMapList() throws SQLException
	{
		try (Link connection = TestDataSource.getLink())
		{

			List<Map<String, Object>> results = connection
					.from(Select.expression("id")
							.expression("name")
							.expression("birthdate")
							.from("Uzer")
							.where(Condition.TRUE))
					.fetchMapList();

			results.forEach(e -> assertEquals(e.get("name"), String.format("User %s", e.get("id").toString())));

		}
	}

}