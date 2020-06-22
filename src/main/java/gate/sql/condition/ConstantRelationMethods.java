package gate.sql.condition;

import gate.sql.statement.Query;

interface ConstantRelationMethods
{

	/**
	 * Adds a new predicate with the specified expression to the current condition.
	 *
	 * @param expression the expression to be associated with the new predicate
	 * @return the new predicate created, for chained invocations
	 *
	 * @see gate.sql.condition.Predicate
	 */
	Predicate expression(String expression);

	/**
	 * Evaluates to the result of the specified sub condition.
	 *
	 * @param condition the sub condition to be evaluated
	 * @return the current condition, for chained invocations
	 *
	 * @see gate.sql.condition.Condition
	 */
	Condition condition(ConstantCondition condition);

	/**
	 * Adds a new predicate with the specified sub query to the current condition.
	 *
	 * @param subquery the sub query to be associated with the new predicate
	 * @return the new predicate created, for chained invocations
	 *
	 * @see gate.sql.condition.Predicate
	 */
	Predicate subquery(Query.Constant.Builder subquery);

	/**
	 * Adds a new predicate with the specified sub query to the current condition.
	 *
	 * @param subquery the sub query to be associated with the new predicate
	 * @return the new predicate created, for chained invocations
	 *
	 * @see gate.sql.condition.Predicate
	 */
	Predicate subquery(Query.Constant subquery);

	/**
	 * Evaluates to true if the specified sub query is not empty.
	 *
	 * @param condition the sub query to be checked for emptiness
	 * @return the current condition, for chained invocations
	 *
	 * @see gate.sql.condition.Condition
	 */
	Condition exists(Query.Constant subquery);

	/**
	 * Evaluates to true if the specified sub query is not empty.
	 *
	 * @param condition the sub query to be checked for emptiness
	 * @return the current condition, for chained invocations
	 *
	 * @see gate.sql.condition.Condition
	 */
	Condition exists(Query.Constant.Builder subquery);

	/**
	 * Negates the current predicate.
	 *
	 * @return the negated predicate, for chained invocations
	 *
	 * @see gate.sql.condition.Predicate
	 */
	Relation not();

	/**
	 * Adds a new negated predicate with the specified expression to the current condition.
	 *
	 * @param expression the expression to be associated with the new predicate
	 * @return the new negated predicate created, for chained invocations
	 *
	 * @see gate.sql.condition.Predicate
	 */
	Predicate not(String expression);

	/**
	 * Evaluates to true if the specified sub condition is false.
	 *
	 * @param condition the sub condition to be evaluated
	 * @return the current condition, for chained invocations
	 *
	 * @see gate.sql.condition.Condition
	 */
	Condition not(ConstantCondition condition);

	/**
	 * Adds a new negated predicate with the specified sub query to the current condition.
	 *
	 * @param subquery the sub query to be associated with the new predicate
	 * @return the new negated predicate created, for chained invocations
	 *
	 * @see gate.sql.condition.Predicate
	 */
	Predicate not(Query.Constant subquery);

	/**
	 * Adds a new negated predicate with the specified sub query to the current condition.
	 *
	 * @param subquery the sub query to be associated with the new predicate
	 * @return the new negated predicate created, for chained invocations
	 *
	 * @see gate.sql.condition.Predicate
	 */
	Predicate not(Query.Constant.Builder subquery);

}
