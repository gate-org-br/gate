package gate.sql.condition;

import gate.sql.Clause;
import java.util.stream.Stream;

interface ExtractorRelationMethods<T> extends Clause
{

	/**
	 * Evaluates to the result of the specified sub condition.
	 *
	 * @param condition the sub condition to be evaluated
	 * @return the current condition, for chained invocations
	 *
	 * @see gate.sql.condition.Condition
	 */
	default ExtractorCondition<T> condition(ExtractorCondition<T> condition)
	{
		return new ExtractorCondition<T>(this)
		{
			@Override
			public String toString()
			{
				String string = getClause().toString();
				if (!string.isEmpty())
					string += " ";
				return string + "(" + condition + ")";
			}

			@Override
			public Stream<Object> getParameters()
			{
				return Stream.concat(getClause().getParameters(),
					condition.getParameters());
			}
		};
	}

	/**
	 * Evaluates to true if the specified sub condition is false.
	 *
	 * @param condition the sub condition to be evaluated
	 * @return the current condition, for chained invocations
	 *
	 * @see gate.sql.condition.Condition
	 */
	ExtractorCondition not(ExtractorCondition condition);

	interface Rollback<T> extends ExtractorRelationMethods<T>
	{

		@Override
		default ExtractorCondition<T> condition(ExtractorCondition condition)
		{
			return new ExtractorCondition<>(getClause().rollback());
		}

		@Override
		default ExtractorCondition<T> not(ExtractorCondition condition)
		{
			return new ExtractorCondition<>(getClause().rollback());
		}
	}
}
