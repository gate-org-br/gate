package gate.sql.condition;

import gate.adapter.registry.ColumnReferenceRegistry;
import gate.sql.Clause;
import gate.sql.statement.Query;
import gate.type.PropertyReference;

/**
 * A relation between two predicates of an extractor-based condition
 *
 * @see gate.sql.condition.ExtractorCondition
 * @see gate.sql.condition.ExtractorPredicate
 */
public class ExtractorRelation<T> extends Relation
		implements ConstantRelationMethods, ExtractorRelationMethods<T>
{

	ExtractorRelation(Clause clause)
	{
		super(clause);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExtractorRelation<T> when(boolean assertion)
	{
		return assertion ? this : new Rollback<T>(getClause());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExtractorPredicate<T> expression(String expression)
	{
		return new ExtractorPredicate<>(this)
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
	public <E, R> ExtractorPredicate<T> expression(PropertyReference<E, R> reference)
	{
		return expression(ColumnReferenceRegistry.INSTANCE.get(reference).name());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExtractorCondition<T> condition(ConstantCondition expression)
	{
		return new ExtractorCondition<T>(this)
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
	public ExtractorPredicate<T> subquery(Query.Constant subquery)
	{
		return new ExtractorPredicate<T>(this)
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
	public ExtractorPredicate<T> subquery(Query.Constant.Builder subquery)
	{
		return subquery(subquery.build());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExtractorCondition<T> exists(Query.Constant subquery)
	{
		return new ExtractorCondition<>(this)
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
	public ExtractorCondition<T> exists(Query.Constant.Builder subquery)
	{
		return exists(subquery.build());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExtractorPredicate<T> not(Query.Constant subquery)
	{
		return new ExtractorPredicate<>(this)
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
	public ExtractorPredicate<T> not(Query.Constant.Builder subquery)
	{
		return not(subquery.build());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExtractorRelation<T> not()
	{
		return new ExtractorRelation<>(this)
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
	public ExtractorPredicate<T> not(String expression)
	{
		return not().expression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E, R> ExtractorPredicate<T> not(PropertyReference<E, R> reference)
	{
		return not().expression(ColumnReferenceRegistry.INSTANCE.get(reference).name());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExtractorCondition<T> not(ConstantCondition expression)
	{
		return not().condition(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExtractorCondition<T> not(ExtractorCondition<T> condition)
	{
		return not().condition(condition);
	}

	static class Rollback<T> extends ExtractorRelation<T>
			implements ExtractorRelationMethods.Rollback<T>
	{

		public Rollback(Clause clause)
		{
			super(clause);
		}

		@Override
		public ExtractorRelation<T> when(boolean assertion)
		{
			return this;
		}

		@Override
		public ExtractorRelation<T> not()
		{
			return this;
		}

		@Override
		public ExtractorCondition<T> not(ExtractorCondition<T> condition)
		{
			return new ExtractorCondition<>(getClause().rollback());
		}

		@Override
		public ExtractorCondition<T> not(ConstantCondition expression)
		{
			return new ExtractorCondition<>(getClause().rollback());
		}

		@Override
		public ExtractorCondition<T> condition(ConstantCondition condition)
		{
			return new ExtractorCondition<>(getClause().rollback());
		}

		@Override
		public ExtractorCondition<T> exists(Query.Constant.Builder subquery)
		{
			return new ExtractorCondition<>(getClause().rollback());
		}

		@Override
		public ExtractorCondition<T> exists(Query.Constant subquery)
		{
			return new ExtractorCondition<>(getClause().rollback());
		}

		@Override
		public ExtractorPredicate<T> not(String expression)
		{
			return new ExtractorPredicate.Rollback<>(getClause());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <E, R> ExtractorPredicate<T> not(PropertyReference<E, R> reference)
		{
			return new ExtractorPredicate.Rollback<>(getClause());
		}

		@Override
		public ExtractorPredicate<T> not(Query.Constant.Builder subquery)
		{
			return new ExtractorPredicate.Rollback<>(getClause());
		}

		@Override
		public ExtractorPredicate<T> not(Query.Constant subquery)
		{
			return new ExtractorPredicate.Rollback<>(getClause());
		}

		@Override
		public ExtractorPredicate<T> subquery(Query.Constant.Builder subquery)
		{
			return new ExtractorPredicate.Rollback<>(getClause());
		}

		@Override
		public ExtractorPredicate<T> subquery(Query.Constant subquery)
		{
			return new ExtractorPredicate.Rollback<>(getClause());
		}

		@Override
		public ExtractorPredicate<T> expression(String expression)
		{
			return new ExtractorPredicate.Rollback<>(getClause());
		}

		@Override
		public <E, R> ExtractorPredicate<T> expression(PropertyReference<E, R> reference)
		{
			return new ExtractorPredicate.Rollback<>(getClause());
		}
	}
}