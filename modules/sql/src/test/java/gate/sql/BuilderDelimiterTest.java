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
	public void shouldBuildStatementsWithoutIdentifierDelimiterByDefault()
	{
		assertEquals("insert into Uzer (id) values (?)",
				Insert.into("Uzer").set("id", 1).toString());
		assertEquals("update Uzer set name = ?",
				Update.table("Uzer").set("name", "Alice").toString());
		assertEquals("replace into Uzer (id) values (?)",
				Replace.into("Uzer").set("id", 1).toString());
		assertEquals("delete from Uzer",
				Delete.from("Uzer").build().toString());
	}
}
