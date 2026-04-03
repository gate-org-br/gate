package gate.sql.condition;

import gate.sql.Clause;
import gate.sql.ColumnReference;
import gate.sql.statement.Query;
import gate.type.PropertyReference;

import java.util.stream.Stream;

/**
 * A relation between two predicates of a compiled condition
 *
 * @see gate.sql.condition.CompiledCondition
 * @see gate.sql.condition.CompiledPredicate
 */
public class CompiledRelation extends Relation
		implements ConstantRelationMethods, CompiledRelationMethods
{

	CompiledRelation(Clause clause)
	{
		super(clause);
	}

	/**
	 * Evaluates the next clause only if the specified assertion is true.
	 *
	 * @param assertion the assertion to be tested
	 * @return the lazy compiled relation, for chained invocations
	 */
	@Override
	public LazyCompiledRelation when(boolean assertion)
	{
		if (!assertion)
			return new LazyCompiledRelation.Rollback(getClause());
		return new LazyCompiledRelation(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledPredicate expression(String expression)
	{
		return new CompiledPredicate(this)
		{
			@Override
			public String toString()
			{
				String string = getClause().toString();
				if (!string.isEmpty())
					string += " ";
				return string + expression;
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T, R> CompiledPredicate expression(PropertyReference<T, R> reference)
	{
		return expression(ColumnReference.of(reference).name());
	}


	/**
	 * Adds a new predicate with the specified expression and parameters.
	 *
	 * @param expression the expression to be associated with the new predicate
	 * @param parameters the parameters to be included on the condition
	 * @return the new predicate created, for chained invocations
	 */
	@Override
	public CompiledPredicate expression(String expression, Object... parameters)
	{
		return new CompiledPredicate(this)
		{
			@Override
			public String toString()
			{
				String string = getClause().toString();
				if (!string.isEmpty())
					string += " ";
				return string + expression;
			}

			@Override
			public Stream<Object> getParameters()
			{
				return Stream.concat(getClause().getParameters(),
						Stream.of(parameters));
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledCondition condition(ConstantCondition expression)
	{
		return new CompiledCondition(this)
		{
			@Override
			public String toString()
			{
				String string = getClause().toString();
				if (!string.isEmpty())
					string += " ";
				return string + "(" + expression + ")";
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledPredicate subquery(Query.Constant subquery)
	{
		return new CompiledPredicate(this)
		{
			@Override
			public String toString()
			{
				String string = getClause().toString();
				if (!string.isEmpty())
					string += " ";
				return string + "(" + subquery + ")";
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledPredicate subquery(Query.Constant.Builder subquery)
	{
		return subquery(subquery.build());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledCondition exists(Query.Constant subquery)
	{
		return new CompiledCondition(this)
		{
			@Override
			public String toString()
			{
				String string = getClause().toString();
				if (!string.isEmpty())
					string += " ";
				return string + "exists (" + subquery + ")";
			}

		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledCondition exists(Query.Constant.Builder subquery)
	{
		return exists(subquery.build());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledPredicate not(Query.Constant subquery)
	{
		return new CompiledPredicate(this)
		{
			@Override
			public String toString()
			{
				String string = getClause().toString();
				if (!string.isEmpty())
					string += " ";
				return string + " not (" + subquery + ")";
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledPredicate not(Query.Constant.Builder subquery)
	{
		return not(subquery.build());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledRelation not()
	{
		return new CompiledRelation(this)
		{
			@Override
			public String toString()
			{
				String string = getClause().toString();
				if (!string.isEmpty())
					string += " ";
				return string + "not";
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledPredicate not(String expression)
	{
		return not().expression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T, R> CompiledPredicate not(PropertyReference<T, R> reference)
	{
		return not().expression(ColumnReference.of(reference).name());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledCondition not(ConstantCondition expression)
	{
		return not().condition(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledCondition not(CompiledCondition condition)
	{
		return not().condition(condition);
	}
}