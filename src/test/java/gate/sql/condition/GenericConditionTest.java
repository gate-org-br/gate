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
				.and("column3").when(true).eq()
				.and().not("column4").when(true).eq()
				.and("column3").when(false).eq()
				.and().not("column4").when(false).eq()
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
				.and("column3").when(true).ne()
				.and().not("column4").when(true).ne()
				.and("column3").when(false).ne()
				.and().not("column4").when(false).ne()
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
				.and("column3").when(true).lt()
				.and().not("column4").when(true).lt()
				.and("column3").when(false).lt()
				.and().not("column4").when(false).lt()
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
				.and("column3").when(true).le()
				.and().not("column4").when(true).le()
				.and("column3").when(false).le()
				.and().not("column4").when(false).le()
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
				.and("column3").when(true).gt()
				.and().not("column4").when(true).gt()
				.and("column3").when(false).gt()
				.and().not("column4").when(false).gt()
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
				.and("column3").when(true).ge()
				.and().not("column4").when(true).ge()
				.and("column3").when(false).ge()
				.and().not("column4").when(false).ge()
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
				.and("column3").when(true).lk()
				.and().not("column4").when(true).lk()
				.and("column3").when(false).lk()
				.and().not("column4").when(false).lk()
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
				.and("column3").when(true).rx()
				.and().not("column4").when(true).rx()
				.and("column3").when(false).rx()
				.and().not("column4").when(false).rx()
				.and("column7").rx();
		assertEquals("column1 rlike ? and not column2 rlike ? and column3 rlike ? and not column4 rlike ? and column7 rlike ?", condition.toString());
		assertTrue(condition.getParameters().collect(Collectors.toList()).isEmpty());
	}
}
