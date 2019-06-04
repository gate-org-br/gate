package gate.sql.condition;

import gate.sql.select.Select;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;

public class CompiledRelationTest
{

	@Test
	public void testNot()
	{
		Condition condition = Condition
			.of("column1").eq(1)
			.and().not("column2").ne(2)
			.and().not("column3").eq(3)
			.and().not(Condition.of("column4").eq(4)
				.or().not("column5").eq(5));
		Assert.assertEquals("column1 = ? and not column2 <> ? and not column3 = ? and not (column4 = ? or not column5 = ?)", condition.toString());
		Assert.assertEquals(condition.getParameters().collect(Collectors.toList()), Arrays.asList(1, 2, 3, 4, 5));
	}

	@Test
	public void testExists()
	{
		Condition condition = Condition
			.of("column1").eq(1)
			.and().exists(Select
				.expression("column2")
				.from("table1")
				.where(gate.sql.condition.Condition.of("column3").eq(2)));
		Assert.assertEquals("column1 = ? and exists (select column2 from table1 where column3 = ?)", condition.toString());
		Assert.assertEquals(condition.getParameters().collect(Collectors.toList()), Arrays.asList(1, 2));
	}

	@Test
	public void testNotExists()
	{
		Condition condition = Condition
			.of("column1").eq(1)
			.and().not().exists(Select.expression("column2").from("table1")
				.where(gate.sql.condition.Condition.of("column3").eq(2)));
		Assert.assertEquals("column1 = ? and not exists (select column2 from table1 where column3 = ?)", condition.toString());
		Assert.assertEquals(condition.getParameters().collect(Collectors.toList()), Arrays.asList(1, 2));
	}
}
