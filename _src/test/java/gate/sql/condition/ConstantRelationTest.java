package gate.sql.condition;

import gate.sql.select.Select;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;

public class ConstantRelationTest
{

	@Test
	public void testNot()
	{
		Condition condition = Condition
			.of("column1").isEq("column2")
			.and().not("column3").isEq("column4")
			.and().not("column5").isEq("column6")
			.and().not(Condition.of("column7").isEq("column8")
				.or().not("column9").isEq("column10"));
		Assert.assertEquals("column1 = column2 and not column3 = column4 and not column5 = column6 and not (column7 = column8 or not column9 = column10)", condition.toString());
		Assert.assertTrue(condition.getParameters().collect(Collectors.toList()).isEmpty());
	}

	@Test
	public void testRollbackExists()
	{
		Condition condition = Condition
			.of("column0").isNull()
			.and().when(false).exists(Select.expression("column1").from("table1"))
			.and().when(true).exists(Select.expression("column2").from("table2"))
			.and().not().when(false).exists(Select.expression("column3").from("table3"))
			.and().when(true).not().exists(Select.expression("column4").from("table4"));
		Assert.assertEquals("column0 is null and exists (select column2 from table2) and not exists (select column4 from table4)", condition.toString());
		Assert.assertTrue(condition.getParameters().collect(Collectors.toList()).isEmpty());
	}

}
