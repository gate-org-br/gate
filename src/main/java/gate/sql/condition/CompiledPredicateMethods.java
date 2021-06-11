package gate.sql.condition;

import gate.converter.Converter;
import gate.sql.Clause;
import gate.sql.statement.Query;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

interface CompiledPredicateMethods extends Clause
{
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Eq
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares the previously specified clause with the specified parameter, if the parameter is not null, and evaluates to true if the clause is equals to
	 * the parameter.
	 *
	 * @param parameter parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default CompiledCondition eq(Object parameter)
	{
		return parameter != null
			? eq((Class<Object>) parameter.getClass(), parameter)
			: new CompiledCondition(getClause().rollback());
	}

	/**
	 * Evaluates to true if the previously specified clause is equals to the specified parameter.
	 *
	 *
	 * @param type type of the parameter to be compared with the specified clause
	 * @param parameter parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition eq(Class<T> type, T parameter)
	{
		Objects.requireNonNull(type, "Attempt to compile a null parameter into a condition");
		Objects.requireNonNull(parameter, "Attempt to compile a null parameter into a condition");

		return new CompiledCondition(this)
		{
			@Override
			public Stream<Object> getParameters()
			{
				return Stream.concat(getClause().getParameters(),
					Stream.of(parameter));
			}

			@Override
			public String toString()
			{
				return getClause() + " = ?";
			}
		};
	}

	/**
	 * Evaluates to true if the previously specified clause is equals to the specified sub query.
	 *
	 * @param subquery sub query to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isEq(Query.Compiled subquery)
	{
		Objects.requireNonNull(subquery, "Attempt to compile a null subquery into a condition");
		return new CompiledCondition(this)
		{

			@Override
			public Stream<Object> getParameters()
			{
				return Stream.concat(getClause().getParameters(),
					subquery.getParameters().stream());
			}

			@Override
			public String toString()
			{
				return getClause() + " = (" + subquery.toString() + ")";
			}
		};
	}

	/**
	 * Evaluates to true if the previously specified clause is equals to the specified sub query.
	 *
	 * @param subquery sub query to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isEq(Query.Compiled.Builder subquery)
	{
		Objects.requireNonNull(subquery, "Attempt to compile a null subquery into a condition");
		return CompiledPredicateMethods.this.isEq(subquery.build());
	}

	/**
	 * Compares the previously specified clause with the parameter supplied by the specified supplier, if the parameter is not null, and evaluates to true
	 * if the clause is equals to the parameter.
	 *
	 * @param supplier supplier from where to get the parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition eqGet(Supplier<Object> supplier)
	{
		return eq(supplier.get());
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Ne
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Compares the previously specified clause with the specified parameter, if the parameter is not null, and evaluates to true if the clause is equals to
	 * the parameter.
	 *
	 * @param parameter parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default CompiledCondition ne(Object parameter)
	{
		return parameter != null
			? ne((Class<Object>) parameter.getClass(), parameter)
			: new CompiledCondition(getClause().rollback());
	}

	/**
	 * Evaluates to true if the previously specified clause is equals to the specified parameter.
	 *
	 *
	 * @param type type of the parameter to be compared with the specified clause
	 * @param parameter parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition ne(Class<T> type, T parameter)
	{
		Objects.requireNonNull(type, "Attempt to compile a null parameter into a condition");
		Objects.requireNonNull(parameter, "Attempt to compile a null parameter into a condition");
		return new CompiledCondition(this)
		{
			@Override
			public Stream<Object> getParameters()
			{
				return Stream.concat(getClause().getParameters(),
					Stream.of(parameter));
			}

			@Override
			public String toString()
			{
				return getClause() + " <> ?";
			}
		};
	}

	/**
	 * Evaluates to true if the previously specified clause is equals to the specified sub query.
	 *
	 * @param subquery sub query to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isNe(Query.Compiled subquery)
	{
		return new CompiledCondition(this)
		{

			@Override
			public Stream<Object> getParameters()
			{
				return Stream.concat(getClause().getParameters(),
					subquery.getParameters().stream());
			}

			@Override
			public String toString()
			{
				return getClause() + " <> (" + subquery.toString() + ")";
			}
		};
	}

	/**
	 * Evaluates to true if the previously specified clause is equals to the specified sub query.
	 *
	 * @param subquery sub query to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isNe(Query.Compiled.Builder subquery)
	{
		Objects.requireNonNull(subquery, "Attempt to compile a null subquery into a condition");
		return CompiledPredicateMethods.this.isNe(subquery.build());
	}

	/**
	 * Compares the previously specified clause with the parameter supplied by the specified supplier, if the parameter is not null, and evaluates to true
	 * if the clause is not equals to the parameter.
	 *
	 * @param supplier supplier from where to get the parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition neGet(Supplier<Object> supplier)
	{
		return ne(supplier.get());
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Lt
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Compares the previously specified clause with the specified parameter, if the parameter is not null, and evaluates to true if the clause is less than
	 * the parameter.
	 *
	 * @param parameter parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default CompiledCondition lt(Object parameter)
	{
		return parameter != null
			? lt((Class<Object>) parameter.getClass(), parameter)
			: new CompiledCondition(getClause().rollback());
	}

	/**
	 * Evaluates to true if the previously specified clause is less than the specified parameter.
	 *
	 *
	 * @param type type of the parameter to be compared with the specified clause
	 * @param parameter parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition lt(Class<T> type, T parameter)
	{
		Objects.requireNonNull(type, "Attempt to compile a null parameter into a condition");
		Objects.requireNonNull(parameter, "Attempt to compile a null parameter into a condition");
		return new CompiledCondition(this)
		{
			@Override
			public Stream<Object> getParameters()
			{
				return Stream.concat(getClause().getParameters(),
					Stream.of(parameter));
			}

			@Override
			public String toString()
			{
				return getClause() + " < ?";
			}
		};
	}

	/**
	 * Evaluates to true if the previously specified clause is less than the specified sub query.
	 *
	 * @param subquery sub query to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isLt(Query.Compiled subquery)
	{
		return new CompiledCondition(this)
		{

			@Override
			public Stream<Object> getParameters()
			{
				return Stream.concat(getClause().getParameters(),
					subquery.getParameters().stream());
			}

			@Override
			public String toString()
			{
				return getClause() + " < (" + subquery.toString() + ")";
			}
		};
	}

	/**
	 * Evaluates to true if the previously specified clause is less than the specified sub query.
	 *
	 * @param subquery sub query to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isLt(Query.Compiled.Builder subquery)
	{
		Objects.requireNonNull(subquery, "Attempt to compile a null subquery into a condition");
		return CompiledPredicateMethods.this.isLt(subquery.build());
	}

	/**
	 * Compares the previously specified clause with the parameter supplied by the specified supplier, if the parameter is not null, and evaluates to true
	 * if the clause is less than the specified parameter.
	 *
	 * @param supplier supplier from where to get the parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition ltGet(Supplier<Object> supplier)
	{
		return lt(supplier.get());
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Le
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Compares the previously specified clause with the specified parameter, if the parameter is not null, and evaluates to true if the clause is less than
	 * or equals the parameter.
	 *
	 * @param parameter parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default CompiledCondition le(Object parameter)
	{
		return parameter != null
			? le((Class<Object>) parameter.getClass(), parameter)
			: new CompiledCondition(getClause().rollback());
	}

	/**
	 * Evaluates to true if the previously specified clause is less than or equals the specified parameter.
	 *
	 *
	 * @param type type of the parameter to be compared with the specified clause
	 * @param parameter parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition le(Class<T> type, T parameter)
	{
		Objects.requireNonNull(type, "Attempt to compile a null parameter into a condition");
		Objects.requireNonNull(parameter, "Attempt to compile a null parameter into a condition");
		return new CompiledCondition(this)
		{
			@Override
			public Stream<Object> getParameters()
			{
				return Stream.concat(getClause().getParameters(),
					Stream.of(parameter));
			}

			@Override
			public String toString()
			{
				return getClause() + " <= ?";
			}
		};
	}

	/**
	 * Evaluates to true if the previously specified clause is less than or equals the specified sub query.
	 *
	 * @param subquery sub query to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isLe(Query.Compiled subquery)
	{
		return new CompiledCondition(this)
		{

			@Override
			public Stream<Object> getParameters()
			{
				return Stream.concat(getClause().getParameters(),
					subquery.getParameters().stream());
			}

			@Override
			public String toString()
			{
				return getClause() + " <= (" + subquery.toString() + ")";
			}
		};
	}

	/**
	 * Evaluates to true if the previously specified clause is less than or equals the specified sub query.
	 *
	 * @param subquery sub query to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isLe(Query.Compiled.Builder subquery)
	{
		Objects.requireNonNull(subquery, "Attempt to compile a null subquery into a condition");
		return CompiledPredicateMethods.this.isLe(subquery.build());
	}

	/**
	 * Compares the previously specified clause with the parameter supplied by the specified supplier, if the parameter is not null, and evaluates to true
	 * if the clause is less than or equals the specified parameter.
	 *
	 * @param supplier supplier from where to get the parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition leGet(Supplier<Object> supplier)
	{
		return le(supplier.get());
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Gt
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Compares the previously specified clause with the specified parameter, if the parameter is not null, and evaluates to true if the clause is greater
	 * than the parameter.
	 *
	 * @param parameter parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default CompiledCondition gt(Object parameter)
	{
		return parameter != null
			? gt((Class<Object>) parameter.getClass(), parameter)
			: new CompiledCondition(getClause().rollback());
	}

	/**
	 * Evaluates to true if the previously specified clause is greater than the specified parameter.
	 *
	 *
	 * @param type type of the parameter to be compared with the specified clause
	 * @param parameter parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition gt(Class<T> type, T parameter)
	{
		Objects.requireNonNull(type, "Attempt to compile a null parameter into a condition");
		Objects.requireNonNull(parameter, "Attempt to compile a null parameter into a condition");
		return new CompiledCondition(this)
		{
			@Override
			public Stream<Object> getParameters()
			{
				return Stream.concat(getClause().getParameters(),
					Stream.of(parameter));
			}

			@Override
			public String toString()
			{
				return getClause() + " > ?";
			}
		};
	}

	/**
	 * Evaluates to true if the previously specified clause is greater than the specified sub query.
	 *
	 * @param subquery sub query to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isGt(Query.Compiled subquery)
	{
		return new CompiledCondition(this)
		{

			@Override
			public Stream<Object> getParameters()
			{
				return Stream.concat(getClause().getParameters(),
					subquery.getParameters().stream());
			}

			@Override
			public String toString()
			{
				return getClause() + " > (" + subquery.toString() + ")";
			}
		};
	}

	/**
	 * Evaluates to true if the previously specified clause is less than the specified sub query.
	 *
	 * @param subquery sub query to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isGt(Query.Compiled.Builder subquery)
	{
		Objects.requireNonNull(subquery, "Attempt to compile a null subquery into a condition");
		return CompiledPredicateMethods.this.isGt(subquery.build());
	}

	/**
	 * Compares the previously specified clause with the parameter supplied by the specified supplier, if the parameter is not null, and evaluates to true
	 * if the clause is greater than the specified parameter.
	 *
	 * @param supplier supplier from where to get the parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition gtGet(Supplier<Object> supplier)
	{
		return gt(supplier.get());
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Ge
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Compares the previously specified clause with the specified parameter, if the parameter is not null, and evaluates to true if the clause is greater
	 * than or equals the parameter.
	 *
	 * @param parameter parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default CompiledCondition ge(Object parameter)
	{
		return parameter != null
			? ge((Class<Object>) parameter.getClass(), parameter)
			: new CompiledCondition(getClause().rollback());
	}

	/**
	 * Evaluates to true if the previously specified clause is greater than or equals the specified parameter.
	 *
	 *
	 * @param type type of the parameter to be compared with the specified clause
	 * @param parameter parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition ge(Class<T> type, T parameter)
	{
		Objects.requireNonNull(type, "Attempt to compile a null parameter into a condition");
		Objects.requireNonNull(parameter, "Attempt to compile a null parameter into a condition");
		return new CompiledCondition(this)
		{
			@Override
			public Stream<Object> getParameters()
			{
				return Stream.concat(getClause().getParameters(),
					Stream.of(parameter));
			}

			@Override
			public String toString()
			{
				return getClause() + " >= ?";
			}
		};
	}

	/**
	 * Evaluates to true if the previously specified clause is greater than or equals the specified sub query.
	 *
	 * @param subquery sub query to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isGe(Query.Compiled subquery)
	{
		return new CompiledCondition(this)
		{

			@Override
			public Stream<Object> getParameters()
			{
				return Stream.concat(getClause().getParameters(),
					subquery.getParameters().stream());
			}

			@Override
			public String toString()
			{
				return getClause() + " >= (" + subquery.toString() + ")";
			}
		};
	}

	/**
	 * Evaluates to true if the previously specified clause is greater than or equals the specified sub query.
	 *
	 * @param subquery sub query to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isGe(Query.Compiled.Builder subquery)
	{
		Objects.requireNonNull(subquery, "Attempt to compile a null subquery into a condition");
		return CompiledPredicateMethods.this.isGe(subquery.build());
	}

	/**
	 * Compares the previously specified clause with the parameter supplied by the specified supplier, if the parameter is not null, and evaluates to true
	 * if the clause is greater than or equals the parameter.
	 *
	 * @param supplier supplier from where to get the parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition geGet(Supplier<Object> supplier)
	{
		return ge(supplier.get());
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Lk
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Compares the previously specified clause with the specified parameter, if the parameter is not null, and evaluates to true if the clause is like the
	 * parameter.
	 *
	 * @param parameter parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default CompiledCondition lk(Object parameter)
	{
		return parameter != null
			? lk((Class<Object>) parameter.getClass(), parameter)
			: new CompiledCondition(getClause().rollback());
	}

	/**
	 * Evaluates to true if the previously specified clause is like the specified parameter.
	 *
	 *
	 * @param type type of the parameter to be compared with the specified clause
	 * @param parameter parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition lk(Class<T> type, T parameter)
	{
		Objects.requireNonNull(type, "Attempt to compile a null parameter into a condition");
		Objects.requireNonNull(parameter, "Attempt to compile a null parameter into a condition");
		return new CompiledCondition(this)
		{
			@Override
			public Stream<Object> getParameters()
			{
				return Stream.concat(getClause().getParameters(),
					Stream.of("%" + Converter.getConverter(type).toString(type, parameter) + "%"));
			}

			@Override
			public String toString()
			{
				return getClause() + " like ?";
			}
		};
	}

	/**
	 * Compares the previously specified clause with the parameter supplied by the specified supplier, if the parameter is not null, and evaluates if the
	 * clause is like the parameter.
	 *
	 * @param supplier supplier from where to get the parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition lkGet(Supplier<Object> supplier)
	{
		return lk(supplier.get());
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Rx
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Compares the previously specified clause with the specified regular expression, if the regular expression is not null, and evaluates to true if the
	 * clause matches the regular expression.
	 *
	 * @param regex regular expression to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default CompiledCondition rx(Object regex)
	{
		return regex != null
			? rx((Class<Object>) regex.getClass(), regex)
			: new CompiledCondition(getClause().rollback());
	}

	/**
	 * Evaluates to true if the previously specified clause matches the specified regular expression.
	 *
	 *
	 * @param type type of the parameter to be compared with the specified clause
	 * @param regex regular expression to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition rx(Class<T> type, T regex)
	{
		Objects.requireNonNull(type, "Attempt to compile a null parameter into a condition");
		Objects.requireNonNull(regex, "Attempt to compile a null parameter into a condition");
		return new CompiledCondition(this)
		{
			@Override
			public Stream<Object> getParameters()
			{
				return Stream.concat(getClause().getParameters(),
					Stream.of(Converter.getConverter(type).toString(type, regex)));
			}

			@Override
			public String toString()
			{
				return getClause() + " rlike ?";
			}
		};
	}

	/**
	 * Compares the previously specified clause with the parameter supplied by the specified supplier, if the parameter is not null, and evaluates to true
	 * if the clause matches the regular expression.
	 *
	 * @param supplier supplier from where to get the parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition rxGet(Supplier<Object> supplier)
	{
		return rx(supplier.get());
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Bw
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Compares the previously specified clause with the specified parameter, if the parameter is not null, and evaluates to true if the the clause is
	 * between the parameter.
	 *
	 * @param parameter parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default CompiledCondition bw(Object parameter)
	{
		return parameter != null
			? bw((Class<Object>) parameter.getClass(), parameter)
			: new CompiledCondition(getClause().rollback());
	}

	/**
	 * Evaluates to true if the previously specified clause is between the specified parameter.
	 *
	 *
	 * @param type type of the parameter to be compared with the specified clause
	 * @param parameter parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition bw(Class<T> type, T parameter)
	{
		Objects.requireNonNull(type, "Attempt to compile a null parameter into a condition");
		Objects.requireNonNull(parameter, "Attempt to compile a null parameter into a condition");
		return new CompiledCondition(this)
		{
			@Override
			public Stream<Object> getParameters()
			{
				return Stream.concat(getClause().getParameters(),
					Stream.of(parameter));
			}

			@Override
			public String toString()
			{
				return getClause() + " between ? and ?";
			}
		};
	}

	/**
	 * Compares the previously specified clause with the specified parameters, if the parameters are both not null, and evaluates to true if the clause is
	 * between the first parameter and the second one.
	 *
	 * @param parameter1 first parameter to be compared with the specified clause
	 * @param parameter2 second parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default CompiledCondition bw(Object parameter1, Object parameter2)
	{
		return parameter1 != null && parameter2 != null
			? bw((Class<Object>) parameter1.getClass(), parameter1, parameter2)
			: new CompiledCondition(getClause().rollback());
	}

	/**
	 * Evaluates to true if the previously specified clause is between the specified parameters.
	 *
	 *
	 * @param type type of the parameter to be compared with the specified clause
	 * @param parameter1 first parameter to be compared with the specified clause
	 * @param parameter2 second parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition bw(Class<T> type, T parameter1, T parameter2)
	{
		Objects.requireNonNull(type, "Attempt to compile a null parameter into a condition");
		Objects.requireNonNull(parameter1, "Attempt to compile a null parameter into a condition");
		Objects.requireNonNull(parameter2, "Attempt to compile a null parameter into a condition");
		return new CompiledCondition(this)
		{
			@Override
			public Stream<Object> getParameters()
			{
				return Stream.concat(getClause().getParameters(),
					Stream.of(parameter1, parameter2));
			}

			@Override
			public String toString()
			{
				return getClause() + " between ? and ?";
			}
		};
	}

	/**
	 * Compares the previously specified clause with the parameter supplied by the specified supplier, if the parameter is not null, and evaluates to true
	 * if the the clause is between the parameter.
	 *
	 * @param supplier supplier from where to get the parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition bwGet(Supplier<Object> supplier)
	{
		return bw(supplier.get());
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// In
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Compares the previously specified clause with the specified parameter list, if the parameter list is not null, and evaluates to true if the parameter
	 * list contains the clause.
	 *
	 * @param parameters parameter list to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 */
	default CompiledCondition in(List<?> parameters)
	{
		return parameters != null
			? in(Object.class, (List<Object>) parameters)
			: new CompiledCondition(getClause().rollback());
	}

