package gate.sql.condition;

import gate.sql.Clause;

/**
 * A single predicate of a compiled condition.
 */
public class ExtractorPredicate<T> extends Predicate implements
	ConstantPredicateMethods,
	ExtractorPredicateMethods<T>
{

	ExtractorPredicate(Clause clause)
	{
		super(clause);
	}

	@Override
	public ExtractorCondition<T> isNull()
	{
		return new ExtractorCondition<T>(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " is null";
			}
		};
	}

	@Override
	public ExtractorCondition<T> isNotNull()
	{
		return new ExtractorCondition<T>(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " is not null";
			}
		};
	}

	@Override
	public ExtractorCondition<T> isEq(String expression)
	{
		return new ExtractorCondition<T>(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " = " + expression;
			}
		};
	}

	@Override
	public ExtractorCondition<T> isNe(String expression)
	{
		return new ExtractorCondition<T>(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " <> " + expression;
			}
		};
	}

	@Override
	public ExtractorCondition<T> isLt(String expression)
	{
		return new ExtractorCondition<T>(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " < " + expression;
			}
		};
	}

	@Override
	public ExtractorCondition<T> isLe(String expression)
	{
		return new ExtractorCondition<T>(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " <= " + expression;
			}
		};
	}

	@Override
	public ExtractorCondition isGt(String expression)
	{
		return new ExtractorCondition<T>(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " > " + expression;
			}
		};
	}

	@Override
	public ExtractorCondition isGe(String expression)
	{
		return new ExtractorCondition<T>(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " >= " + expression;
			}
		};
	}

	@Override
	public ExtractorCondition isBw(String expression1, String expression2)
	{
		return new ExtractorCondition<T>(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " between " + expression1 + " and " + expression2;
			}
		};
	}

	static class Rollback<T> extends ExtractorPredicate<T> implements ExtractorPredicateMethods.Rollback<T>
	{

		public Rollback(Clause clause)
		{
			super(clause);
		}

		@Override
		public ExtractorCondition<T> isNull()
		{
			return new ExtractorCondition(getClause().rollback());
		}

		@Override
		public ExtractorCondition<T> isEq(String expression)
		{
			return new ExtractorCondition(getClause().rollback());
		}

		@Override
		public ExtractorCondition<T> isNe(String expression)
		{
			return new ExtractorCondition(getClause().rollback());
		}

		@Override
		public ExtractorCondition<T> isLt(String expression)
		{
			return new ExtractorCondition(getClause().rollback());
		}

		@Override
		public ExtractorCondition<T> isLe(String expression)
		{
			return new ExtractorCondition(getClause().rollback());
		}

		@Override
		public ExtractorCondition<T> isGt(String expression)
		{
			return new ExtractorCondition(getClause().rollback());
		}

		@Override
		public ExtractorCondition<T> isGe(String expression)
		{
			return new ExtractorCondition(getClause().rollback());
		}

		@Override
		public ExtractorCondition<T> isBw(String expression1, String expression2)
		{
			return new ExtractorCondition(getClause().rollback());
		}
	}
}
