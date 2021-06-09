package gate.sql.condition;

import gate.sql.Clause;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

interface ExtractorPredicateMethods<T> extends Clause
{
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Eq
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Evaluates to true if the previously specified clause is equals to the specified parameter.
	 *
	 * @param parameter parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default ExtractorCondition<T> eq(Function<T, Object> parameter)
	{
		Objects.requireNonNull(parameter, "Attempt to compile a null parameter into a condition");

		return new ExtractorCondition<T>(this)
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

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Ne
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Evaluates to true if the previously specified clause is equals to the specified parameter.
	 *
	 * @param parameter parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default ExtractorCondition<T> ne(Function<T, Object> parameter)
	{
		Objects.requireNonNull(parameter, "Attempt to compile a null parameter into a condition");
		return new ExtractorCondition<T>(this)
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

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Lt
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Evaluates to true if the previously specified clause is less than the specified parameter.
	 *
	 * @param parameter parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default ExtractorCondition<T> lt(Function<T, Object> parameter)
	{
		Objects.requireNonNull(parameter, "Attempt to compile a null parameter into a condition");
		return new ExtractorCondition<T>(this)
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

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Le
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Evaluates to true if the previously specified clause is less than or equals the specified parameter.
	 *
	 * @param parameter parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default ExtractorCondition<T> le(Function<T, Object> parameter)
	{
		Objects.requireNonNull(parameter, "Attempt to compile a null parameter into a condition");
		return new ExtractorCondition<T>(this)
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

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Gt
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Evaluates to true if the previously specified clause is greater than the specified parameter.
	 *
	 * @param parameter parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default ExtractorCondition<T> gt(Function<T, Object> parameter)
	{
		Objects.requireNonNull(parameter, "Attempt to compile a null parameter into a condition");
		return new ExtractorCondition<T>(this)
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

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Ge
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Evaluates to true if the previously specified clause is greater than or equals the specified parameter.
	 *
	 * @param parameter parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default ExtractorCondition<T> ge(Function<T, Object> parameter)
	{
		Objects.requireNonNull(parameter, "Attempt to compile a null parameter into a condition");
		return new ExtractorCondition<T>(this)
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

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Bw
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Evaluates to true if the previously specified clause is between the specified parameter.
	 *
	 * @param parameter parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default ExtractorCondition<T> bw(Function<T, Object> parameter)
	{
		Objects.requireNonNull(parameter, "Attempt to compile a null parameter into a condition");
		return new ExtractorCondition<T>(this)
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
	 * Evaluates to true if the previously specified clause is between the specified parameters.
	 *
	 * @param parameter1 first parameter to be compared with the specified clause
	 * @param parameter2 second parameter to be compared with the specified clause
	 *
	 * @return the current {@link gate.sql.condition.Predicate}, for chained invocations
	 *
	 * @throws java.lang.NullPointerException if any of the parameters is null
	 */
	default ExtractorCondition<T> bw(Function<T, Object> parameter1, T parameter2)
	{
		Objects.requireNonNull(parameter1, "Attempt to compile a null parameter into a condition");
		Objects.requireNonNull(parameter2, "Attempt to compile a null parameter into a condition");
		return new ExtractorCondition<T>(this)
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

	interface Rollback<T> extends ExtractorPredicateMethods<T>
	{

		@Override
		default ExtractorCondition<T> eq(Function<T, Object> parameter)
		{
			return new ExtractorCondition(getClause().rollback());
		}

		@Override
		default ExtractorCondition<T> ne(Function<T, Object> parameter)
		{
			return new ExtractorCondition<>(getClause().rollback());
		}

		@Override
		default ExtractorCondition<T> lt(Function<T, Object> parameter)
		{
			return new ExtractorCondition<>(getClause().rollback());
		}

		@Override
		default ExtractorCondition<T> le(Function<T, Object> parameter)
		{
			return new ExtractorCondition<>(getClause().rollback());
		}

		@Override
		default ExtractorCondition<T> gt(Function<T, Object> parameter)
		{
			return new ExtractorCondition<>(getClause().rollback());
		}

		@Override
		default ExtractorCondition<T> ge(Function<T, Object> parameter)
		{
			return new ExtractorCondition<>(getClause().rollback());
		}

		@Override
		default ExtractorCondition<T> bw(Function<T, Object> parameter)
		{
			return new ExtractorCondition<>(getClause().rollback());
		}

		@Override
		default ExtractorCondition<T> bw(Function<T, Object> parameter1, T parameter2)
		{
			return new ExtractorCondition<>(getClause().rollback());
		}
	}
}
