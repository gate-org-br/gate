package gate.sql.condition;

import gate.sql.Clause;
import gate.sql.statement.Query;

/**
 * Parameterized condition compiled with it's parameter values.
 */
public class ExtractorCondition<T> extends Condition implements ExtractorConditionMethods<T>
{

	/**
	 * A compiled condition that is always true.
	 */
	public static final ExtractorCondition<?> EXTRACTOR_TRUE =
			new ExtractorCondition<>(Condition.of("0").isEq("0"));

	/**
	 * A compiled condition that is always false.
	 */
	public static final ExtractorCondition<?> EXTRACTOR_FALSE =
			new ExtractorCondition<>(Condition.of("0").isEq("1"));

	ExtractorCondition(Clause clause)
	{
		super(clause);
	}

	/** {@inheritDoc} */
	@Override
	public ExtractorRelation<T> and()
	{
		return new ExtractorRelation<T>(this)
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
	public ExtractorRelation<T> or()
	{
		return new ExtractorRelation<T>(this)
		{
			@Override
			public String toString()
			{
				String string = getClause().toString();
				return string.isEmpty() ? string : string + " or";
			}
		};
	}

	/** {@inheritDoc} */
	@Override
	public ExtractorPredicate<T> and(String expression)
	{
		return and().expression(expression);
	}

	/** {@inheritDoc} */
	@Override
	public ExtractorPredicate<T> or(String expression)
	{
		return or().expression(expression);
	}

	/**
	 * Adds a new extractor sub condition associated by an AND relation.
	 *
	 * @param condition the sub condition to be associated with the current condition
	 * @return the current condition, for chained invocations
	 */
	@Override
	public ExtractorCondition<T> and(ExtractorCondition<T> condition)
	{
		return and().condition(condition);
	}

	/**
	 * Adds a new extractor sub condition associated by an OR relation.
	 *
	 * @param condition the sub condition to be associated with the current condition
	 * @return the current condition, for chained invocations
	 */
	@Override
	public ExtractorCondition<T> or(ExtractorCondition<T> condition)
	{
		return or().condition(condition);
	}

	/** {@inheritDoc} */
	@Override
	public ExtractorCondition<T> and(ConstantCondition condition)
	{
		return and().condition(condition);
	}

	/** {@inheritDoc} */
	@Override
	public ExtractorCondition<T> or(ConstantCondition condition)
	{
		return or().condition(condition);
	}

	/** {@inheritDoc} */
	@Override
	public ExtractorPredicate<T> and(Query.Constant subquery)
	{
		return and().subquery(subquery);
	}

	/** {@inheritDoc} */
	@Override
	public ExtractorPredicate<T> and(Query.Constant.Builder subquery)
	{
		return and().subquery(subquery);
	}

	/** {@inheritDoc} */
	@Override
	public ExtractorPredicate<T> or(Query.Constant subquery)
	{
		return or().subquery(subquery);
	}

	/** {@inheritDoc} */
	@Override
	public ExtractorPredicate<T> or(Query.Constant.Builder subquery)
	{
		return or().subquery(subquery);
	}
}
