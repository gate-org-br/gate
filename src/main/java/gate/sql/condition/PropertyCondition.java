package gate.sql.condition;

import gate.sql.Clause;
import gate.sql.statement.Query;

/**
 * Parameterized condition compiled with it's parameter values.
 */
public class PropertyCondition extends Condition implements PropertyConditionMethods
{

	/**
	 * A compiled condition that is always true.
	 */
	public static final PropertyCondition PROPERTY_TRUE
		= new PropertyCondition(Condition.of("0").isEq("0"));

	/**
	 * A compiled condition that is always false.
	 */
	public static final PropertyCondition PROPERTY_FALSE
		= new PropertyCondition(Condition.of("0").isEq("1"));

	PropertyCondition(Clause clause)
	{
		super(clause);
	}

	@Override
	public PropertyRelation and()
	{
		return new PropertyRelation(this)
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
	public PropertyRelation or()
	{
		return new PropertyRelation(this)
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
	public PropertyPredicate and(String expression)
	{
		return and().expression(expression);
	}

	@Override
	public PropertyPredicate or(String expression)
	{
		return or().expression(expression);
	}

	@Override
	public PropertyCondition and(PropertyCondition condition)
	{
		return and().condition(condition);
	}

	@Override
	public PropertyCondition or(PropertyCondition condition)
	{
		return or().condition(condition);
	}

	@Override
	public PropertyCondition and(ConstantCondition condition)
	{
		return and().condition(condition);
	}

	@Override
	public PropertyCondition or(ConstantCondition condition)
	{
		return or().condition(condition);
	}

	@Override
	public PropertyPredicate and(Query.Constant subquery)
	{
		return and().subquery(subquery);
	}

	@Override
	public PropertyPredicate and(Query.Constant.Builder subquery)
	{
		return and().subquery(subquery);
	}

	@Override
	public PropertyPredicate or(Query.Constant subquery)
	{
		return or().subquery(subquery);
	}

	@Override
	public PropertyPredicate or(Query.Constant.Builder subquery)
	{
		return or().subquery(subquery);
	}
}
