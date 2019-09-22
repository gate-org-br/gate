package gate.sql.condition;

import gate.sql.Clause;
import gate.sql.statement.Query;

/**
 * A condition without parameters.
 */
public class ConstantCondition extends Condition implements
	CompiledConditionMethods,
	GenericConditionMethods
{

	/**
	 * A generic condition that is always true.
	 */
	public static final ConstantCondition CONSTANT_TRUE
		= Condition.of("0").isEq("0");

	/**
	 * A generic condition that is always false.
	 */
	public static final ConstantCondition CONSTANT_FALSE
		= Condition.of("0").isEq("1");

	ConstantCondition(Clause clause)
	{
		super(clause);
	}

	@Override
	public ConstantRelation and()
	{
		return new ConstantRelation(this)
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
	public ConstantRelation or()
	{
		return new ConstantRelation(this)
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
	public ConstantPredicate and(String expression)
	{
		return and().expression(expression);
	}

	@Override
	public CompiledPredicate and(String expression, Object... parameters)
	{
		return and().expression(expression, parameters);
	}

	@Override
	public GenericPredicate and(Query subquery)
	{
		return and().subquery(subquery);
	}

	@Override
	public CompiledPredicate and(Query.Compiled subquery)
	{
		return and().subquery(subquery);
	}

	@Override
	public CompiledPredicate or(Query.Compiled subquery)
	{
		return or().subquery(subquery);
	}

	@Override
	public ConstantPredicate or(String exp)
	{
		return or().expression(exp);
	}

	@Override
	public CompiledPredicate or(String expression, Object... parameters)
	{
		return or().expression(expression, parameters);
	}

	@Override
	public ConstantCondition and(ConstantCondition condition)
	{
		return and().condition(condition);
	}

	@Override
	public CompiledCondition and(CompiledCondition condition)
	{
		return and().condition(condition);
	}

	@Override
	public CompiledCondition or(CompiledCondition condition)
	{
		return or().condition(condition);
	}

	@Override
	public ConstantCondition or(ConstantCondition condition)
	{
		return and().condition(condition);
	}

	@Override
	public GenericPredicate or(Query subquery)
	{
		return or().subquery(subquery);
	}

	@Override
	public ConstantPredicate and(Query.Constant subquery)
	{
		return and().subquery(subquery);
	}

	@Override
	public ConstantPredicate and(Query.Constant.Builder subquery)
	{
		return and().subquery(subquery);
	}

	@Override
	public ConstantPredicate or(Query.Constant subquery)
	{
		return or().subquery(subquery);
	}

	@Override
	public ConstantPredicate or(Query.Constant.Builder subquery)
	{
		return or().subquery(subquery);
	}

	@Override
	public GenericCondition and(GenericCondition condition)
	{
		return and().condition(condition);
	}

	@Override
	public GenericCondition or(GenericCondition condition)
	{
		return or().condition(condition);
	}
}
