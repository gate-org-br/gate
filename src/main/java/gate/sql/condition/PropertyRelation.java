package gate.sql.condition;

import gate.sql.Clause;
import gate.sql.statement.Query;

/**
 * A relation between two predicates of a compiled condition
 *
 * @see gate.sql.condition.PropertyCondition
 * @see gate.sql.condition.PropertyPredicate
 */
public class PropertyRelation extends Relation
	implements ConstantRelationMethods, PropertyRelationMethods
{

	PropertyRelation(Clause clause)
	{
		super(clause);
	}

	/** {@inheritDoc} */
	@Override
	public PropertyRelation when(boolean assertion)
	{
		return assertion ? this : new Rollback(getClause());
	}

	/** {@inheritDoc} */
	@Override
	public PropertyPredicate expression(String expression)
	{
		return new PropertyPredicate(this)
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
	public PropertyCondition condition(ConstantCondition expression)
	{
		return new PropertyCondition(this)
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

	/** {@inheritDoc} */
	@Override
	public PropertyPredicate subquery(Query.Constant subquery)
	{
		return new PropertyPredicate(this)
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
	public PropertyPredicate subquery(Query.Constant.Builder subquery)
	{
		return subquery(subquery.build());
	}

	/** {@inheritDoc} */
	@Override
	public PropertyCondition exists(Query.Constant subquery)
	{
		return new PropertyCondition(this)
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
	public PropertyCondition exists(Query.Constant.Builder subquery)
	{
		return exists(subquery.build());
	}

	/** {@inheritDoc} */
	@Override
	public PropertyPredicate not(Query.Constant subquery)
	{
		return new PropertyPredicate(this)
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
	public PropertyPredicate not(Query.Constant.Builder subquery)
	{
		return not(subquery.build());
	}

	/** {@inheritDoc} */
	@Override
	public PropertyRelation not()
	{
		return new PropertyRelation(this)
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
	public PropertyPredicate not(String expression)
	{
		return not().expression(expression);
	}

	/** {@inheritDoc} */
	@Override
	public PropertyCondition not(ConstantCondition expression)
	{
		return not().condition(expression);
	}

	/** {@inheritDoc} */
	@Override
	public PropertyCondition not(PropertyCondition condition)
	{
		return not().condition(condition);
	}

	static class Rollback extends PropertyRelation implements PropertyRelationMethods.Rollback
	{

		public Rollback(Clause clause)
		{
			super(clause);
		}

		@Override
		public PropertyRelation when(boolean assertion)
		{
			return this;
		}

		@Override
		public PropertyRelation not()
		{
			return this;
		}

		@Override
		public PropertyCondition not(PropertyCondition condition)
		{
			return new PropertyCondition(getClause().rollback());
		}

		@Override
		public PropertyCondition not(ConstantCondition expression)
		{
			return new PropertyCondition(getClause().rollback());
		}

		@Override
		public PropertyCondition condition(ConstantCondition condition)
		{
			return new PropertyCondition(getClause().rollback());
		}

		@Override
		public PropertyCondition exists(Query.Constant.Builder subquery)
		{
			return new PropertyCondition(getClause().rollback());
		}

		@Override
		public PropertyCondition exists(Query.Constant subquery)
		{
			return new PropertyCondition(getClause().rollback());
		}

		@Override
		public PropertyPredicate not(String expression)
		{
			return new PropertyPredicate.Rollback(getClause());
		}

		@Override
		public PropertyPredicate not(Query.Constant.Builder subquery)
		{
			return new PropertyPredicate.Rollback(getClause());
		}

		@Override
		public PropertyPredicate not(Query.Constant subquery)
		{
			return new PropertyPredicate.Rollback(getClause());
		}

		@Override
		public PropertyPredicate subquery(Query.Constant.Builder subquery)
		{
			return new PropertyPredicate.Rollback(getClause());
		}

		@Override
		public PropertyPredicate subquery(Query.Constant subquery)
		{
			return new PropertyPredicate.Rollback(getClause());
		}

		@Override
		public PropertyPredicate expression(String expression)
		{
			return new PropertyPredicate.Rollback(getClause());
		}
	}
}
