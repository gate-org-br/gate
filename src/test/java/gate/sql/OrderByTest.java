package gate.sql;

import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class OrderByTest
{

	@Test
	public void test01()
	{
		assertEquals("name asc, age desc",
			OrderBy.asc("name").desc("age").toString());
	}

	@Test
	public void test02()
	{
		assertEquals("name1 asc, name2 asc, age1 desc, age2 desc",
			OrderBy.asc("name").desc("age")
				.toString(e -> Stream.of(e + "1", e + "2")));
	}

	@Test
	public void test03()
	{
		assertEquals("name asc, age asc",
			OrderBy.asc("name", "age").toString());
	}

	@Test
	public void test04()
	{
		assertEquals("name desc, age desc",
			OrderBy.desc("name", "age").toString());
	}

}
