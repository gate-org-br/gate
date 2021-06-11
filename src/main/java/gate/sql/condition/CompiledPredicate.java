package gate.sql.condition;

import gate.sql.Clause;
import java.util.function.Supplier;

/**
 * A single predicate of a compiled condition.
 */
public abstract class CompiledPredicate extends Predicate implements
	ConstantPredicateMethods,
	CompiledPredicateMethods
{

	CompiledPredicate(Clause clause)
	{
		super(clause);
	}

	@Override
	public CompiledCondition isNull()
	{
		return new CompiledCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " is null";
			}
		};
	}

	@Override
	public CompiledCondition isNotNull()
	{
		return new CompiledCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " is not null";
			}
		};
	}

	@Override
	public CompiledCondition isEq(String expression)
	{
		return new CompiledCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " = " + expression;
			}
		};
	}

	@Override
	public CompiledCondition isNe(String expression)
	{
		return new CompiledCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " <> " + expression;
			}
		};
	}

	@Override
	public CompiledCondition isLt(String expression)
	{
		return new CompiledCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " < " + expression;
			}
		};
	}

	@Override
	public CompiledCondition isLe(String expression)
	{
		return new CompiledCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " <= " + expression;
			}
		};
	}

	@Override
	public CompiledCondition isGt(String expression)
	{
		return new CompiledCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " > " + expression;
			}
		};
	}

	@Override
	public CompiledCondition isGe(String expression)
	{
		return new CompiledCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " >= " + expression;
			}
		};
	}

	@Override
	public CompiledCondition isBw(String expression1, String expression2)
	{
		return new CompiledCondition(this)
		{
			@Override
			public String toString()
			{
				return getClause() + " between " + expression1 + " and " + expression2;
			}
		};
	}

	static class Rollback extends CompiledPredicate implements CompiledPredicateMethods.Rollback
	{

		public Rollback(Clause clause)
		{
			super(clause);
		}

		@Override
		public CompiledCondition isNull()
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		public CompiledCondition isNotNull()
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		public CompiledCondition isEq(String expression)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		public CompiledCondition isNe(String expression)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		public CompiledCondition isLt(String expression)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		public CompiledCondition isLe(String expression)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		public CompiledCondition isGt(String expression)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		public CompiledCondition isGe(String expression)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		public CompiledCondition isBw(String expression1, String expression2)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		public CompiledCondition eqGet(Supplier<Object> supplier)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		public CompiledCondition neGet(Supplier<Object> supplier)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		public CompiledCondition ltGet(Supplier<Object> supplier)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		public CompiledCondition leGet(Supplier<Object> supplier)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		public CompiledCondition gtGet(Supplier<Object> supplier)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		public CompiledCondition geGet(Supplier<Object> supplier)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		public CompiledCondition lkGet(Supplier<Object> supplier)
		{
			return new CompiledCondition(getClause().rollback());
		}

		@Override
		public CompiledCondition rxGet(Supplier<Object> supplier)
		{
			return new CompiledCondition(getClause().rollback());
		}
	}
}
