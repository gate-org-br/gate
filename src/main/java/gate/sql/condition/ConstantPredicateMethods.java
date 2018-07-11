package gate.sql.condition;

interface ConstantPredicateMethods
{

	/**
	 * Evaluates to true if the previously specified clause is null.
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	Condition isNull();

	/**
	 * Evaluates to true if the previously specified clause is not null.
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	Condition isNotNull();

	/**
	 * Evaluates to true if the previously specified clause is equals to the specified expression.
	 *
	 * @param expression the expression to be compared with the previously specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws NullPointerException if any of the parameters is null
	 */
	Condition isEq(String expression);

	/**
	 * Evaluates to true if the previously specified clause is not equals to the specified expression.
	 *
	 * @param expression the expression to be compared with the previously specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws NullPointerException if any of the parameters is null
	 */
	Condition isNe(String expression);

	/**
	 * Evaluates to true if the previously specified clause less than the specified expression.
	 *
	 * @param expression the expression to be compared with the previously specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws NullPointerException if any of the parameters is null
	 */
	Condition isLt(String expression);

	/**
	 * Evaluates to true if the previously specified clause less than or equals the specified expression.
	 *
	 * @param expression the expression to be compared with the previously specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws NullPointerException if any of the parameters is null
	 */
	Condition isLe(String expression);

	/**
	 * Evaluates to true if the previously specified clause greater than the specified expression.
	 *
	 * @param expression the expression to be compared with the previously specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws NullPointerException if any of the parameters is null
	 */
	Condition isGt(String expression);

	/**
	 * Evaluates to true if the previously specified clause greater than or equals the specified expression.
	 *
	 * @param expression the expression to be compared with the previously specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws NullPointerException if any of the parameters is null
	 */
	Condition isGe(String expression);

	/**
	 * Evaluates to true if the previously specified clause is between specified expressions.
	 *
	 * @param expression1 the fist expression to be compared with the previously specified clause
	 * @param expression2 the second expression to be compared with the previously specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws NullPointerException if any of the parameters is null
	 */
	Condition isBw(String expression1, String expression2);
}
