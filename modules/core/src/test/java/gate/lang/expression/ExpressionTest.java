package gate.lang.expression;

import gate.error.ExpressionException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
	public void shouldEvaluateSimpleGreaterThanComparison() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("age > 50").evaluate(person));
	}

	@Test
	public void shouldEvaluateNestedPropertyComparison() throws ExpressionException
	{
		assertEquals(Boolean.FALSE, Expression.valueOf("relationships.wife.age > 60").evaluate(person));
	}

	@Test
	public void shouldEvaluateMethodInvocationComparison() throws ExpressionException
	{
		assertEquals(Boolean.FALSE, Expression.valueOf("relationships.wife.getAge() > 60").evaluate(person));
	}

	@Test
	public void shouldEvaluateArithmeticWithNestedProperty() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("age + relationships.wife.age == 120").evaluate(person));
	}

	@Test
	public void shouldReturnCollectionSize() throws ExpressionException
	{
		assertEquals(2, (int) Expression.valueOf("size children").evaluate(person));
	}

	@Test
	public void shouldEvaluateIndexedCollectionArithmetic() throws ExpressionException
	{
		assertEquals(60, (int) Expression.valueOf("children[0].age + children[1].age").evaluate(person));
	}

	@Test
	public void shouldRespectParenthesesInArithmeticExpression() throws ExpressionException
	{
		assertEquals(60, (int) Expression.valueOf("(age + relationships.wife.age) / 2").evaluate(person));
	}

	@Test
	public void shouldCompareArithmeticExpressionsUsingEqOperator() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf(
						"(age + relationships.wife.age) / 2 eq children[0].age + children[1].age")
				.evaluate(person));
	}

	@Test
	public void shouldResolveParameterValue() throws ExpressionException
	{
		assertEquals(1, (int) Expression.valueOf("@idade").evaluate(context, parameters));
	}

	@Test
	public void shouldEvaluateMethodCallWithArgumentExpression() throws ExpressionException
	{
		Object result = Expression.valueOf("multiply(getAge() + 1)").evaluate(context, parameters);
		assertEquals(130, (int) result);
	}

	@Test
	public void shouldEvaluateNestedArithmeticExpression() throws ExpressionException
	{
		Object result = Expression.valueOf("(size children + 5) * 2").evaluate(context, parameters);
		assertEquals(14, (int) result);
	}

	@Test
	public void shouldEvaluateAndBeforeOr() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("true or false and false").evaluate(person));
	}

	@Test
	public void shouldPreserveBooleanPrecedenceAcrossMultipleOperators() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("true or false or true and false or true or false").evaluate(person));
	}

	@Test
	public void shouldEvaluateRegexMatch() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("this rx '^[0-9]{3}$'").evaluate("123"));
	}

	@Test
	public void shouldThrowWhenRegexOperandIsNotString()
	{
		assertThrows(ExpressionException.class, () -> Expression.valueOf("this rx 123").evaluate("123"));
	}

	@Test
	public void shouldEvaluateNotTrue() throws ExpressionException
	{
		assertEquals(Boolean.FALSE, Expression.valueOf("not true").evaluate(person));
	}

	@Test
	public void shouldEvaluateNotFalse() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("not false").evaluate(person));
	}

	@Test
	public void shouldEvaluateDoubleNot() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("not not true").evaluate(person));
	}

	@Test
	public void shouldThrowWhenNotOperandIsNotBoolean()
	{
		assertThrows(ExpressionException.class, () -> Expression.valueOf("not age").evaluate(person));
	}

	@Test
	public void shouldReturnTrueForEmptyString() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("empty this").evaluate(""));
	}

	@Test
	public void shouldReturnFalseForNonEmptyString() throws ExpressionException
	{
		assertEquals(Boolean.FALSE, Expression.valueOf("empty name").evaluate(person));
	}

	@Test
	public void shouldReturnTrueForEmptyCollection() throws ExpressionException
	{
		Person p = new Person();
		assertEquals(Boolean.TRUE, Expression.valueOf("empty children").evaluate(p));
	}

	@Test
	public void shouldReturnFalseForNonEmptyCollection() throws ExpressionException
	{
		assertEquals(Boolean.FALSE, Expression.valueOf("empty children").evaluate(person));
	}

	@Test
	public void shouldReturnTrueForEmptyMap() throws ExpressionException
	{
		Person p = new Person();
		assertEquals(Boolean.TRUE, Expression.valueOf("empty relationships").evaluate(p));
	}

	@Test
	public void shouldReturnFalseForNonEmptyMap() throws ExpressionException
	{
		assertEquals(Boolean.FALSE, Expression.valueOf("empty relationships").evaluate(person));
	}

	@Test
	public void shouldThrowWhenEmptyOperandIsInvalidType()
	{
		assertThrows(ExpressionException.class, () -> Expression.valueOf("empty age").evaluate(person));
	}

	@Test
	public void shouldReturnSizeOfString() throws ExpressionException
	{
		assertEquals(3, (int) Expression.valueOf("size this").evaluate("abc"));
	}

	@Test
	public void shouldReturnSizeOfCollection() throws ExpressionException
	{
		assertEquals(2, (int) Expression.valueOf("size children").evaluate(person));
	}

	@Test
	public void shouldReturnSizeOfMap() throws ExpressionException
	{
		assertEquals(1, (int) Expression.valueOf("size relationships").evaluate(person));
	}

	@Test
	public void shouldThrowWhenSizeOperandIsInvalidType()
	{
		assertThrows(ExpressionException.class, () -> Expression.valueOf("size age").evaluate(person));
	}

	@Test
	public void shouldEvaluateUnaryMinus() throws ExpressionException
	{
		assertEquals(-64, (int) Expression.valueOf("-age").evaluate(person));
	}

	@Test
	public void shouldEvaluateUnaryPlus() throws ExpressionException
	{
		assertEquals(64, (int) Expression.valueOf("+age").evaluate(person));
	}

	@Test
	public void shouldEvaluateLessThan() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("age < 70").evaluate(person));
	}

	@Test
	public void shouldEvaluateLessThanOrEqual() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("age <= 64").evaluate(person));
	}

	@Test
	public void shouldEvaluateGreaterThanOrEqual() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("age >= 64").evaluate(person));
	}

	@Test
	public void shouldEvaluateNotEqual() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("age != 50").evaluate(person));
	}

	@Test
	public void shouldThrowWhenComparingNonComparable()
	{
		assertThrows(ExpressionException.class, () -> Expression.valueOf("children > 1").evaluate(person));
	}

	@Test
	public void shouldThrowWhenAndOperandIsNotBoolean()
	{
		assertThrows(ExpressionException.class, () -> Expression.valueOf("age and true").evaluate(person));
	}

	@Test
	public void shouldThrowWhenOrOperandIsNotBoolean()
	{
		assertThrows(ExpressionException.class, () -> Expression.valueOf("age or true").evaluate(person));
	}

	@Test
	public void shouldReturnEmptyStringForUnknownParameter() throws ExpressionException
	{
		assertEquals("", Expression.valueOf("@inexistente").evaluate(context, parameters));
	}

	@Test
	public void shouldEvaluateParameterWithNestedProperty() throws ExpressionException
	{
		Parameters params = new Parameters();
		params.put("pessoa", person);
		assertEquals("Pessoa 1", Expression.valueOf("@pessoa.name").evaluate(context, params));
	}

	@Test
	public void shouldAccessMapEntryByStringIndex() throws ExpressionException
	{
		assertEquals("Esposa Pessoa 1", Expression.valueOf("relationships['wife'].name").evaluate(person));
	}

	@Test
	public void shouldThrowOnUnclosedParentheses()
	{
		assertThrows(ExpressionException.class, () -> Expression.valueOf("(age + 1").evaluate(person));
	}

	// -------------------------------------------------------------------------
	// coalesce (??)
	// -------------------------------------------------------------------------

	@Test
	public void shouldReturnLeftWhenNotNull() throws ExpressionException
	{
		assertEquals("Pessoa 1", Expression.valueOf("name ?? 'default'").evaluate(person));
	}

	@Test
	public void shouldReturnRightWhenLeftIsNull() throws ExpressionException
	{
		Person p = new Person();
		assertEquals("default", Expression.valueOf("name ?? 'default'").evaluate(p));
	}

	@Test
	public void shouldReturnRightWhenLeftIsEmptyString() throws ExpressionException
	{
		assertEquals("fallback", Expression.valueOf("@inexistente ?? 'fallback'").evaluate(context, parameters));
	}

	@Test
	public void shouldChainCoalesce() throws ExpressionException
	{
		Person p = new Person();
		Parameters params = new Parameters();
		List<Object> ctx = new ArrayList<>(List.of(p));
		assertEquals("ultimo", Expression.valueOf("@a ?? @b ?? 'ultimo'").evaluate(ctx, params));
	}

	@Test
	public void shouldReturnFirstNonNullInChain() throws ExpressionException
	{
		Parameters params = new Parameters();
		params.put("b", "achei");
		assertEquals("achei", Expression.valueOf("@a ?? @b ?? 'ultimo'").evaluate(context, params));
	}

	@Test
	public void shouldCoalesceWithArithmeticOnRight() throws ExpressionException
	{
		Person p = new Person();
		assertEquals(10, (int) Expression.valueOf("age ?? 5 + 5").evaluate(p));
	}

	@Test
	public void shouldNotCoalesceWhenLeftIsZero() throws ExpressionException
	{
		Person p = new Person().setAge(0);
		assertEquals(0, (int) Expression.valueOf("age ?? 99").evaluate(p));
	}

	// -------------------------------------------------------------------------
	// mod (%)
	// -------------------------------------------------------------------------

	@Test
	public void shouldCalculateModInteger() throws ExpressionException
	{
		assertEquals(1, (int) Expression.valueOf("age % 7").evaluate(person)); // 64 % 7 = 1
	}

	@Test
	public void shouldReturnZeroWhenDivisible() throws ExpressionException
	{
		assertEquals(0, (int) Expression.valueOf("age % 8").evaluate(person)); // 64 % 8 = 0
	}

	@Test
	public void shouldCalculateModDouble() throws ExpressionException
	{
		assertEquals(0.5, (double) Expression.valueOf("weight % 10").evaluate(person), 0.001); // 80.5 % 10 = 0.5
	}

	@Test
	public void shouldUseModInComparison() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("age % 2 == 0").evaluate(person)); // 64 é par
	}

	@Test
	public void shouldThrowWhenModOperandIsIncompatible()
	{
		assertThrows(ExpressionException.class, () -> Expression.valueOf("name % 2").evaluate(person));
	}

	// -------------------------------------------------------------------------
