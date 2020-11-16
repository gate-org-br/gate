package gate.lang.expression;

import gate.error.ExpressionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class ExpressionTest
{

	public final Person person;
	private final List<Object> context = new ArrayList<>();
	private final Parameters parameters = new Parameters();

	public ExpressionTest()
	{
		person = new Person().setName("Pessoa 1")
			.setAge(64)
			.setWeight(80.5)
			.addChild(new Person()
				.setName("Filho 1 Pessoa 1")
				.setAge(35)
				.setWeight(60.0))
			.addChild(new Person()
				.setName("Filho 2 Pessoa 1")
				.setAge(25)
				.setWeight(50.0))
			.addRelacionamento("wife", new Person()
				.setAge(56)
				.setName("Esposa Pessoa 1")
				.setWeight(65.7));

		context.add(person);
		parameters.put("idade", 1);
	}

	@Test
	public void test1() throws ExpressionException
	{
		Assert.assertEquals(Expression.of("age > 50").evaluate(person), Boolean.TRUE);
	}

	@Test
	public void test2() throws ExpressionException
	{
		Assert.assertEquals(Expression.of("relationships.wife.age > 60").evaluate(person), Boolean.FALSE);
	}

	@Test
	public void test3() throws ExpressionException
	{
		Assert.assertEquals(Expression.of("relationships.wife.getAge() > 60").evaluate(person), Boolean.FALSE);
	}

	@Test
	public void test4() throws ExpressionException
	{
		Assert.assertEquals(Expression.of("age + relationships.wife.age == 120").evaluate(person), Boolean.TRUE);
	}

	@Test
	public void test5() throws ExpressionException
	{
		Assert.assertEquals(2, (int) Expression.of("size children").evaluate(person));
	}

	@Test
	public void test6() throws ExpressionException
	{
		Assert.assertEquals(60, (int) Expression.of("children[0].age + children[1].age").evaluate(person));
	}

	@Test
	public void test7() throws ExpressionException
	{
		Assert.assertEquals(60, (int) Expression.of("(age + relationships.wife.age) / 2").evaluate(person));
	}

	@Test
	public void test8() throws ExpressionException
	{
		Assert.assertEquals(Boolean.TRUE, Expression.of(
			"(age + relationships.wife.age) / 2 eq children[0].age + children[1].age")
			.evaluate(person));
	}

	@Test
	public void test9() throws ExpressionException
	{
		Assert.assertEquals(1, (int) Expression.of("@idade").evaluate(context, parameters));
	}

	@Test
	public void test11() throws ExpressionException
	{
		Object result = Expression.of("multiply(getAge() + 1)").evaluate(context, parameters);
		Assert.assertEquals(130, (int) result);
	}

	@Test
	public void test12() throws ExpressionException
	{
		Object result = Expression.of("(size children + 5) * 2")
			.evaluate(context, parameters);
		Assert.assertEquals(14, (int) result);
	}

	@Test
	public void testRx() throws ExpressionException
	{
		Assert.assertEquals(Boolean.TRUE,
			Expression.of("this rx '^[0-9]{3}$'")
				.evaluate("123"));
	}
	
	@Test(expected = ExpressionException.class)
	public void testRxError() throws ExpressionException
	{
		Assert.assertEquals(Boolean.TRUE,
			Expression.of("this rx 123")
				.evaluate("123"));
	}

	public static class Person
	{

		private Double weight;
		private String name;
		private Integer age;
		private List<Person> children;
		private Map<String, Person> relationships;

		public Double getWeight()
		{
			return weight;
		}

		public Person setWeight(Double weight)
		{
			this.weight = weight;
			return this;
		}

		public String getName()
		{
			return name;
		}

		public Person setName(String name)
		{
			this.name = name;
			return this;
		}

		public Integer getAge()
		{
			return age;
		}

		public Integer multiply(Integer age)
		{
			return age * 2;
		}

		public Person setAge(Integer age)
		{
			this.age = age;
			return this;
		}

		public List<Person> getChildren()
		{
			if (children == null)
				children = new ArrayList<>();
			return children;
		}

		public Person setChildren(List<Person> children)
		{
			this.children = children;
			return this;
		}

		public Map<String, Person> getRelationships()
		{
			if (relationships == null)
				relationships = new HashMap<>();
			return relationships;
		}

		public Person setRelationships(Map<String, Person> relationships)
		{
			this.relationships = relationships;
			return this;
		}

		public Person addRelacionamento(String relationships, Person person)
		{
			getRelationships().put(relationships, person);
			return this;
		}

		public Person addChild(Person person)
		{
			getChildren().add(person);
			return this;
		}
	}
}
