package gate.io;

import java.io.File;
import org.junit.Assert;
import org.junit.Test;

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

		Assert.assertTrue(database.size("table1") == 2);
		Assert.assertTrue(database.size("table2") == 2);
		Assert.assertTrue(database.size() == 4);

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

		Assert.assertTrue(database.size("table1") == 2);
		Assert.assertTrue(database.size("table2") == 1);
		Assert.assertTrue(database.size() == 3);

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

		Assert.assertTrue(database.search(e -> e.startsWith("table1"))
			.size() == 2);

		Assert.assertTrue(database.search(e -> e.endsWith("string1"))
			.size() == 2);
		database.drop();
	}
}
