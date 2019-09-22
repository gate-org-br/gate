package gate.sql.condition;

import gate.sql.Clause;
import gate.sql.statement.Query;

/**
 * Parameterized condition compiled with it's parameter values.
 */
public class CompiledCondition extends Condition implements CompiledConditionMethods
{

	/**
	 * A compiled condition that is always true.
	 */
	public static final CompiledCondition COMPILED_TRUE
		= new CompiledCondition(Condition.of("0").isEq("0"));

	/**
	 * A compiled condition that is always false.
	 */
	public static final CompiledCondition COMPILED_FALSE
		= new CompiledCondition(Condition.of("0").isEq("1"));

	CompiledCondition(Clause clause)
	{
		super(clause);
	}

	@Override
	public CompiledRelation and()
	{
		return new CompiledRelation(this)
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
	public CompiledRelation or()
	{
		return new CompiledRelation(this)
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
	public CompiledPredicate and(String expression)
	{
		return and().expression(expression);
	}

	@Override
	public CompiledPredicate and(String expression, Object... parameters)
	{
		return and().expression(expression, parameters);
	}

	@Override
	public CompiledPredicate or(String expression)
	{
		return or().expression(expression);
	}

	@Override
	public CompiledPredicate or(String expression, Object... parameters)
	{
		return or().expression(expression, parameters);
	}

	@Override
	public CompiledCondition and(CompiledCondition condition)
	{
		return and().condition(condition);
	}

	@Override
	public CompiledPredicate and(Query.Compiled subquery)
	{
		return and().subquery(subquery);
	}

	@Override
	public CompiledCondition or(CompiledCondition condition)
	{
		return or().condition(condition);
	}

	@Override
	public CompiledPredicate or(Query.Compiled subquery)
	{
		return or().subquery(subquery);
	}

	@Override
	public CompiledCondition and(ConstantCondition condition)
	{
		return and().condition(condition);
	}

	@Override
	public CompiledCondition or(ConstantCondition condition)
	{
		return or().condition(condition);
	}

	@Override
	public CompiledPredicate and(Query.Constant subquery)
	{
		return and().subquery(subquery);
	}

	@Override
	public CompiledPredicate and(Query.Constant.Builder subquery)
	{
		return and().subquery(subquery);
	}

	@Override
	public CompiledPredicate or(Query.Constant subquery)
	{
		return or().subquery(subquery);
	}

	@Override
	public CompiledPredicate or(Query.Constant.Builder subquery)
	{
		return or().subquery(subquery);
	}
}
