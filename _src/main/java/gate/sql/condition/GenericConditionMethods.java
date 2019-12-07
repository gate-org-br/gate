package gate.sql.condition;

import gate.sql.statement.Query;

interface GenericConditionMethods
{

	/**
	 * Adds a new sub condition to the current condition associated by an AND relation.
	 *
	 * @param condition the sub condition to be associated with the current condition
	 * @return the current condition, for chained invocations
	 *
	 * @see gate.sql.condition.Condition
	 */
	GenericCondition and(GenericCondition condition);

	/**
	 * Adds a new sub query to the condition associated by an AND relation.
	 *
	 * @param subquery the sub query to be associated with the condition
	 * @return the current predicate, for chained invocations
	 *
	 * @see gate.sql.condition.Predicate
	 */
	GenericPredicate and(Query subquery);

	/**
	 * Adds a new sub condition to the current condition associated by an OR relation.
	 *
	 * @param condition the sub condition to be associated with the current condition
	 * @return the current condition, for chained invocations
	 *
	 * @see gate.sql.condition.Condition
	 */
	GenericCondition or(GenericCondition condition);

	/**
	 * Adds a new sub query to the condition associated by an OR relation.
	 *
	 * @param subquery the sub query to be associated with the condition
	 * @return the current predicate, for chained invocations
	 *
	 * @see gate.sql.condition.Predicate
	 */
	GenericPredicate or(Query subquery);
}
