package gate.sql.condition;

import gate.sql.Clause;
import gate.sql.statement.Query;

/**
 * A relation between two predicates of a compiled condition
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

	@Override
	public ExtractorRelation<T> when(boolean assertion)
	{
		return assertion ? this : new Rollback(getClause());
	}

	@Override
	public ExtractorPredicate<T> expression(String expression)
	{
		return new ExtractorPredicate<T>(this)
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

	@Override
	public ExtractorPredicate<T> subquery(Query.Constant.Builder subquery)
	{
		return subquery(subquery.build());
	}

	@Override
	public ExtractorCondition<T> exists(Query.Constant subquery)
	{
		return new ExtractorCondition(this)
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

	@Override
	public ExtractorCondition<T> exists(Query.Constant.Builder subquery)
	{
		return exists(subquery.build());
	}

	@Override
	public ExtractorPredicate<T> not(Query.Constant subquery)
	{
		return new ExtractorPredicate(this)
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

	@Override
	public ExtractorPredicate<T> not(Query.Constant.Builder subquery)
	{
		return not(subquery.build());
	}

	@Override
	public ExtractorRelation<T> not()
	{
		return new ExtractorRelation(this)
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

	@Override
	public ExtractorPredicate<T> not(String expression)
	{
		return not().expression(expression);
	}

	@Override
	public ExtractorCondition<T> not(ConstantCondition expression)
	{
		return not().condition(expression);
	}

	@Override
	public ExtractorCondition<T> not(ExtractorCondition condition)
	{
		return not().condition(condition);
	}

	static class Rollback<T> extends ExtractorRelation<T> implements ExtractorRelationMethods.Rollback<T>
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
		public ExtractorCondition<T> not(ExtractorCondition condition)
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
	}
}
