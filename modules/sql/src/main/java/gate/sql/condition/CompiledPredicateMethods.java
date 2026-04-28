package gate.sql.condition;

import gate.adapter.converter.Converter;
import gate.sql.Clause;
import gate.sql.statement.Query;

import java.util.List;

interface CompiledPredicateMethods extends Clause
{
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Eq
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares the previously specified clause with the specified parameter if the parameter is
	 * not null and evaluates to true if the clause is equal to the parameter.
	 *
	 * @param parameter parameter to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default CompiledCondition eq(Object parameter)
	{
		return CompiledCondition.of(this, " = ?", parameter);
	}

	/**
	 * Evaluates to true if the previously specified clause is equal to the specified parameter.
	 *
	 * @param type      type of the parameter to be compared with the specified clause
	 * @param parameter parameter to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition eq(Class<T> type, T parameter)
	{
		return CompiledCondition.of(this, type, " = ?", parameter);
	}

	/**
	 * Evaluates to true if the previously specified clause is equal to the specified sub query.
	 *
	 * @param subquery sub query to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isEq(Query.Compiled subquery)
	{
		return CompiledCondition.of(this, " = ", subquery);
	}

	/**
	 * Evaluates to true if the previously specified clause is equal to the specified sub query.
	 *
	 * @param subquery sub query to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isEq(Query.Compiled.Builder subquery)
	{
		return CompiledCondition.of(this, " = ", subquery);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Ne
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Compares the previously specified clause with the specified parameter if the parameter is
	 * not null and evaluates to true if the clause is equal to the parameter.
	 *
	 * @param parameter parameter to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default CompiledCondition ne(Object parameter)
	{
		return CompiledCondition.of(this, " <> ?", parameter);
	}

	/**
	 * Evaluates to true if the previously specified clause is equal to the specified parameter.
	 *
	 * @param type      type of the parameter to be compared with the specified clause
	 * @param parameter parameter to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition ne(Class<T> type, T parameter)
	{
		return CompiledCondition.of(this, type, " <> ?", parameter);
	}

	/**
	 * Evaluates to true if the previously specified clause is equal to the specified sub query.
	 *
	 * @param subquery sub query to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isNe(Query.Compiled subquery)
	{
		return CompiledCondition.of(this, " <> ", subquery);
	}

	/**
	 * Evaluates to true if the previously specified clause is equal to the specified sub query.
	 *
	 * @param subquery sub query to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isNe(Query.Compiled.Builder subquery)
	{
		return CompiledCondition.of(this, " <> ", subquery);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Lt
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Compares the previously specified clause with the specified parameter if the parameter is
	 * not null and evaluates to true if the clause is less than the parameter.
	 *
	 * @param parameter parameter to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default CompiledCondition lt(Object parameter)
	{
		return CompiledCondition.of(this, " < ?", parameter);
	}

	/**
	 * Evaluates to true if the previously specified clause is less than the specified parameter.
	 *
	 * @param type      type of the parameter to be compared with the specified clause
	 * @param parameter parameter to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition lt(Class<T> type, T parameter)
	{
		return CompiledCondition.of(this, type, " < ?", parameter);
	}

	/**
	 * Evaluates to true if the previously specified clause is less than the specified sub query.
	 *
	 * @param subquery sub query to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isLt(Query.Compiled subquery)
	{
		return CompiledCondition.of(this, " < ", subquery);
	}

	/**
	 * Evaluates to true if the previously specified clause is less than the specified sub query.
	 *
	 * @param subquery sub query to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isLt(Query.Compiled.Builder subquery)
	{
		return CompiledCondition.of(this, " < ", subquery);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Le
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Compares the previously specified clause with the specified parameter if the parameter is
	 * not null and evaluates to true if the clause is less than or equals the parameter.
	 *
	 * @param parameter parameter to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default CompiledCondition le(Object parameter)
	{
		return CompiledCondition.of(this, " <= ?", parameter);
	}

	/**
	 * Evaluates to true if the previously specified clause is less than or equals the specified
	 * parameter.
	 *
	 * @param type      type of the parameter to be compared with the specified clause
	 * @param parameter parameter to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition le(Class<T> type, T parameter)
	{
		return CompiledCondition.of(this, type, " <= ?", parameter);
	}

	/**
	 * Evaluates to true if the previously specified clause is less than or equals the specified sub
	 * query.
	 *
	 * @param subquery sub query to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isLe(Query.Compiled subquery)
	{
		return CompiledCondition.of(this, " <= ", subquery);
	}

	/**
	 * Evaluates to true if the previously specified clause is less than or equals the specified sub
	 * query.
	 *
	 * @param subquery sub query to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isLe(Query.Compiled.Builder subquery)
	{
		return CompiledCondition.of(this, " <= ", subquery);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Gt
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Compares the previously specified clause with the specified parameter if the parameter is
	 * not null and evaluates to true if the clause is greater than the parameter.
	 *
	 * @param parameter parameter to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default CompiledCondition gt(Object parameter)
	{
		return CompiledCondition.of(this, " > ?", parameter);
	}

	/**
	 * Evaluates to true if the previously specified clause is greater than the specified parameter.
	 *
	 * @param type      type of the parameter to be compared with the specified clause
	 * @param parameter parameter to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition gt(Class<T> type, T parameter)
	{
		return CompiledCondition.of(this, type, " > ?", parameter);
	}

	/**
	 * Evaluates to true if the previously specified clause is greater than the specified sub query.
	 *
	 * @param subquery sub query to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isGt(Query.Compiled subquery)
	{
		return CompiledCondition.of(this, " > ", subquery);
	}

	/**
	 * Evaluates to true if the previously specified clause is less than the specified sub query.
	 *
	 * @param subquery sub query to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isGt(Query.Compiled.Builder subquery)
	{
		return CompiledCondition.of(this, " > ", subquery);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Ge
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Compares the previously specified clause with the specified parameter if the parameter is
	 * not null and evaluates to true if the clause is greater than or equals the parameter.
	 *
	 * @param parameter parameter to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default CompiledCondition ge(Object parameter)
	{
		return CompiledCondition.of(this, " >= ?", parameter);
	}

	/**
	 * Evaluates to true if the previously specified clause is greater than or equals the specified
	 * parameter.
	 *
	 * @param type      type of the parameter to be compared with the specified clause
	 * @param parameter parameter to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition ge(Class<T> type, T parameter)
	{
		return CompiledCondition.of(this, type, " >= ?", parameter);
	}

	/**
	 * Evaluates to true if the previously specified clause is greater than or equals the specified
	 * sub query.
	 *
	 * @param subquery sub query to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isGe(Query.Compiled subquery)
	{
		return CompiledCondition.of(this, " >= ", subquery);
	}

	/**
	 * Evaluates to true if the previously specified clause is greater than or equals the specified
	 * sub query.
	 *
	 * @param subquery sub query to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isGe(Query.Compiled.Builder subquery)
	{
		return CompiledCondition.of(this, " >= ", subquery);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Lk
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Compares the previously specified clause with the specified parameter if the parameter is
	 * not null and evaluates to true if the clause is like the parameter.
	 *
	 * @param parameter parameter to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default CompiledCondition lk(Object parameter)
	{
		return CompiledCondition.of(this, " like ?",
				parameter != null ? "%" + Converter.toString(parameter) + "%" : null);
	}

	/**
	 * Evaluates to true if the previously specified clause is like the specified parameter.
	 *
	 * @param type      type of the parameter to be compared with the specified clause
	 * @param parameter parameter to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition lk(Class<? extends T> type, T parameter)
	{
		return CompiledCondition.of(this, type, " like ?",
				parameter != null ? "%" + Converter.toString(parameter) + "%" : null);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Rx
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Compares the previously specified clause with the specified regular expression if the
	 * regular expression is not null and evaluates to true if the clause matches the regular
	 * expression.
	 *
	 * @param parameter regular expression to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default CompiledCondition rx(Object parameter)
	{
		return CompiledCondition.of(this, " rlike ?",
				parameter != null ? Converter.toString(parameter) : null);
	}

	/**
	 * Evaluates to true if the previously specified clause matches the specified regular
	 * expression.
	 *
	 * @param type      type of the parameter to be compared with the specified clause
	 * @param parameter regular expression to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition rx(Class<T> type, T parameter)
	{
		return CompiledCondition.of(this, type, " rlike ?",
				parameter != null ? Converter.toString(parameter) : null);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Bw
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Compares the previously specified clause with the specified parameter if the parameter is
	 * not null, and evaluates to true if the clause is between the parameter.
	 *
	 * @param parameter parameter to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default CompiledCondition bw(Object parameter)
	{
		return CompiledCondition.of(this, " between ? and ?", parameter);
	}

	/**
	 * Evaluates to true if the previously specified clause is between the specified parameter.
	 *
	 * @param type      type of the parameter to be compared with the specified clause
	 * @param parameter parameter to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition bw(Class<T> type, T parameter)
	{
		return CompiledCondition.of(this, type, " between ? and ?", parameter);
	}

	/**
	 * Compares the previously specified clause with the specified parameters if the parameters are
	 * both not null and evaluates to true if the clause is between the first parameter and the
	 * second one.
	 *
	 * @param parameter1 first parameter to be compared with the specified clause
	 * @param parameter2 second parameter to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default CompiledCondition bw(Object parameter1, Object parameter2)
	{
		return CompiledCondition.of(this, " between ? and ?", parameter1, parameter2);
	}

	/**
	 * Evaluates to true if the previously specified clause is between the specified parameters.
	 *
	 * @param type       type of the parameter to be compared with the specified clause
	 * @param parameter1 first parameter to be compared with the specified clause
	 * @param parameter2 second parameter to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition bw(Class<T> type, T parameter1, T parameter2)
	{
		return CompiledCondition.of(this, type, " between ? and ?", parameter1, parameter2);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// In
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares the previously specified clause with the specified parameter list if the parameter
	 * list is not null and evaluates to true if the parameter list contains the clause.
	 *
	 * @param parameters parameter list to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default CompiledCondition in(List<?> parameters)
	{
		return CompiledCondition.ofList(this, "in", parameters);
	}

	/**
	 * Evaluates to true if the specified parameter list contains the previously specified clause.
	 *
	 * @param type       type of the parameters to be compared with the specified clause
	 * @param parameters parameter list to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition in(Class<T> type, List<T> parameters)
	{
		return CompiledCondition.ofList(this, type, "in", parameters);
	}

	/**
	 * Evaluates to true if the specified sub query result contains the previously specified clause.
	 *
	 * @param subquery sub query to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isIn(Query.Compiled subquery)
	{
		return CompiledCondition.of(this, " in ", subquery);
	}

	/**
	 * Evaluates to true if the specified sub query result contains the previously specified clause.
	 *
	 * @param subquery sub query to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isIn(Query.Compiled.Builder subquery)
	{
		return CompiledCondition.of(this, " in ", subquery);
	}

	interface Rollback extends CompiledPredicateMethods
	{

		@Override
		default CompiledCondition isIn(Query.Compiled.Builder subquery)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition isIn(Query.Compiled subquery)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default <T> CompiledCondition in(Class<T> type, List<T> parameters)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition in(List<?> parameters)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default <T> CompiledCondition bw(Class<T> type, T parameter1, T parameter2)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition bw(Object parameter1, Object parameter2)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default <T> CompiledCondition bw(Class<T> type, T parameter)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition bw(Object parameter)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default <T> CompiledCondition rx(Class<T> type, T parameter)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition rx(Object parameter)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default <T> CompiledCondition lk(Class<? extends T> type, T parameter)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition lk(Object parameter)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition isGe(Query.Compiled.Builder subquery)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition isGe(Query.Compiled subquery)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default <T> CompiledCondition ge(Class<T> type, T parameter)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition ge(Object parameter)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition isGt(Query.Compiled.Builder subquery)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition isGt(Query.Compiled subquery)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default <T> CompiledCondition gt(Class<T> type, T parameter)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition gt(Object parameter)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition isLe(Query.Compiled.Builder subquery)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition isLe(Query.Compiled subquery)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default <T> CompiledCondition le(Class<T> type, T parameter)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition le(Object parameter)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition isLt(Query.Compiled.Builder subquery)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition isLt(Query.Compiled subquery)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default <T> CompiledCondition lt(Class<T> type, T parameter)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition lt(Object parameter)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition isNe(Query.Compiled.Builder subquery)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition isNe(Query.Compiled subquery)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default <T> CompiledCondition ne(Class<T> type, T parameter)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition ne(Object parameter)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition isEq(Query.Compiled.Builder subquery)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition isEq(Query.Compiled subquery)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default <T> CompiledCondition eq(Class<T> type, T parameter)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition eq(Object parameter)
		{
			return new CompiledCondition(getClause().rollback());
		}
	}
}