// between (bw)
// -------------------------------------------------------------------------

	@Test
	public void shouldReturnTrueWhenValueIsWithinRange() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("age bw 60 and 70").evaluate(person)); // 64
	}

	@Test
	public void shouldReturnTrueWhenValueIsOnLowerBound() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("age bw 64 and 70").evaluate(person));
	}

	@Test
	public void shouldReturnTrueWhenValueIsOnUpperBound() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("age bw 60 and 64").evaluate(person));
	}

	@Test
	public void shouldReturnFalseWhenValueIsBelowRange() throws ExpressionException
	{
		assertEquals(Boolean.FALSE, Expression.valueOf("age bw 70 and 80").evaluate(person));
	}

	@Test
	public void shouldReturnFalseWhenValueIsAboveRange() throws ExpressionException
	{
		assertEquals(Boolean.FALSE, Expression.valueOf("age bw 10 and 20").evaluate(person));
	}

	@Test
	public void shouldEvaluateBetweenWithDouble() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("weight bw 80.0 and 81.0").evaluate(person)); // 80.5
	}

	@Test
	public void shouldEvaluateBetweenWithString() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("name bw 'P' and 'Q'").evaluate(person)); // "Pessoa 1"
	}

	@Test
	public void shouldThrowWhenBetweenOperandIsNotComparable()
	{
		assertThrows(ExpressionException.class, () -> Expression.valueOf("children bw 1 and 5").evaluate(person));
	}

	// -------------------------------------------------------------------------
