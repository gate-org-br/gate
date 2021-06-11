package gate.sql.condition;

import gate.entity.User;
import gate.lang.property.Entity;
import gate.lang.property.Property;
import gate.sql.GQN;
import gate.type.ID;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class CompiledConditionTest
{

	@Test
	public void testEq_Object()
	{
		Condition condition = Condition
			.of("column1").eq(1)
			.and().not("column2").eq(2)
			.and().when(true).expression("column3").eq(3)
			.and().when(true).not("column4").eq(4)
			.and().when(false).expression("column3").eq(5)
			.and().when(false).not("column4").eq(6)
			.and("column7").eq(7)
			.and("column8").eq(null)
			.and().not("column9").eq(null);
		assertEquals("column1 = ? and not column2 = ? and column3 = ? and not column4 = ? and column7 = ?", condition.toString());
		assertEquals(condition.getParameters().collect(Collectors.toList()), Arrays.asList(1, 2, 3, 4, 7));
	}

	@Test
	public void testNe_Object()
	{
		Condition condition = Condition
			.of("column1").ne(1)
			.and().not("column2").ne(2)
			.and().when(true).expression("column3").ne(3)
			.and().when(true).not("column4").ne(4)
			.and().when(false).expression("column3").ne(5)
			.and().when(false).not("column4").ne(6)
			.and("column7").ne(7)
			.and("column8").ne(null)
			.and().not("column9").ne(null);
		assertEquals("column1 <> ? and not column2 <> ? and column3 <> ? and not column4 <> ? and column7 <> ?", condition.toString());
		assertEquals(condition.getParameters().collect(Collectors.toList()), Arrays.asList(1, 2, 3, 4, 7));
	}

	@Test
	public void testLt_Object()
	{
		Condition condition = Condition
			.of("column1").lt(1)
			.and().not("column2").lt(2)
			.and().when(true).expression("column3").lt(3)
			.and().when(true).not("column4").lt(4)
			.and().when(false).expression("column3").lt(5)
			.and().when(false).not("column4").lt(6)
			.and("column7").lt(7)
			.and("column8").lt(null)
			.and().not("column9").lt(null);
		assertEquals("column1 < ? and not column2 < ? and column3 < ? and not column4 < ? and column7 < ?", condition.toString());
		assertEquals(condition.getParameters().collect(Collectors.toList()), Arrays.asList(1, 2, 3, 4, 7));
	}

	@Test
	public void testLe_Object()
	{
		Condition condition = Condition
			.of("column1").le(1)
			.and().not("column2").le(2)
			.and().when(true).expression("column3").le(3)
			.and().when(true).not("column4").le(4)
			.and().when(false).expression("column3").le(5)
			.and().when(false).not("column4").le(6)
			.and("column7").le(7)
			.and("column8").le(null)
			.and().not("column9").le(null);
		assertEquals("column1 <= ? and not column2 <= ? and column3 <= ? and not column4 <= ? and column7 <= ?", condition.toString());
		assertEquals(condition.getParameters().collect(Collectors.toList()), Arrays.asList(1, 2, 3, 4, 7));
	}

	@Test
	public void testGt_Object()
	{
		Condition condition = Condition
			.of("column1").gt(1)
			.and().not("column2").gt(2)
			.and().when(true).expression("column3").gt(3)
			.and().when(true).not("column4").gt(4)
			.and().when(false).expression("column3").gt(5)
			.and().when(false).not("column4").gt(6)
			.and("column7").gt(7)
			.and("column8").gt(null)
			.and().not("column9").gt(null);
		assertEquals("column1 > ? and not column2 > ? and column3 > ? and not column4 > ? and column7 > ?", condition.toString());
		assertEquals(condition.getParameters().collect(Collectors.toList()), Arrays.asList(1, 2, 3, 4, 7));
	}

	@Test
	public void testGe_Object()
	{
		Condition condition = Condition
			.of("column1").ge(1)
			.and().not("column2").ge(2)
			.and().when(true).expression("column3").ge(3)
			.and().when(true).not("column4").ge(4)
			.and().when(false).expression("column3").ge(5)
			.and().when(false).not("column4").ge(6)
			.and("column7").ge(7)
			.and("column8").ge(null)
			.and().not("column9").ge(null);
		assertEquals("column1 >= ? and not column2 >= ? and column3 >= ? and not column4 >= ? and column7 >= ?", condition.toString());
		assertEquals(condition.getParameters().collect(Collectors.toList()), Arrays.asList(1, 2, 3, 4, 7));
	}

	@Test
	public void testLk_Object()
	{
		Condition condition = Condition
			.of("column1").lk(1)
			.and().not("column2").lk(2)
			.and().when(true).expression("column3").lk(3)
			.and().when(true).not("column4").lk(4)
			.and().when(false).expression("column3").lk(5)
			.and().when(false).not("column4").lk(6)
			.and("column7").lk(7)
			.and("column8").lk(null)
			.and().not("column9").lk(null);
		assertEquals("column1 like ? and not column2 like ? and column3 like ? and not column4 like ? and column7 like ?", condition.toString());
		assertEquals(condition.getParameters().collect(Collectors.toList()), Arrays.asList("%1%", "%2%", "%3%", "%4%", "%7%"));
	}

	@Test
	public void testRx_Object()
	{
		Condition condition = Condition
			.of("column1").rx(1)
			.and().not("column2").rx(2)
			.and().when(true).expression("column3").rx(3)
			.and().when(true).not("column4").rx(4)
			.and().when(false).expression("column3").rx(5)
			.and().when(false).not("column4").rx(6)
			.and("column7").rx(7)
			.and("column8").rx(null)
			.and().not("column9").rx(null);
		assertEquals("column1 rlike ? and not column2 rlike ? and column3 rlike ? and not column4 rlike ? and column7 rlike ?", condition.toString());
		assertEquals(condition.getParameters().collect(Collectors.toList()), Arrays.asList("1", "2", "3", "4", "7"));
	}

	@Test
	public void test01()
	{
		CompiledCondition condition = Condition.of("id")
			.eq(1).and("name").eq("Person 1").and("birthdate").gt(null);

		List<Object> parameters = condition.getParameters().collect(Collectors.toList());
		Assert.assertEquals("id = ? and name = ?", condition.toString());
		assertEquals(2, parameters.size());
		assertEquals(1, parameters.get(0));
		assertEquals("Person 1", parameters.get(1));
	}

	@Test
	public void test02()
	{
		CompiledCondition condition = Condition.of("id")
			.eq(1).or("name").eq("Person 1").or("birthdate").gt(null);

		List<Object> parameters = condition.getParameters().collect(Collectors.toList());
		Assert.assertEquals("id = ? or name = ?", condition.toString());
		assertEquals(2, parameters.size());
		assertEquals(1, parameters.get(0));
		assertEquals("Person 1", parameters.get(1));
	}

	@Test
	public void testAndPropertyEquals()
	{
		Condition condition = Condition
			.of(Entity.getFullColumnName(Property.getProperty(User.class, "id"))).eq(new ID(1))
			.and(Entity.getFullColumnName(Property.getProperty(User.class, "name"))).eq("Person 1")
			.and(Entity.getFullColumnName(Property.getProperty(User.class, "role.id"))).eq(new ID(2))
			.and(Entity.getFullColumnName(Property.getProperty(User.class, "role.name"))).eq(null);
		Assert.assertEquals("Uzer.id = ? and Uzer.name = ? and Uzer$Role.id = ?", condition.toString());
		assertEquals(condition.getParameters().collect(Collectors.toList()), Arrays.asList(new ID(1), "Person 1", new ID(2)));
	}

	@Test
	public void testOrPropertyEquals()
	{
		CompiledCondition condition = Condition
			.of(Entity.getFullColumnName(Property.getProperty(User.class, "id"))).eq(new ID(1))
			.or(Entity.getFullColumnName(Property.getProperty(User.class, "name"))).eq("Person 1")
			.or(Entity.getFullColumnName(Property.getProperty(User.class, "role.id"))).eq(new ID(2))
			.or(Entity.getFullColumnName(Property.getProperty(User.class, "role.name"))).eq(null);
		Assert.assertEquals("Uzer.id = ? or Uzer.name = ? or Uzer$Role.id = ?", condition.toString());
		assertEquals(condition.getParameters().collect(Collectors.toList()), Arrays.asList(new ID(1), "Person 1", new ID(2)));
	}

	@Test
	public void test05()
	{
		GQN GQN = new GQN(User.class, "=id", "%name", "=email");
		CompiledCondition condition
			= GQN.getCondition(new User()
				.setId(new ID(1))
				.setName("Person 1"));

		String expected = "0 = 0 and Uzer.id = ? and Uzer.name like ?";
		String result = condition.toString();

		List<Object> parameters = condition.getParameters().collect(Collectors.toList());
		Assert.assertEquals(expected, result);
		assertEquals(2, parameters.size());
		assertEquals(parameters.get(0), new ID(1));
		assertEquals("%Person 1%", parameters.get(1));
	}

	@Test
	public void test06()
	{
		try
		{
			Condition.of("name").eq(User.class, null);
			Assert.fail();
		} catch (Exception e)
		{
		}
	}

	@Test
	public void test07()
	{
		CompiledCondition condition
			= Condition.when(false)
				.expression("id").eq(1)
				.and("name").eq("Person 1")
				.and("birthdate").gt(null);

		List<Object> parameters = condition.getParameters().collect(Collectors.toList());
		Assert.assertEquals("name = ?", condition.toString());
		assertEquals(1, parameters.size());
		assertEquals("Person 1", parameters.get(0));
	}

	@Test
	public void test08()
	{
		CompiledCondition condition = Condition
			.of(Entity.getFullColumnName(Property.getProperty(User.class, "id"))).eq(new ID(1))
			.and().when(false).expression(Entity.getFullColumnName(Property.getProperty(User.class, "name"))).eq("Person 1")
			.and().when(true).expression(Entity.getFullColumnName(Property.getProperty(User.class, "role.id"))).eq(new ID(2))
			.and(Entity.getFullColumnName(Property.getProperty(User.class, "role.name"))).eq(null);

		List<Object> parameters = condition.getParameters().collect(Collectors.toList());
		Assert.assertEquals("Uzer.id = ? and Uzer$Role.id = ?",
			condition.toString());
		assertEquals(2, parameters.size());
		assertEquals(parameters.get(0), new ID(1));
		assertEquals(parameters.get(1), new ID(2));
	}

}
