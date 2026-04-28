package gate.sql.condition;

import gate.adapter.registry.ColumnReferenceRegistry;
import gate.sql.Clause;
import gate.sql.statement.Query;
import gate.type.PropertyReference;

import java.util.stream.Stream;

/**
 * A relation between two predicates of a constant condition
 *
 * @see gate.sql.condition.ConstantCondition
 * @see gate.sql.condition.ConstantPredicate
 */
public class ConstantRelation extends Relation
		implements ConstantRelationMethods,
		CompiledRelationMethods
{

	ConstantRelation(Clause clause)
	{
		super(clause);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConstantRelation when(boolean assertion)
	{
		return assertion ? this : new Rollback(getClause());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConstantRelation not()
	{
		return new ConstantRelation(this)
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
	public ConstantPredicate expression(String expression)
	{
		return new ConstantPredicate(this)
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
	public <T, R> ConstantPredicate expression(PropertyReference<T, R> reference)
	{
		return expression(ColumnReferenceRegistry.INSTANCE.get(reference).name());
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
	public ConstantPredicate not(String expression)
	{
		return not().expression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T, R> ConstantPredicate not(PropertyReference<T, R> reference)
	{
		return not().expression(ColumnReferenceRegistry.INSTANCE.get(reference).name());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConstantCondition condition(ConstantCondition condition)
	{
		return new ConstantCondition(this)
		{
			@Override
			public String toString()
			{
				String string = getClause().toString();
				if (!string.isEmpty())
					string += " ";
				return string + "(" + condition + ")";
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConstantCondition not(ConstantCondition condition)
	{
		return not().condition(condition);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledCondition not(CompiledCondition condition)
	{
		return not().condition(condition);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConstantPredicate subquery(Query.Constant subquery)
	{
		return new ConstantPredicate(this)
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
	public ConstantPredicate subquery(Query.Constant.Builder subquery)
	{
		return subquery(subquery.build());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConstantPredicate not(Query.Constant subquery)
	{
		return new ConstantPredicate(this)
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
	public ConstantPredicate not(Query.Constant.Builder subquery)
	{
		return subquery(subquery.build());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConstantCondition exists(Query.Constant subquery)
	{
		return new ConstantCondition(this)
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
	public ConstantCondition exists(Query.Constant.Builder subquery)
	{
		return exists(subquery.build());
	}

	static class Rollback extends ConstantRelation implements CompiledRelationMethods.Rollback
	{

		public Rollback(Clause clause)
		{
			super(clause);
		}

		@Override
		public ConstantRelation not()
		{
			return this;
		}

		@Override
		public ConstantRelation when(boolean assertion)
		{
			return this;
		}

		@Override
		public ConstantCondition not(ConstantCondition condition)
		{
			return new ConstantCondition(getClause().rollback());
		}

		@Override
		public ConstantCondition condition(ConstantCondition exp)
		{
			return new ConstantCondition(getClause().rollback());
		}

		@Override
		public ConstantCondition exists(Query.Constant.Builder subquery)
		{
			return new ConstantCondition(getClause().rollback());
		}

		@Override
		public ConstantCondition exists(Query.Constant subquery)
		{
			return new ConstantCondition(getClause().rollback());
		}

		@Override
		public CompiledCondition not(CompiledCondition condition)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		public ConstantPredicate expression(String expression)
		{
			return new ConstantPredicate.Rollback(getClause());
		}

		@Override
		public <T, R> ConstantPredicate expression(PropertyReference<T, R> reference) {return new ConstantPredicate.Rollback(getClause());}

		@Override
		public CompiledPredicate expression(String expression, Object... parameters)
		{
			return new CompiledPredicate.Rollback(getClause());
		}

		@Override
		public CompiledCondition exists(Query.Compiled.Builder subquery)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		public CompiledCondition exists(Query.Compiled subquery)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		public ConstantPredicate not(Query.Constant.Builder subquery)
		{
			return new ConstantPredicate.Rollback(getClause());
		}

		@Override
		public CompiledCondition condition(CompiledCondition condition)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		public CompiledPredicate not(Query.Compiled.Builder subquery)
		{
			return new CompiledPredicate.Rollback(getClause());
		}

		@Override
		public ConstantPredicate subquery(Query.Constant.Builder subquery)
		{
			return new ConstantPredicate.Rollback(getClause());
		}

		@Override
		public CompiledPredicate not(Query.Compiled subquery)
		{
			return new CompiledPredicate.Rollback(getClause());
		}

		@Override
		public ConstantPredicate not(Query.Constant subquery)
		{
			return new ConstantPredicate.Rollback(getClause());
		}

		@Override
		public CompiledPredicate subquery(Query.Compiled.Builder subquery)
		{
			return new CompiledPredicate.Rollback(getClause());
		}

		@Override
		public CompiledPredicate subquery(Query.Compiled subquery)
		{
			return new CompiledPredicate.Rollback(getClause());
		}

		@Override
		public ConstantPredicate subquery(Query.Constant subquery)
		{
			return new ConstantPredicate.Rollback(getClause());
		}

		@Override
		public ConstantPredicate not(String expression)
		{
			return new ConstantPredicate.Rollback(getClause());
		}

		@Override
		public <T, R> ConstantPredicate not(PropertyReference<T, R> reference)
		{
			return new ConstantPredicate.Rollback(getClause());
		}
	}
}