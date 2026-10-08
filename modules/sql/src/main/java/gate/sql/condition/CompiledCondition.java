package gate.sql.condition;

import gate.sql.Clause;
import gate.sql.statement.Query;
import gate.type.PropertyReference;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Parameterized condition compiled with its parameter values.
 */
public class CompiledCondition extends Condition implements CompiledConditionMethods
{

	/**
	 * A compiled condition that is always true.
	 */
	public static final CompiledCondition COMPILED_TRUE
			= new CompiledCondition(Condition.of("0").isEq("0"));

	/**
	 * A compiled condition that is always false.
	 */
	public static final CompiledCondition COMPILED_FALSE
			= new CompiledCondition(Condition.of("0").isEq("1"));

	CompiledCondition(Clause clause)
	{
		super(clause);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledRelation and()
	{
		return new CompiledRelation(this)
		{
			@Override
			public String toString()
			{
				String string = getClause().toString();
				return string.isEmpty() ? string : string + " and";
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledRelation or()
	{
		return new CompiledRelation(this)
		{
			@Override
			public String toString()
			{
				String string = getClause().toString();
				return string.isEmpty() ? string : string + " or";
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledPredicate and(String expression)
	{
		return and().expression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T, R> CompiledPredicate and(PropertyReference<T, R> reference)
	{
		return and().expression(reference);
	}

	/**
	 * Adds a new expression with explicit parameters associated by an AND relation.
	 *
	 * @param expression the expression to be associated with the condition
	 * @param parameters the parameters to be included on the condition
	 * @return the current predicate, for chained invocations
	 */
	@Override
	public CompiledPredicate and(String expression, Object... parameters)
	{
		return and().expression(expression, parameters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledPredicate or(String expression)
	{
		return or().expression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T, R> CompiledPredicate or(PropertyReference<T, R> reference)
	{
		return or().expression(reference);
	}

	/**
	 * Adds a new expression with explicit parameters associated by an OR relation.
	 *
	 * @param expression the expression to be associated with the condition
	 * @param parameters the parameters to be included on the condition
	 * @return the current predicate, for chained invocations
	 */
	@Override
	public CompiledPredicate or(String expression, Object... parameters)
	{
		return or().expression(expression, parameters);
	}

	/**
	 * Adds a new compiled sub condition associated by an AND relation.
	 *
	 * @param condition the sub condition to be associated with the current condition
	 * @return the current condition, for chained invocations
	 */
	@Override
	public CompiledCondition and(CompiledCondition condition)
	{
		return and().condition(condition);
	}

	/**
	 * Adds a new compiled sub query associated by an AND relation.
	 *
	 * @param subquery the sub query to be associated with the condition
	 * @return the current predicate, for chained invocations
	 */
	@Override
	public CompiledPredicate and(Query.Compiled subquery)
	{
		return and().subquery(subquery);
	}

	/**
	 * Adds a new compiled sub condition associated by an OR relation.
	 *
	 * @param condition the sub condition to be associated with the current condition
	 * @return the current condition, for chained invocations
	 */
	@Override
	public CompiledCondition or(CompiledCondition condition)
	{
		return or().condition(condition);
	}

	/**
	 * Adds a new compiled sub query associated by an OR relation.
	 *
	 * @param subquery the sub query to be associated with the condition
	 * @return the current predicate, for chained invocations
	 */
	@Override
	public CompiledPredicate or(Query.Compiled subquery)
	{
		return or().subquery(subquery);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledCondition and(ConstantCondition condition)
	{
		return and().condition(condition);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledCondition or(ConstantCondition condition)
	{
		return or().condition(condition);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledPredicate and(Query.Constant subquery)
	{
		return and().subquery(subquery);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledPredicate and(Query.Constant.Builder subquery)
	{
		return and().subquery(subquery);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledPredicate or(Query.Constant subquery)
	{
		return or().subquery(subquery);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledPredicate or(Query.Constant.Builder subquery)
	{
		return or().subquery(subquery);
	}

	static CompiledCondition of(Clause clause, String predicate,
	                            Query.Compiled.Builder subquery)
	{
		Objects.requireNonNull(subquery, "Attempt to compile a null subquery into a condition");
		return of(clause, predicate, subquery.build());
	}

	static CompiledCondition of(Clause clause, String predicate, Query.Compiled subquery)
	{
		Objects.requireNonNull(subquery, "Attempt to compile a null subquery into a condition");
		return new CompiledCondition(clause)
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
				return clause + predicate + "(" + subquery + ")";
			}
		};
	}

	static CompiledCondition of(Clause clause, String predicate, Object parameter)
	{
		return parameter != null
				? of(clause, parameter.getClass(), predicate, parameter)
				: new CompiledCondition(clause.rollback());
	}

	static <T> CompiledCondition of(Clause clause,
	                                Class<? extends T> type,
	                                String predicate,
	                                T parameter)
	{
		Objects.requireNonNull(type, "Attempt to compile a null type into a condition");
		if (parameter == null)
			throw new NullPointerException("Attempt to compile a null parameter into a condition");
		return new CompiledCondition(clause)
		{
			@Override
			public Stream<Object> getParameters()
			{
				return Stream.concat(getClause().getParameters(), Stream.of(parameter));
			}

			@Override
			public String toString()
			{
				return clause + predicate;
			}
		};
	}

	static <T> CompiledCondition of(Clause clause,
	                                String predicate,
	                                T parameter1,
	                                T parameter2)
	{
		return parameter1 != null && parameter2 != null
				? of(clause, Object.class, predicate, parameter1, parameter2)
				: new CompiledCondition(clause.rollback());
	}

	static <T> CompiledCondition of(Clause clause,
	                                Class<? extends T> type,
	                                String predicate,
	                                T parameter1,
	                                T parameter2)
	{
		Objects.requireNonNull(type, "Attempt to compile a null type into a condition");
		Objects.requireNonNull(parameter1, "Attempt to compile a null parameter into a condition");
		Objects.requireNonNull(parameter2, "Attempt to compile a null parameter into a condition");
		return new CompiledCondition(clause)
		{
			@Override
			public Stream<Object> getParameters()
			{
				return Stream.concat(getClause().getParameters(), Stream.of(parameter1, parameter2));
			}

			@Override
			public String toString()
			{
				return clause + predicate;
			}
		};
	}

	static <T> CompiledCondition ofList(Clause clause,
	                                    String predicate,
	                                    List<T> parameters)
	{
		if (parameters == null)
			return new CompiledCondition(clause.rollback());
		return ofList(clause, Object.class, predicate, parameters);
	}

	static <T> CompiledCondition ofList(Clause clause,
	                                    Class<? super T> type,
	                                    String predicate,
	                                    List<T> parameters)
	{
		Objects.requireNonNull(type, "Attempt to compile a null type into a condition");
		Objects.requireNonNull(parameters, "Attempt to compile a null list of parameters into a condition");
		return new CompiledCondition(clause)
		{
			@Override
			public Stream<Object> getParameters()
			{
				return Stream.concat(getClause().getParameters(), parameters.stream());
			}

			@Override
			public String toString()
			{
				return "%s %s (%s)".formatted(clause, predicate, parameters.stream().map(e -> "?")
						.collect(Collectors.joining(", ")));
			}
		};
	}
}