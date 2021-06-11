package gate.sql.condition;

import gate.lang.property.Property;
import gate.sql.Clause;
import gate.sql.statement.Query;
import java.util.stream.Stream;

/**
 * A condition to be used on SQL select, update and delete statements.
 * <p>
 * A Condition is a list of {@link gate.sql.condition.Predicate} associated by {@link gate.sql.condition.Relation}
 */
public abstract class Condition implements Clause
{

	/**
	 * A condition that is always true.
	 */
	public static final ConstantCondition TRUE
		= ConstantCondition.CONSTANT_TRUE;
	/**
	 * A condition that is always false.
	 */
	public static final ConstantCondition FALSE
		= ConstantCondition.CONSTANT_FALSE;

	static final RootClause ROOT
		= new RootClause();

	private final Clause clause;

	Condition(Clause clause)
	{
		this.clause = clause;
	}

	@Override
	public Clause getClause()
	{
		return clause;
	}

	@Override
	public Clause rollback()
	{
		return this;
	}

	@Override
	public String toString()
	{
		return getClause().toString();
	}

	/**
	 * Creates a condition from the specified string
	 *
	 * @param string the string of the condition to be created
	 * @return the new condition created
	 */
	public static ConstantCondition from(String string)
	{
		return new ConstantCondition(ROOT)
		{
			@Override
			public Stream<Object> getParameters()
			{
				return Stream.empty();
			}

			@Override
			public Stream<Property> getProperties()
			{
				return Stream.empty();
			}

			@Override
			public String toString()
			{
				return string;
			}

			@Override
			public Clause rollback()
			{
				return this;
			}
		};
	}

	public interface From<T>
	{

		public ExtractorPredicate<T> expression(String expression);
	}

	public static <T> From<T> from(Class<T> type)
	{
		return (String expression) -> new ExtractorPredicate<T>(ROOT)
		{
			@Override
			public Stream<Object> getParameters()
			{
				return Stream.empty();
			}

			@Override
			public Stream<Property> getProperties()
			{
				return Stream.empty();
			}

			@Override
			public String toString()
			{
				return expression;
			}
		};
	}

	public interface When
	{

		public ConstantPredicate expression(String expression);
	}

	public static When when(boolean assertion)
	{
		return assertion
			? (String expression) -> new ConstantPredicate(ROOT)
		{
			@Override
			public Stream<Object> getParameters()
			{
				return Stream.empty();
			}

			@Override
			public Stream<Property> getProperties()
			{
				return Stream.empty();
			}

			@Override
			public String toString()
			{
				return expression;
			}
		}
			: (String expression) -> new ConstantPredicate.Rollback(ROOT)
		{
			@Override
			public Stream<Object> getParameters()
			{
				return Stream.empty();
			}

			@Override
			public Stream<Property> getProperties()
			{
				return Stream.empty();
			}

			@Override
			public String toString()
			{
				return expression;
			}
		};
	}

	/**
	 * Creates a new predicate with the specified expression.
	 *
	 * @param expression the expression to be tested
	 * @return the current predicate, for chained invocations
	 *
	 * @see gate.sql.condition.ConstantPredicate
	 */
	public static ConstantPredicate of(String expression)
	{
		return new ConstantPredicate(ROOT)
		{
			@Override
			public Stream<Object> getParameters()
			{
				return Stream.empty();
			}

			@Override
			public Stream<Property> getProperties()
			{
				return Stream.empty();
			}

			@Override
			public String toString()
			{
				return expression;
			}
		};
	}

	/**
	 * Creates a new negated relation.
	 *
	 * @return the current relation, for chained invocations
	 *
	 * @see gate.sql.condition.ConstantRelation
	 */
	public static ConstantRelation not()
	{
		return new ConstantRelation(ROOT)
		{
			@Override
			public Stream<Object> getParameters()
			{
				return Stream.empty();
			}

			@Override
			public Stream<Property> getProperties()
			{
				return Stream.empty();
			}

			@Override
			public String toString()
			{
				return "not";
			}
		};
	}

	/**
	 * Creates a new negated predicate with the specified expression.
	 *
	 * @param expression the expression to be tested
	 * @return the current predicate, for chained invocations
	 *
	 * @see gate.sql.condition.ConstantPredicate
	 */
	public static ConstantPredicate not(String expression)
	{
		return not().expression(expression);
	}

