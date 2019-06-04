package gate.sql.condition;

interface ExtractorConditionMethods<T>
{

	/**
	 * Adds a new sub condition to the current condition associated by an AND relation.
	 *
	 * @param condition the sub condition to be associated with the current condition
	 * @return the current condition, for chained invocations
	 *
	 * @see gate.sql.condition.Condition
	 */
	ExtractorCondition<T> and(ExtractorCondition<T> condition);

	/**
	 * Adds a new sub condition to the current condition associated by an OR relation.
	 *
	 * @param condition the sub condition to be associated with the current condition
	 * @return the current condition, for chained invocations
	 *
	 * @see gate.sql.condition.Condition
	 */
	ExtractorCondition<T> or(ExtractorCondition<T> condition);
}
