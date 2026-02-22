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

	/** {@inheritDoc} */
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

	/** {@inheritDoc} */
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

	/**
	 * Adds a new generic sub condition associated by an AND relation.
	 *
	 * @param expression the sub condition to be associated with the current condition
	 * @return the current condition, for chained invocations
	 */
	@Override
	public GenericCondition and(GenericCondition expression)
	{
		return and().condition(expression);
	}

	/**
	 * Adds a new generic sub query associated by an AND relation.
	 *
	 * @param subquery the sub query to be associated with the condition
	 * @return the current predicate, for chained invocations
	 */
	@Override
	public GenericPredicate and(Query subquery)
	{
		return and().subquery(subquery);
	}

	/**
	 * Adds a new generic sub condition associated by an OR relation.
	 *
	 * @param exp the sub condition to be associated with the current condition
	 * @return the current condition, for chained invocations
	 */
	@Override
	public GenericCondition or(GenericCondition exp)
	{
		return or().condition(exp);
	}

	/**
	 * Adds a new generic sub query associated by an OR relation.
	 *
	 * @param subquery the sub query to be associated with the condition
	 * @return the current predicate, for chained invocations
	 */
	@Override
	public GenericPredicate or(Query subquery)
	{
		return or().subquery(subquery);
	}

	/** {@inheritDoc} */
	@Override
	public GenericPredicate and(Query.Constant subquery)
	{
		return and().subquery(subquery);
	}

	/** {@inheritDoc} */
	@Override
	public GenericPredicate and(Query.Constant.Builder subquery)
	{
		return and().subquery(subquery);
	}

	/** {@inheritDoc} */
	@Override
	public GenericPredicate or(Query.Constant subquery)
	{
		return or().subquery(subquery);
	}

	/** {@inheritDoc} */
	@Override
	public GenericPredicate or(Query.Constant.Builder subquery)
	{
		return or().subquery(subquery);
	}

	/** {@inheritDoc} */
	@Override
	public GenericPredicate and(String expression)
	{
		return and().expression(expression);
	}

	/** {@inheritDoc} */
	@Override
	public GenericPredicate or(String expression)
	{
		return or().expression(expression);
	}

	/** {@inheritDoc} */
	@Override
	public GenericCondition and(ConstantCondition expression)
	{
		return and().condition(expression);
	}

	/** {@inheritDoc} */
	@Override
	public GenericCondition or(ConstantCondition expression)
	{
		return or().condition(expression);
	}
}
