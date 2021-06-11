package gate.sql.condition;

import gate.sql.Clause;

/**
 * A single predicate of a constant condition.
 */
public abstract class ConstantPredicate extends Predicate
	implements
	ConstantPredicateMethods,
	GenericPredicateMethods,
	CompiledPredicateMethods,
	PropertyPredicateMethods
{

	ConstantPredicate(Clause clause)
	{
		super(clause);
	}

	@Override
	public ConstantCondition isNull()
	{
		return new ConstantCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " is null";
			}
		};
	}

	@Override
	public ConstantCondition isNotNull()
	{
		return new ConstantCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " is not null";
			}
		};
	}

	@Override
	public ConstantCondition isEq(String expression)
	{
		return new ConstantCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " = " + expression;
			}
		};
	}

	@Override
	public ConstantCondition isNe(String expression)
	{
		return new ConstantCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " <> " + expression;
			}
		};
	}

	@Override
	public ConstantCondition isLt(String expression)
	{
		return new ConstantCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " < " + expression;
			}
		};
	}

	@Override
	public ConstantCondition isLe(String expression)
	{
		return new ConstantCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " <= " + expression;
			}
		};
	}

	@Override
	public ConstantCondition isGt(String expression)
	{
		return new ConstantCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " > " + expression;
			}
		};
	}

	@Override
	public ConstantCondition isGe(String expression)
	{
		return new ConstantCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " >= " + expression;
			}
		};
	}

	@Override
	public ConstantCondition isBw(String expression1, String expression2)
	{
		return new ConstantCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " between " + expression1 + " and " + expression2;
			}
		};
	}

	static class Rollback extends ConstantPredicate
		implements ConstantPredicateMethods,
		GenericPredicateMethods.Rollback,
		CompiledPredicateMethods.Rollback
	{

		public Rollback(Clause clause)
		{
			super(clause);
		}

		@Override
		public ConstantCondition isNull()
		{
			return new ConstantCondition(getClause().rollback());
		}

		@Override
		public ConstantCondition isEq(String expression)
		{
			return new ConstantCondition(getClause().rollback());
		}

		@Override
		public ConstantCondition isNe(String expression)
		{
			return new ConstantCondition(getClause().rollback());
		}

		@Override
		public ConstantCondition isLt(String expression)
		{
			return new ConstantCondition(getClause().rollback());
		}

		@Override
		public ConstantCondition isLe(String expression)
		{
			return new ConstantCondition(getClause().rollback());
		}

		@Override
		public ConstantCondition isGt(String expression)
		{
			return new ConstantCondition(getClause().rollback());
		}

		@Override
		public ConstantCondition isGe(String expression)
		{
			return new ConstantCondition(getClause().rollback());
		}

		@Override
		public ConstantCondition isBw(String expression1, String expression2)
		{
			return new ConstantCondition(getClause().rollback());
		}

		@Override
		public ConstantCondition isNotNull()
		{
			return new ConstantCondition(getClause().rollback());
		}

	}
}
