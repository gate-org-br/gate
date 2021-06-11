package gate.sql.condition;

import gate.sql.Clause;

/**
 * A single predicate of a generic condition.
 */
public abstract class GenericPredicate extends Predicate implements
	ConstantPredicateMethods,
	GenericPredicateMethods
{

	GenericPredicate(Clause clause)
	{
		super(clause);
	}

	@Override
	public GenericCondition isNull()
	{
		return new GenericCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " is null";
			}
		};
	}

	@Override
	public GenericCondition isNotNull()
	{
		return new GenericCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " is not null";
			}
		};
	}

	@Override
	public GenericCondition isEq(String expression)
	{
		return new GenericCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " = " + expression;
			}
		};
	}

	@Override
	public GenericCondition isNe(String expression)
	{
		return new GenericCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " <> " + expression;
			}
		};
	}

	@Override
	public GenericCondition isLt(String expression)
	{
		return new GenericCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " < " + expression;
			}
		};
	}

	@Override
	public GenericCondition isLe(String expression)
	{
		return new GenericCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " <= " + expression;
			}
		};
	}

	@Override
	public GenericCondition isGt(String expression)
	{
		return new GenericCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " > " + expression;
			}
		};
	}

	@Override
	public GenericCondition isGe(String expression)
	{
		return new GenericCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " >= " + expression;
			}
		};
	}

	@Override
	public GenericCondition isBw(String expression1, String expression2)
	{
		return new GenericCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " between " + expression1 + " and " + expression2;
			}
		};
	}

	static class Rollback extends GenericPredicate implements
		ConstantPredicateMethods,
		GenericPredicateMethods.Rollback
	{

		public Rollback(Clause clause)
		{
			super(clause);
		}

		@Override
		public GenericCondition isNull()
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		public GenericCondition isNotNull()
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		public GenericCondition isEq(String expression)
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		public GenericCondition isNe(String expression)
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		public GenericCondition isLt(String expression)
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		public GenericCondition isLe(String expression)
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		public GenericCondition isGt(String expression)
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		public GenericCondition isGe(String expression)
		{
			return new GenericCondition(getClause().rollback());
		}

		@Override
		public GenericCondition isBw(String expression1, String expression2)
		{
			return new GenericCondition(getClause().rollback());
		}

	}
}
