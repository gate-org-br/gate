package gate.sql;

import gate.sql.delete.Delete;
import gate.sql.insert.Insert;
import gate.sql.replace.Replace;
import gate.sql.update.Update;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuilderDelimiterTest
{
	@Test
	public void testBuildersDefaultToNoDelimiter()
	{
		assertEquals("insert into Person (id) values (?)",
				Insert.into("Person").set("id", 1).toString());
		assertEquals("update Person set name = ?",
				Update.table("Person").set("name", "Alice").toString());
		assertEquals("replace into Person (id) values (?)",
				Replace.into("Person").set("id", 1).toString());
		assertEquals("delete from Person",
				Delete.from("Person").build().toString());
	}
}