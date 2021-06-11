package gate.sql.condition;

import java.util.stream.Collectors;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class GenericConditionTest
{

	@Test
	public void testEq()
	{
		Condition condition = Condition
			.of("column1").eq()
			.and().not("column2").eq()
			.and().when(true).expression("column3").eq()
			.and().when(true).not("column4").eq()
			.and().when(false).expression("column3").eq()
			.and().when(false).not("column4").eq()
			.and("column7").eq();
		assertEquals("column1 = ? and not column2 = ? and column3 = ? and not column4 = ? and column7 = ?", condition.toString());
		assertTrue(condition.getParameters().collect(Collectors.toList()).isEmpty());
	}

	@Test
	public void testNe()
	{
		Condition condition = Condition
			.of("column1").ne()
			.and().not("column2").ne()
			.and().when(true).expression("column3").ne()
			.and().when(true).not("column4").ne()
			.and().when(false).expression("column3").ne()
			.and().when(false).not("column4").ne()
			.and("column7").ne();
		assertEquals("column1 <> ? and not column2 <> ? and column3 <> ? and not column4 <> ? and column7 <> ?", condition.toString());
		assertTrue(condition.getParameters().collect(Collectors.toList()).isEmpty());
	}

	@Test
	public void testLt()
	{
		Condition condition = Condition
			.of("column1").lt()
			.and().not("column2").lt()
			.and().when(true).expression("column3").lt()
			.and().when(true).not("column4").lt()
			.and().when(false).expression("column3").lt()
			.and().when(false).not("column4").lt()
			.and("column7").lt();
		assertEquals("column1 < ? and not column2 < ? and column3 < ? and not column4 < ? and column7 < ?", condition.toString());
		assertTrue(condition.getParameters().collect(Collectors.toList()).isEmpty());
	}

	@Test
	public void testLe()
	{
		Condition condition = Condition
			.of("column1").le()
			.and().not("column2").le()
			.and().when(true).expression("column3").le()
			.and().when(true).not("column4").le()
			.and().when(false).expression("column3").le()
			.and().when(false).not("column4").le()
			.and("column7").le();
		assertEquals("column1 <= ? and not column2 <= ? and column3 <= ? and not column4 <= ? and column7 <= ?", condition.toString());
		assertTrue(condition.getParameters().collect(Collectors.toList()).isEmpty());
	}

	@Test
	public void testGt()
	{
		Condition condition = Condition
			.of("column1").gt()
			.and().not("column2").gt()
			.and().when(true).expression("column3").gt()
			.and().when(true).not("column4").gt()
			.and().when(false).expression("column3").gt()
			.and().when(false).not("column4").gt()
			.and("column7").gt();
		assertEquals("column1 > ? and not column2 > ? and column3 > ? and not column4 > ? and column7 > ?", condition.toString());
		assertTrue(condition.getParameters().collect(Collectors.toList()).isEmpty());
	}

	@Test
	public void testGe()
	{
		Condition condition = Condition
			.of("column1").ge()
			.and().not("column2").ge()
			.and().when(true).expression("column3").ge()
			.and().when(true).not("column4").ge()
			.and().when(false).expression("column3").ge()
			.and().when(false).not("column4").ge()
			.and("column7").ge();
		assertEquals("column1 >= ? and not column2 >= ? and column3 >= ? and not column4 >= ? and column7 >= ?", condition.toString());
		assertTrue(condition.getParameters().collect(Collectors.toList()).isEmpty());
	}

	@Test
	public void testLk()
	{
		Condition condition = Condition
			.of("column1").lk()
			.and().not("column2").lk()
			.and().when(true).expression("column3").lk()
			.and().when(true).not("column4").lk()
			.and().when(false).expression("column3").lk()
			.and().when(false).not("column4").lk()
			.and("column7").lk();
		assertEquals("column1 like ? and not column2 like ? and column3 like ? and not column4 like ? and column7 like ?", condition.toString());
		assertTrue(condition.getParameters().collect(Collectors.toList()).isEmpty());
	}

	@Test
	public void testRx()
	{
		Condition condition = Condition
			.of("column1").rx()
			.and().not("column2").rx()
			.and().when(true).expression("column3").rx()
			.and().when(true).not("column4").rx()
			.and().when(false).expression("column3").rx()
			.and().when(false).not("column4").rx()
			.and("column7").rx();
		assertEquals("column1 rlike ? and not column2 rlike ? and column3 rlike ? and not column4 rlike ? and column7 rlike ?", condition.toString());
		assertTrue(condition.getParameters().collect(Collectors.toList()).isEmpty());
	}
}