	/**
	 * Creates a new condition with the specified condition.
	 *
	 * @param condition the condition to be tested
	 * @return the current predicate, for chained invocations
	 *
	 * @see gate.sql.condition.ConstantPredicate
	 */
	public static ConstantCondition of(ConstantCondition condition)
	{
		return new ConstantCondition(ROOT)
		{
			@Override
			public Stream<Object> getParameters()
			{
				return condition.getParameters();
			}

			@Override
			public Stream<Property> getProperties()
			{
				return condition.getProperties();
			}

			@Override
			public String toString()
			{
				return "(" + condition + ")";
			}
		};
	}

	/**
	 * Creates a new condition with the specified condition.
	 *
	 * @param condition the condition to be tested
	 * @return the current predicate, for chained invocations
	 *
	 * @see gate.sql.condition.ConstantPredicate
	 */
	public static GenericCondition of(GenericCondition condition)
	{
		return new GenericCondition(ROOT)
		{
			@Override
			public Stream<Object> getParameters()
			{
				return condition.getParameters();
			}

			@Override
			public Stream<Property> getProperties()
			{
				return condition.getProperties();
			}

			@Override
			public String toString()
			{
				return "(" + condition + ")";
			}
		};
	}

	/**
	 * Creates a new condition with the specified condition.
	 *
	 * @param condition the condition to be tested
	 * @return the current predicate, for chained invocations
	 *
	 * @see gate.sql.condition.ConstantPredicate
	 */
	public static CompiledCondition of(CompiledCondition condition)
	{

		return new CompiledCondition(ROOT)
		{

			@Override
			public Stream<Object> getParameters()
			{
				return condition.getParameters();
			}

			@Override
			public Stream<Property> getProperties()
			{
				return condition.getProperties();
			}

			@Override
			public String toString()
			{
				return "(" + condition + ")";
			}
		};
	}

	/**
	 * Adds a new AND relation to the condition.
	 *
	 * @return the current relation, for chained invocations
	 *
	 * @see gate.sql.condition.Relation
	 */
	public abstract Relation and();

	/**
	 * Adds a new expression to the condition associated by an AND relation.
	 *
	 * @param expression the expression to be associated with the condition
	 * @return the current predicate, for chained invocations
	 *
	 * @see gate.sql.condition.Predicate
	 */
	public abstract Predicate and(String expression);

	/**
	 * Adds a new sub condition to the current condition associated by an AND relation.
	 *
	 * @param condition the sub condition to be associated with the current condition
	 * @return the current condition, for chained invocations
	 *
	 * @see gate.sql.condition.Condition
	 */
	public abstract Condition and(ConstantCondition condition);

	/**
	 * Adds a new sub query to the condition associated by an AND relation.
	 *
	 * @param subquery the sub query to be associated with the condition
	 * @return the current predicate, for chained invocations
	 *
	 * @see gate.sql.condition.Predicate
	 */
	public abstract Predicate and(Query.Constant subquery);

	/**
	 * Adds a new sub query to the condition associated by an AND relation.
	 *
	 * @param subquery the sub query to be associated with the condition
	 * @return the current predicate, for chained invocations
	 *
	 * @see gate.sql.condition.Predicate
	 */
	public abstract Predicate and(Query.Constant.Builder subquery);

	/**
	 * Adds a new OR relation to the condition.
	 *
	 * @return the current relation, for chained invocations
	 *
	 * @see gate.sql.condition.Relation
	 */
	public abstract Relation or();

	/**
	 * Adds a new expression to the condition associated by an OR relation.
	 *
	 * @param expression the expression to be associated with the condition
	 * @return the current predicate, for chained invocations
	 *
	 * @see gate.sql.condition.Predicate
	 */
	public abstract Predicate or(String expression);

	/**
	 * Adds a new sub condition to the current condition associated by an OR relation.
	 *
	 * @param condition the sub condition to be associated with the current condition
	 * @return the current condition, for chained invocations
	 *
	 * @see gate.sql.condition.Condition
	 */
	public abstract Condition or(ConstantCondition condition);

	/**
	 * Adds a new sub query to the condition associated by an OR relation.
	 *
	 * @param subquery the sub query to be associated with the condition
	 * @return the current predicate, for chained invocations
	 *
	 * @see gate.sql.condition.Predicate
	 */
	public abstract Predicate or(Query.Constant subquery);

	/**
	 * Adds a new sub query to the condition associated by an OR relation.
	 *
	 * @param subquery the sub query to be associated with the condition
	 * @return the current predicate, for chained invocations
	 *
	 * @see gate.sql.condition.Predicate
	 */
	public abstract Predicate or(Query.Constant.Builder subquery);
}
