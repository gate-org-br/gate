package gate.sql.condition;

import gate.sql.Clause;
import gate.sql.statement.Query;
import gate.type.PropertyReference;

/**
 * Condition composed from property references.
 */
public class PropertyCondition extends Condition implements PropertyConditionMethods
{

	/**
	 * A property condition that is always true.
	 */
	public static final PropertyCondition PROPERTY_TRUE
			= new PropertyCondition(Condition.of("0").isEq("0"));

	/**
	 * A property condition that is always false.
	 */
	public static final PropertyCondition PROPERTY_FALSE
			= new PropertyCondition(Condition.of("0").isEq("1"));

	PropertyCondition(Clause clause)
	{
		super(clause);
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PropertyPredicate and(String expression)
	{
		return and().expression(expression);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T, R> PropertyPredicate and(PropertyReference<T, R> reference)
	{
		return and().expression(reference);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PropertyPredicate or(String expression)
	{
		return or().expression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T, R> PropertyPredicate or(PropertyReference<T, R> reference)
	{
		return or().expression(reference);
	}

	/**
	 * Adds a new property sub condition associated by an AND relation.
	 *
	 * @param condition the sub condition to be associated with the current condition
	 * @return the current condition, for chained invocations
	 */
	@Override
	public PropertyCondition and(PropertyCondition condition)
	{
		return and().condition(condition);
	}

	/**
	 * Adds a new property sub condition associated by an OR relation.
	 *
	 * @param condition the sub condition to be associated with the current condition
	 * @return the current condition, for chained invocations
	 */
	@Override
	public PropertyCondition or(PropertyCondition condition)
	{
		return or().condition(condition);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PropertyCondition and(ConstantCondition condition)
	{
		return and().condition(condition);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PropertyCondition or(ConstantCondition condition)
	{
		return or().condition(condition);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PropertyPredicate and(Query.Constant subquery)
	{
		return and().subquery(subquery);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PropertyPredicate and(Query.Constant.Builder subquery)
	{
		return and().subquery(subquery);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PropertyPredicate or(Query.Constant subquery)
	{
		return or().subquery(subquery);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PropertyPredicate or(Query.Constant.Builder subquery)
	{
		return or().subquery(subquery);
	}
}