// in
// -------------------------------------------------------------------------

	@Test
	public void shouldReturnTrueWhenValueIsInList() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("age in (50, 64, 70)").evaluate(person));
	}

	@Test
	public void shouldReturnFalseWhenValueIsNotInList() throws ExpressionException
	{
		assertEquals(Boolean.FALSE, Expression.valueOf("age in (50, 65, 70)").evaluate(person));
	}

	@Test
	public void shouldEvaluateInWithStrings() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("name in ('Pessoa 1', 'Pessoa 2')").evaluate(person));
	}

	@Test
	public void shouldEvaluateInWithSingleElement() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("age in (64)").evaluate(person));
	}

	@Test
	public void shouldEvaluateInWithExpression() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("age in (60 + 4, 70)").evaluate(person));
	}

	@Test
	public void shouldUseInInsideLogicalExpression() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("age in (64, 70) and name in ('Pessoa 1', 'Pessoa 2')").evaluate(person));
	}

	@Test
	public void shouldThrowOnUnclosedInTuple()
	{
		assertThrows(ExpressionException.class, () -> Expression.valueOf("age in (64, 70").evaluate(person));
	}

	// -------------------------------------------------------------------------
// power (**)
// -------------------------------------------------------------------------

	@Test
	public void shouldCalculatePowerOfIntegers() throws ExpressionException
	{
		assertEquals(4, (int) Expression.valueOf("2 ** 2").evaluate(person));
	}

	@Test
	public void shouldCalculatePowerOfAge() throws ExpressionException
	{
		assertEquals(4096, (int) Expression.valueOf("age ** 2").evaluate(person)); // 64 ** 2
	}

	@Test
	public void shouldReturnOneForExponentZero() throws ExpressionException
	{
		assertEquals(1, (int) Expression.valueOf("age ** 0").evaluate(person));
	}

	@Test
	public void shouldReturnBaseForExponentOne() throws ExpressionException
	{
		assertEquals(64, (int) Expression.valueOf("age ** 1").evaluate(person));
	}

	@Test
	public void shouldBeRightAssociative() throws ExpressionException
	{
		assertEquals(512, (int) Expression.valueOf("2 ** 3 ** 2").evaluate(person)); // 2 ** 9, não 8 ** 2
	}

	@Test
	public void shouldHaveHigherPrecedenceThanMultiplication() throws ExpressionException
	{
		assertEquals(18, (int) Expression.valueOf("2 * 3 ** 2").evaluate(person)); // 2 * 9, não 6 ** 2
	}

	@Test
	public void shouldCalculatePowerOfDouble() throws ExpressionException
	{
		assertEquals(2.0, (double) Expression.valueOf("4.0 ** 0.5").evaluate(person), 0.001); // raiz quadrada
	}

	@Test
	public void shouldThrowWhenPowerOperandIsIncompatible()
	{
		assertThrows(ExpressionException.class, () -> Expression.valueOf("name ** 2").evaluate(person));
	}

	// -------------------------------------------------------------------------
