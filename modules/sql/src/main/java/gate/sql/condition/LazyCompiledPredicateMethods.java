package gate.sql.condition;

import gate.adapter.converter.Converter;
import gate.sql.Clause;
import gate.sql.statement.Query;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public interface LazyCompiledPredicateMethods extends Clause
{

	/**
	 * Compares the previously specified clause with the parameter supplied by the specified supplier,
	 * if the parameter is not null, and evaluates to true if the clause is equal to the parameter.
	 *
	 * @param type     type of the parameter to be compared with the specified clause
	 * @param supplier supplier from where to get the parameter to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition eq(Class<T> type, Supplier<T> supplier)
	{
		return CompiledCondition.of(this, type, " = ?", Objects.requireNonNull(supplier).get());
	}

	/**
	 * Compares the previously specified clause with the parameter supplied by the specified
	 * supplier, if the parameter is not null, and evaluates to true if the clause is equal to the
	 * parameter.
	 *
	 * @param supplier supplier from where to get the parameter to be compared with the specified
	 *                 clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition eq(Supplier<Object> supplier)
	{
		return CompiledCondition.of(this, " = ?", Objects.requireNonNull(supplier).get());
	}

	/**
	 * Compares the previously specified clause with the parameter supplied by the specified
	 * supplier, if the parameter is not null, and evaluates to true if the clause is not equals to
	 * the parameter.
	 *
	 * @param type     type of the parameter to be compared with the specified clause
	 * @param supplier supplier from where to get the parameter to be compared with the specified
	 *                 clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition ne(Class<T> type, Supplier<T> supplier)
	{
		return CompiledCondition.of(this, type, " <> ?", Objects.requireNonNull(supplier).get());
	}

	/**
	 * Compares the previously specified clause with the parameter supplied by the specified
	 * supplier, if the parameter is not null, and evaluates to true if the clause is not equals to
	 * the parameter.
	 *
	 * @param supplier supplier from where to get the parameter to be compared with the specified
	 *                 clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition ne(Supplier<Object> supplier)
	{
		return CompiledCondition.of(this, " <> ?", Objects.requireNonNull(supplier).get());
	}

	/**
	 * Compares the previously specified clause with the parameter supplied by the specified
	 * supplier, if the parameter is not null, and evaluates to true if the clause is less than the
	 * specified parameter.
	 *
	 * @param type     type of the parameter to be compared with the specified clause
	 * @param supplier supplier from where to get the parameter to be compared with the specified
	 *                 clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition lt(Class<T> type, Supplier<T> supplier)
	{
		return CompiledCondition.of(this, type, " < ?", Objects.requireNonNull(supplier).get());
	}

	/**
	 * Compares the previously specified clause with the parameter supplied by the specified
	 * supplier, if the parameter is not null, and evaluates to true if the clause is less than the
	 * specified parameter.
	 *
	 * @param supplier supplier from where to get the parameter to be compared with the specified
	 *                 clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition lt(Supplier<Object> supplier)
	{
		return CompiledCondition.of(this, " < ?", Objects.requireNonNull(supplier).get());
	}

	/**
	 * Compares the previously specified clause with the parameter supplied by the specified
	 * supplier, if the parameter is not null, and evaluates to true if the clause is less than or
	 * equals the specified parameter.
	 *
	 * @param type     type of the parameter to be compared with the specified clause
	 * @param supplier supplier from where to get the parameter to be compared with the specified
	 *                 clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition le(Class<T> type, Supplier<T> supplier)
	{
		return CompiledCondition.of(this, type, " <= ?", Objects.requireNonNull(supplier).get());
	}

	/**
	 * Compares the previously specified clause with the parameter supplied by the specified
	 * supplier, if the parameter is not null, and evaluates to true if the clause is less than or
	 * equals the specified parameter.
	 *
	 * @param supplier supplier from where to get the parameter to be compared with the specified
	 *                 clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition le(Supplier<Object> supplier)
	{
		return CompiledCondition.of(this, " <= ?", Objects.requireNonNull(supplier).get());
	}

	/**
	 * Compares the previously specified clause with the parameter supplied by the specified
	 * supplier, if the parameter is not null, and evaluates to true if the clause is greater than
	 * the specified parameter.
	 *
	 * @param type     type of the parameter to be compared with the specified clause
	 * @param supplier supplier from where to get the parameter to be compared with the specified
	 *                 clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition gt(Class<T> type, Supplier<T> supplier)
	{
		return CompiledCondition.of(this, type, " > ?", Objects.requireNonNull(supplier).get());
	}

	/**
	 * Compares the previously specified clause with the parameter supplied by the specified
	 * supplier, if the parameter is not null, and evaluates to true if the clause is greater than
	 * the specified parameter.
	 *
	 * @param supplier supplier from where to get the parameter to be compared with the specified
	 *                 clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition gt(Supplier<Object> supplier)
	{
		return CompiledCondition.of(this, " > ?", Objects.requireNonNull(supplier).get());
	}

	/**
	 * Compares the previously specified clause with the parameter supplied by the specified
	 * supplier, if the parameter is not null, and evaluates to true if the clause is greater than
	 * or equals the parameter.
	 *
	 * @param type     type of the parameter to be compared with the specified clause
	 * @param supplier supplier from where to get the parameter to be compared with the specified
	 *                 clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition ge(Class<T> type, Supplier<T> supplier)
	{
		return CompiledCondition.of(this, type, " >= ?", Objects.requireNonNull(supplier).get());
	}

	/**
	 * Compares the previously specified clause with the parameter supplied by the specified
	 * supplier, if the parameter is not null, and evaluates to true if the clause is greater than
	 * or equals the parameter.
	 *
	 * @param supplier supplier from where to get the parameter to be compared with the specified
	 *                 clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition ge(Supplier<Object> supplier)
	{
		return CompiledCondition.of(this, " >= ?", Objects.requireNonNull(supplier).get());
	}

	/**
	 * Compares the previously specified clause with the parameter supplied by the specified
	 * supplier, if the parameter is not null, and evaluates if the clause is like the parameter.
	 *
	 * @param type     type of the parameter to be compared with the specified clause
	 * @param supplier supplier from where to get the parameter to be compared with the specified
	 *                 clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition lk(Class<T> type, Supplier<T> supplier)
	{
		return CompiledCondition.of(this, type, " like ?",
				Optional.ofNullable(Objects.requireNonNull(supplier).get())
						.map(Converter::toString)
						.map(parameter -> "%" + parameter + "%")
						.orElse(null));
	}

	/**
	 * Compares the previously specified clause with the parameter supplied by the specified
	 * supplier, if the parameter is not null, and evaluates if the clause is like the parameter.
	 *
	 * @param supplier supplier from where to get the parameter to be compared with the specified
	 *                 clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition lk(Supplier<Object> supplier)
	{
		return CompiledCondition.of(this, " like ?",
				Optional.ofNullable(Objects.requireNonNull(supplier).get())
						.map(Converter::toString)
						.map(parameter -> "%" + parameter + "%")
						.orElse(null));
	}

	/**
	 * Compares the previously specified clause with the parameter supplied by the specified
	 * supplier, if the parameter is not null, and evaluates to true if the clause matches the
	 * regular expression.
	 *
	 * @param type     type of the parameter to be compared with the specified clause
	 * @param supplier supplier from where to get the parameter to be compared with the specified
	 *                 clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition rx(Class<T> type, Supplier<T> supplier)
	{
		return CompiledCondition.of(this, type, " rlike ?",
				Optional.ofNullable(Objects.requireNonNull(supplier).get())
						.map(Converter::toString)
						.orElse(null));
	}

	/**
	 * Compares the previously specified clause with the parameter supplied by the specified
	 * supplier, if the parameter is not null, and evaluates to true if the clause matches the
	 * regular expression.
	 *
	 * @param supplier supplier from where to get the parameter to be compared with the specified
	 *                 clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition rx(Supplier<Object> supplier)
	{
		return CompiledCondition.of(this, " rlike ?",
				Optional.ofNullable(Objects.requireNonNull(supplier).get())
						.map(Converter::toString)
						.orElse(null));
	}

	/**
	 * Compares the previously specified clause with the parameter supplied by the specified
	 * supplier, if the parameter is not null, and evaluates to true if the clause is between
	 * the parameter.
	 *
	 * @param type     type of the parameter to be compared with the specified clause
	 * @param supplier supplier from where to get the parameter to be compared with the specified
	 *                 clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition bw(Class<T> type, Supplier<T> supplier)
	{
		return CompiledCondition.of(this, type, " between ? and ?", Objects.requireNonNull(supplier).get());
	}

	/**
	 * Compares the previously specified clause with the parameter supplied by the specified
	 * supplier, if the parameter is not null, and evaluates to true if the clause is between
	 * the parameter.
	 *
	 * @param supplier supplier from where to get the parameter to be compared with the specified
	 *                 clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition bw(Supplier<Object> supplier)
	{
		return CompiledCondition.of(this, " between ? and ?", Objects.requireNonNull(supplier).get());
	}

	/**
	 * Compares the previously specified clause with the parameters supplied by the specified
	 * suppliers, if both parameters are not null, and evaluates to true if the clause is between
	 * the first parameter and the second one.
	 *
	 * @param type      type of the parameters to be compared with the specified clause
	 * @param supplier1 supplier from where to get the first parameter to be compared
	 * @param supplier2 supplier from where to get the second parameter to be compared
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition bw(Class<T> type, Supplier<T> supplier1, Supplier<T> supplier2)
	{
		return CompiledCondition.of(this, " between ? and ?",
				Objects.requireNonNull(supplier1).get(),
				Objects.requireNonNull(supplier2).get());
	}

	/**
	 * Compares the previously specified clause with the parameters supplied by the specified
	 * suppliers, if both parameters are not null, and evaluates to true if the clause is between
	 * the first parameter and the second one.
	 *
	 * @param supplier1 supplier from where to get the first parameter to be compared
	 * @param supplier2 supplier from where to get the second parameter to be compared
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition bw(Supplier<Object> supplier1, Supplier<Object> supplier2)
	{
		return CompiledCondition.of(this, " between ? and ?",
				Objects.requireNonNull(supplier1).get(),
				Objects.requireNonNull(supplier2).get());
	}


	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// In
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Compares the previously specified clause with the specified parameter list, if the parameter
	 * list is not null, and evaluates to true if the parameter list contains the clause.
	 *
	 * @param supplier parameter list to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default <T> CompiledCondition in(Supplier<List<T>> supplier)
	{
		return CompiledCondition.ofList(this, "in", Objects.requireNonNull(supplier).get());
	}

	/**
	 * Evaluates to true if the specified parameter list contains the previously specified clause.
	 *
	 * @param type     type of the supplier to be compared with the specified clause
	 * @param supplier parameter list to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the supplier is null
	 */
	default <T> CompiledCondition in(Class<T> type, Supplier<List<T>> supplier)
	{
		return CompiledCondition.ofList(this, type, "in", Objects.requireNonNull(supplier).get());
	}

	/**
	 * Evaluates to true if the specified sub query result contains the previously specified clause.
	 *
	 * @param supplier query to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isIn(Query.Compiled.Supplier supplier)
	{
		return CompiledCondition.of(this, " in ", Objects.requireNonNull(supplier).get());
	}

	/**
	 * Evaluates to true if the specified sub query result contains the previously specified clause.
	 *
	 * @param supplier sub query to be compared with the specified clause
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isIn(Query.Compiled.Builder.Supplier supplier)
	{
		return CompiledCondition.of(this, " in ", Objects.requireNonNull(supplier).get());
	}


	interface Rollback extends LazyCompiledPredicateMethods
	{

		@Override
		default <T> CompiledCondition eq(Class<T> type, Supplier<T> supplier)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition eq(Supplier<Object> supplier)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default <T> CompiledCondition ne(Class<T> type, Supplier<T> supplier)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition ne(Supplier<Object> supplier)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default <T> CompiledCondition lt(Class<T> type, Supplier<T> supplier)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition lt(Supplier<Object> supplier)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default <T> CompiledCondition le(Class<T> type, Supplier<T> supplier)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition le(Supplier<Object> supplier)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default <T> CompiledCondition gt(Class<T> type, Supplier<T> supplier)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition gt(Supplier<Object> supplier)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default <T> CompiledCondition ge(Class<T> type, Supplier<T> supplier)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition ge(Supplier<Object> supplier)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default <T> CompiledCondition lk(Class<T> type, Supplier<T> supplier)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition lk(Supplier<Object> supplier)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default <T> CompiledCondition rx(Class<T> type, Supplier<T> supplier)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition rx(Supplier<Object> supplier)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default <T> CompiledCondition bw(Class<T> type, Supplier<T> supplier)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition bw(Supplier<Object> supplier)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default <T> CompiledCondition bw(Class<T> type, Supplier<T> supplier1, Supplier<T> supplier2)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition bw(Supplier<Object> supplier1, Supplier<Object> supplier2)
		{
			return new CompiledCondition(getClause().rollback());
		}
	}
}