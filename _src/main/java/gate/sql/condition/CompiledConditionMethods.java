package gate.sql.condition;

import gate.sql.statement.Query;

interface CompiledConditionMethods
{

	/**
	 * Adds the specified expression and the specified parameters to the condition associated by an AND relation.
	 *
	 * @param expression the expression to be associated with the condition
	 * @param parameters the parameters to be associated with the condition
	 * @return the current predicate, for chained invocations
	 *
	 * @see gate.sql.condition.Predicate
	 */
	CompiledPredicate and(String expression, Object... parameters);

	/**
	 * Adds the specified expression and the specified parameters to the condition associated by an OR relation.
	 *
	 * @param expression the expression to be associated with the condition
	 * @param parameters the parameters to be associated with the condition
	 * @return the current predicate, for chained invocations
	 *
	 * @see gate.sql.condition.Predicate
	 */
	CompiledPredicate or(String expression, Object... parameters);

	/**
	 * Adds a new sub condition to the current condition associated by an AND relation.
	 *
	 * @param condition the sub condition to be associated with the current condition
	 * @return the current condition, for chained invocations
	 *
	 * @see gate.sql.condition.Condition
	 */
	CompiledCondition and(CompiledCondition condition);

	/**
	 * Adds a new sub query to the condition associated by an AND relation.
	 *
	 * @param subquery the sub query to be associated with the condition
	 * @return the current predicate, for chained invocations
	 *
	 * @see gate.sql.condition.Predicate
	 */
	CompiledPredicate and(Query.Compiled subquery);

	/**
	 * Adds a new sub condition to the current condition associated by an OR relation.
	 *
	 * @param condition the sub condition to be associated with the current condition
	 * @return the current condition, for chained invocations
	 *
	 * @see gate.sql.condition.Condition
	 */
	CompiledCondition or(CompiledCondition condition);

	/**
	 * Adds a new sub query to the condition associated by an OR relation.
	 *
	 * @param subquery the sub query to be associated with the condition
	 * @return the current predicate, for chained invocations
	 *
	 * @see gate.sql.condition.Predicate
	 */
	CompiledPredicate or(Query.Compiled subquery);
}