// like (lk)
// -------------------------------------------------------------------------

	@Test
	public void shouldMatchLikeWithWildcardAtEnd() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("name lk 'Pessoa%'").evaluate(person));
	}

	@Test
	public void shouldMatchLikeWithWildcardAtStart() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("name lk '%1'").evaluate(person));
	}

	@Test
	public void shouldMatchLikeWithWildcardBothSides() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("name lk '%esso%'").evaluate(person));
	}

	@Test
	public void shouldNotMatchLikeWhenPatternDoesNotFit() throws ExpressionException
	{
		assertEquals(Boolean.FALSE, Expression.valueOf("name lk 'Pessoa2%'").evaluate(person));
	}

	@Test
	public void shouldMatchLikeWithSingleCharWildcard() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("name lk 'Pesso_ 1'").evaluate(person));
	}

	@Test
	public void shouldNotMatchLikeWithSingleCharWhenMultiple() throws ExpressionException
	{
		assertEquals(Boolean.FALSE, Expression.valueOf("name lk 'Pess_ 1'").evaluate(person));
	}

	@Test
	public void shouldMatchLikeExactString() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("name lk 'Pessoa 1'").evaluate(person));
	}

	@Test
	public void shouldThrowWhenLikeLeftOperandIsNotString()
	{
		assertThrows(ExpressionException.class, () -> Expression.valueOf("age lk 'Pessoa%'").evaluate(person));
	}

	// -------------------------------------------------------------------------
