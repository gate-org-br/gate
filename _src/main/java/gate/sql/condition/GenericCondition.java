package gate.sql.condition;

import gate.sql.Clause;
import gate.sql.statement.Query;

/**
 * Parameterized condition without the parameter values.
 */
public class GenericCondition extends Condition implements GenericConditionMethods
{

	/**
	 * A generic condition that is always true.
	 */
	public static final GenericCondition GENERIC_TRUE
		= new GenericCondition(Condition.of("0").isEq("0"));

	/**
	 * A generic condition that is always false.
	 */
	public static final GenericCondition GENERIC_FALSE
		= new GenericCondition(Condition.of("0").isEq("1"));

	GenericCondition(Clause clause)
	{
		super(clause);
	}

	@Override
	public GenericRelation and()
	{
		return new GenericRelation(this)
		{
			@Override
			public String toString()
			{
				String string = getClause().toString();
				return string.isEmpty() ? string : string + " and";
			}
		};
	}

	@Override
	public GenericRelation or()
	{
		return new GenericRelation(this)
		{
			@Override
			public String toString()
			{
				String string = getClause().toString();
				return string.isEmpty() ? string : string + " or";
			}
		};
	}

	@Override
	public GenericCondition and(GenericCondition expression)
	{
		return and().condition(expression);
	}

	@Override
	public GenericPredicate and(Query subquery)
	{
		return and().subquery(subquery);
	}

	@Override
	public GenericCondition or(GenericCondition exp)
	{
		return and().condition(exp);
	}

	@Override
	public GenericPredicate or(Query subquery)
	{
		return or().subquery(subquery);
	}

	@Override
	public GenericPredicate and(Query.Constant subquery)
	{
		return and().subquery(subquery);
	}

	@Override
	public GenericPredicate and(Query.Constant.Builder subquery)
	{
		return and().subquery(subquery);
	}

	@Override
	public GenericPredicate or(Query.Constant subquery)
	{
		return or().subquery(subquery);
	}

	@Override
	public GenericPredicate or(Query.Constant.Builder subquery)
	{
		return or().subquery(subquery);
	}

	@Override
	public GenericPredicate and(String expression)
	{
		return and().expression(expression);
	}

	@Override
	public GenericPredicate or(String expression)
	{
		return or().expression(expression);
	}

	@Override
	public GenericCondition and(ConstantCondition expression)
	{
		return and().condition(expression);
	}

	@Override
	public GenericCondition or(ConstantCondition expression)
	{
		return and().condition(expression);
	}
}