	/**
	 * Evaluates to true if the specified parameter list contains the previously specified clause.
	 *
	 *
	 * @param type type of the parameters to be compared with the specified clause
	 * @param parameters parameter list to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default <T> CompiledCondition in(Class<T> type, List<T> parameters)
	{
		Objects.requireNonNull(type, "Attempt to compile a null parameter into a condition");
		Objects.requireNonNull(parameters, "Attempt to compile a null parameter list into a condition");
		return new CompiledCondition(this)
		{
			@Override
			public Stream<Object> getParameters()
			{
				return Stream.concat(getClause().getParameters(),
					parameters.stream());
			}

			@Override
			public String toString()
			{
				return getClause() + Stream.generate(() -> "?")
					.limit(parameters.size()).collect(Collectors.joining(", ", " in (", ")"));
			}
		};
	}

	/**
	 * Evaluates to true if the specified sub query result contains the previously specified clause.
	 *
	 * @param subquery sub query to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isIn(Query.Compiled subquery)
	{
		return new CompiledCondition(this)
		{

			@Override
			public Stream<Object> getParameters()
			{
				return Stream.concat(getClause().getParameters(),
					subquery.getParameters().stream());
			}

			@Override
			public String toString()
			{
				return getClause() + " in (" + subquery.toString() + ")";
			}
		};
	}

	/**
	 * Evaluates to true if the specified sub query result contains the previously specified clause.
	 *
	 * @param subquery sub query to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default CompiledCondition isIn(Query.Compiled.Builder subquery)
	{
		Objects.requireNonNull(subquery, "Attempt to compile a null subquery into a condition");
		return CompiledPredicateMethods.this.isIn(subquery.build());
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
		default CompiledCondition bwGet(Supplier<Object> supplier)
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
		default CompiledCondition rxGet(Supplier<Object> supplier)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default <T> CompiledCondition rx(Class<T> type, T regex)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition rx(Object regex)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition lkGet(Supplier<Object> supplier)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default <T> CompiledCondition lk(Class<T> type, T parameter)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition lk(Object parameter)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		default CompiledCondition geGet(Supplier<Object> supplier)
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
		default CompiledCondition gtGet(Supplier<Object> supplier)
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
		default CompiledCondition leGet(Supplier<Object> supplier)
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
		default CompiledCondition ltGet(Supplier<Object> supplier)
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
		default CompiledCondition neGet(Supplier<Object> supplier)
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
		default CompiledCondition eqGet(Supplier<Object> supplier)
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
