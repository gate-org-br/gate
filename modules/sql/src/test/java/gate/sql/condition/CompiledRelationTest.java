package gate.sql.condition;

import mock.UserMock;
import gate.sql.select.Select;
import java.util.Arrays;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class CompiledRelationTest
{

	@Test
	public void shouldBuildNegatedCompiledRelations()
	{
		Condition condition = Condition
			.of("column1").eq(1)
			.and().not("column2").ne(2)
			.and().not("column3").eq(3)
			.and().not(Condition.of("column4").eq(4)
				.or().not("column5").eq(5));
		assertEquals("column1 = ? and not column2 <> ? and not column3 = ? and not (column4 = ? or not column5 = ?)", condition.toString());
		assertEquals(condition.getParameters().collect(Collectors.toList()), Arrays.asList(1, 2, 3, 4, 5));
	}

	@Test
	public void shouldBuildExistsRelation()
	{
		Condition condition = Condition
			.of("column1").eq(1)
			.and().exists(Select
				.expression("column2")
				.from("table1")
				.where(gate.sql.condition.Condition.of("column3").eq(2)));
		assertEquals("column1 = ? and exists (select column2 from table1 where column3 = ?)", condition.toString());
		assertEquals(condition.getParameters().collect(Collectors.toList()), Arrays.asList(1, 2));
	}

	@Test
	public void shouldBuildNotExistsRelation()
	{
		Condition condition = Condition
			.of("column1").eq(1)
			.and().not().exists(Select.expression("column2").from("table1")
				.where(gate.sql.condition.Condition.of("column3").eq(2)));
		assertEquals("column1 = ? and not exists (select column2 from table1 where column3 = ?)", condition.toString());
		assertEquals(condition.getParameters().collect(Collectors.toList()), Arrays.asList(1, 2));
	}

	@Test
	public void shouldBuildNegatedRelationFromCompiledBuilder()
	{
		Condition condition = Condition
				.of("column1").eq(1)
				.and().not(() -> gate.sql.statement.Query.of("select score from table2 where active = ?", java.util.List.of(9)))
				.eq(2);

		assertEquals("column1 = ? and  not (select score from table2 where active = ?) = ?", condition.toString());
		assertEquals(Arrays.asList(1, 9, 2), condition.getParameters().collect(Collectors.toList()));
	}

	@Test
	public void shouldBuildCompiledRelationFromPropertyReferences()
	{
		Condition condition = Condition
				.of("id").eq(1)
				.and().expression(UserMock::getName).eq(2)
				.and().not(UserMock::getActive).eq(3);

		assertEquals("id = ? and name = ? and not active = ?", condition.toString());
		assertEquals(Arrays.asList(1, 2, 3), condition.getParameters().collect(Collectors.toList()));
	}
}