// not in, not lk, not bw
// -------------------------------------------------------------------------

	@Test
	public void shouldReturnTrueWhenValueIsNotInList() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("age not in (50, 65, 70)").evaluate(person));
	}

	@Test
	public void shouldReturnFalseWhenValueIsInList() throws ExpressionException
	{
		assertEquals(Boolean.FALSE, Expression.valueOf("age not in (50, 64, 70)").evaluate(person));
	}

	@Test
	public void shouldReturnTrueWhenNameNotLike() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("name not lk 'Filho%'").evaluate(person));
	}

	@Test
	public void shouldReturnFalseWhenNameMatchesNotLike() throws ExpressionException
	{
		assertEquals(Boolean.FALSE, Expression.valueOf("name not lk 'Pessoa%'").evaluate(person));
	}

	@Test
	public void shouldReturnTrueWhenValueNotBetween() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("age not bw 70 and 80").evaluate(person));
	}

	@Test
	public void shouldReturnFalseWhenValueIsWithinNotBetweenRange() throws ExpressionException
	{
		assertEquals(Boolean.FALSE, Expression.valueOf("age not bw 60 and 70").evaluate(person));
	}

	@Test
	public void shouldThrowWhenInvalidOperatorAfterNot()
	{
		assertThrows(ExpressionException.class, () -> Expression.valueOf("age not eq 64").evaluate(person));
	}

	@Test
	public void shouldReturnTrueWhenValueDoesNotMatchRegex() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("this not rx '^[a-z]+$'").evaluate("123"));
	}

	@Test
	public void shouldReturnFalseWhenValueMatchesNotRegex() throws ExpressionException
	{
		assertEquals(Boolean.FALSE, Expression.valueOf("this not rx '^[0-9]{3}$'").evaluate("123"));
	}

	@Test
	public void shouldThrowWhenNotRxOperandIsNotString()
	{
		assertThrows(ExpressionException.class, () -> Expression.valueOf("age not rx '^[0-9]+$'").evaluate(person));
	}

	// -------------------------------------------------------------------------
	// ternary (? :)
	// -------------------------------------------------------------------------

	@Test
	public void shouldReturnThenWhenConditionIsTrue() throws ExpressionException
	{
		assertEquals("maior", Expression.valueOf("age > 18 ? 'maior' : 'menor'").evaluate(person));
	}

	@Test
	public void shouldReturnElseWhenConditionIsFalse() throws ExpressionException
	{
		assertEquals("menor", Expression.valueOf("age < 18 ? 'maior' : 'menor'").evaluate(person));
	}

	@Test
	public void shouldEvaluateTernaryWithArithmeticInBranches() throws ExpressionException
	{
		assertEquals(128, (int) Expression.valueOf("age > 18 ? age * 2 : age / 2").evaluate(person)); // 64 * 2
	}

	@Test
	public void shouldEvaluateNestedTernary() throws ExpressionException
	{
		assertEquals("senior", Expression.valueOf("age < 18 ? 'jovem' : age < 60 ? 'adulto' : 'senior'").evaluate(person));
	}

	@Test
	public void shouldEvaluateTernaryWithCoalesce() throws ExpressionException
	{
		assertEquals("Pessoa 1", Expression.valueOf("age > 18 ? name ?? 'anonimo' : 'menor'").evaluate(person));
	}

	@Test
	public void shouldEvaluateTernaryConditionWithAndOr() throws ExpressionException
	{
		assertEquals("ok", Expression.valueOf("age > 18 and age < 100 ? 'ok' : 'fora'").evaluate(person));
	}

	@Test
	public void shouldThrowWhenTernaryConditionIsNotBoolean()
	{
		assertThrows(ExpressionException.class, () -> Expression.valueOf("age ? 'sim' : 'nao'").evaluate(person));
	}

	@Test
	public void shouldThrowWhenTernaryMissingColon()
	{
		assertThrows(ExpressionException.class, () -> Expression.valueOf("age > 18 ? 'maior'").evaluate(person));
	}

	// -------------------------------------------------------------------------
	// short ternary (?:)
	// -------------------------------------------------------------------------

	@Test
	public void shouldReturnValueWhenTrue() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("age > 18 ?: false").evaluate(person));
	}

	@Test
	public void shouldReturnOtherwiseWhenFalse() throws ExpressionException
	{
		assertEquals("fallback", Expression.valueOf("age < 18 ?: 'fallback'").evaluate(person));
	}

	@Test
	public void shouldChainShortTernary() throws ExpressionException
	{
		assertEquals("fallback", Expression.valueOf("age < 18 ?: age < 0 ?: 'fallback'").evaluate(person));
	}

	@Test
	public void shouldReturnFirstTruthyInChain() throws ExpressionException
	{
		assertEquals(Boolean.TRUE, Expression.valueOf("age > 18 ?: age > 100 ?: false").evaluate(person));
	}

	@Test
	public void shouldThrowWhenLikeRightOperandIsNotString()
	{
		assertThrows(ExpressionException.class, () -> Expression.valueOf("name lk age").evaluate(person));
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