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

		Assert.assertEquals(2, database.size("table1"));
		Assert.assertEquals(2, database.size("table2"));
		Assert.assertEquals(4, database.size());

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

		Assert.assertEquals(2, database.size("table1"));
		Assert.assertEquals(1, database.size("table2"));
		Assert.assertEquals(3, database.size());

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

		Assert.assertEquals(2, database.search(e -> e.startsWith("table1"))
			.size());

		Assert.assertEquals(2, database.search(e -> e.endsWith("string1"))
			.size());
		database.drop();
	}
}
