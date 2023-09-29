package gate.io;

import java.io.File;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class DatabaseTest
{

	@Test
	public void testInsert()
	{
		Database database = Database.of(String.class, new File("Database"));
		database.insert("table1", "table1.string1");
		database.insert("table1", "table1.string2");

		database.insert("table2", "table2.string1");
		database.insert("table2", "table2.string2");

		assertEquals(2, database.size("table1"));
		assertEquals(2, database.size("table2"));
		assertEquals(4, database.size());

		database.drop();
	}

	@Test
	public void testDelete()
	{
		Database database = Database.of(String.class, new File("Database"));
		database.insert("table1", "table1.string1");
		database.insert("table1", "table1.string2");
		database.insert("table2", "table2.string1");
		database.insert("table2", "table2.string2");

		database.delete("table2", "table2.string2");

		assertEquals(2, database.size("table1"));
		assertEquals(1, database.size("table2"));
		assertEquals(3, database.size());

		database.drop();
	}

	@Test
	public void testSearch()
	{
		Database<String> database = Database.of(String.class, new File("Database"));
		database.insert("table1", "table1.string1");
		database.insert("table1", "table1.string2");
		database.insert("table2", "table2.string1");
		database.insert("table2", "table2.string2");

		assertEquals(2, database.search(e -> e.startsWith("table1"))
			.size());

		assertEquals(2, database.search(e -> e.endsWith("string1"))
			.size());
		database.drop();
	}
}
