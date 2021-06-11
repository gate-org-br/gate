package gate.sql.condition;

import gate.sql.Clause;

/**
 * A single predicate of a compiled condition.
 */
public abstract class PropertyPredicate extends Predicate implements
	ConstantPredicateMethods,
	PropertyPredicateMethods
{

	PropertyPredicate(Clause clause)
	{
		super(clause);
	}

	@Override
	public PropertyCondition isNull()
	{
		return new PropertyCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " is null";
			}
		};
	}

	@Override
	public PropertyCondition isNotNull()
	{
		return new PropertyCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " is not null";
			}
		};
	}

	@Override
	public PropertyCondition isEq(String expression)
	{
		return new PropertyCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " = " + expression;
			}
		};
	}

	@Override
	public PropertyCondition isNe(String expression)
	{
		return new PropertyCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " <> " + expression;
			}
		};
	}

	@Override
	public PropertyCondition isLt(String expression)
	{
		return new PropertyCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " < " + expression;
			}
		};
	}

	@Override
	public PropertyCondition isLe(String expression)
	{
		return new PropertyCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " <= " + expression;
			}
		};
	}

	@Override
	public PropertyCondition isGt(String expression)
	{
		return new PropertyCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " > " + expression;
			}
		};
	}

	@Override
	public PropertyCondition isGe(String expression)
	{
		return new PropertyCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " >= " + expression;
			}
		};
	}

	@Override
	public PropertyCondition isBw(String expression1, String expression2)
	{
		return new PropertyCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " between " + expression1 + " and " + expression2;
			}
		};
	}

	static class Rollback extends PropertyPredicate implements PropertyPredicateMethods.Rollback
	{

		public Rollback(Clause clause)
		{
			super(clause);
		}

		@Override
		public PropertyCondition isNull()
		{
			return new PropertyCondition(getClause().rollback());
		}

		@Override
		public PropertyCondition isEq(String expression)
		{
			return new PropertyCondition(getClause().rollback());
		}

		@Override
		public PropertyCondition isNe(String expression)
		{
			return new PropertyCondition(getClause().rollback());
		}

		@Override
		public PropertyCondition isLt(String expression)
		{
			return new PropertyCondition(getClause().rollback());
		}

		@Override
		public PropertyCondition isLe(String expression)
		{
			return new PropertyCondition(getClause().rollback());
		}

		@Override
		public PropertyCondition isGt(String expression)
		{
			return new PropertyCondition(getClause().rollback());
		}

		@Override
		public PropertyCondition isGe(String expression)
		{
			return new PropertyCondition(getClause().rollback());
		}

		@Override
		public PropertyCondition isBw(String expression1, String expression2)
		{
			return new PropertyCondition(getClause().rollback());
		}
	}
}
