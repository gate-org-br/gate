package gate.sql.condition;

import gate.sql.Clause;
import gate.sql.statement.Query;

/**
 * A relation between two predicates of a generic condition
 *
 * @see gate.sql.condition.GenericCondition
 * @see gate.sql.condition.GenericPredicate
 */
public class GenericRelation extends Relation implements ConstantRelationMethods, GenericRelationMethods
{

	GenericRelation(Clause clause)
	{
		super(clause);
	}

	/** {@inheritDoc} */
	@Override
	public GenericRelation when(boolean assertion)
	{
		return assertion ? this : new Rollback(getClause());
	}

	/** {@inheritDoc} */
	@Override
	public GenericPredicate expression(String expression)
	{
		return new GenericPredicate(this)
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

	/** {@inheritDoc} */
	@Override
	public GenericCondition condition(ConstantCondition condition)
	{
		return new GenericCondition(this)
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

	/** {@inheritDoc} */
	@Override
	public GenericRelation not()
	{
		return new GenericRelation(this)
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

	/** {@inheritDoc} */
	@Override
	public GenericPredicate not(String expression)
	{
		return not().expression(expression);
	}

	/** {@inheritDoc} */
	@Override
	public GenericCondition not(ConstantCondition condition)
	{
		return not().condition(condition);
	}

	/** {@inheritDoc} */
	@Override
	public GenericCondition not(GenericCondition condition)
	{
		return not().condition(condition);
	}

	/** {@inheritDoc} */
	@Override
	public GenericPredicate subquery(Query.Constant subquery)
	{
		return new GenericPredicate(this)
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

	/** {@inheritDoc} */
	@Override
	public GenericPredicate subquery(Query.Constant.Builder subquery)
	{
		return subquery(subquery.build());
	}

	/** {@inheritDoc} */
	@Override
	public GenericPredicate not(Query.Constant subquery)
	{
		return new GenericPredicate(this)
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

	/** {@inheritDoc} */
	@Override
	public GenericPredicate not(Query.Constant.Builder subquery)
	{
		return not(subquery.build());
	}

	/** {@inheritDoc} */
	@Override
	public GenericCondition exists(Query.Constant subquery)
	{
		return new GenericCondition(this)
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

	/** {@inheritDoc} */
	@Override
	public GenericCondition exists(Query.Constant.Builder subquery)
	{
		return exists(subquery.build());
	}

	static class Rollback extends GenericRelation
		implements GenericRelationMethods.Rollback
	{

		public Rollback(Clause clause)
		{
			super(clause);
		}

		@Override
		public GenericRelation not()
		{
			return this;
		}

		@Override
		public GenericRelation when(boolean assertion)
		{
			return this;
		}

		@Override
		public GenericCondition exists(Query.Constant.Builder subquery)
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		public GenericCondition exists(Query.Constant subquery)
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		public GenericPredicate not(Query.Constant.Builder subquery)
		{
			return new GenericPredicate.Rollback(getClause());
		}

		@Override
		public GenericPredicate not(Query.Constant subquery)
		{
			return new GenericPredicate.Rollback(getClause());
		}

		@Override
		public GenericPredicate subquery(Query.Constant.Builder subquery)
		{
			return new GenericPredicate.Rollback(getClause());
		}

		@Override
		public GenericPredicate subquery(Query.Constant subquery)
		{
			return new GenericPredicate.Rollback(getClause());
		}

		@Override
		public GenericCondition not(GenericCondition condition)
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		public GenericCondition not(ConstantCondition condition)
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		public GenericPredicate not(String expression)
		{
			return new GenericPredicate.Rollback(getClause());
		}

		@Override
		public GenericCondition condition(ConstantCondition condition)
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		public GenericPredicate expression(String expression)
		{
			return new GenericPredicate.Rollback(getClause());
		}

	}
}
