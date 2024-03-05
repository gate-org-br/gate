package gate.sql.select;

import gate.sql.Clause;
import gate.sql.condition.ConstantCondition;
import gate.sql.statement.Query;
import java.util.stream.Collectors;

public abstract class WithRollupSelect implements Clause, Orderable, Limitable
{

	private final Clause clause;

	public WithRollupSelect(Clause clause)
	{
		this.clause = clause;
	}

	@Override
	public Clause getClause()
	{
		return clause;
	}

	public abstract static class Constant extends WithRollupSelect implements
		Refinable.Constant,
		Orderable.Constant,
		Limitable.Constant,
		Query.Constant.Builder
	{

		public Constant(Clause clause)
		{
			super(clause);
		}

		@Override
		public RefinedSelect.Constant having(ConstantCondition predicate)
		{
			return new RefinedSelect.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " having " + predicate.toString();
				}
			};
		}

		@Override
		public Query.Constant build()
		{
			return Query.of(toString()).constant();
		}
	}

	public static abstract class Generic extends WithRollupSelect implements
		Refinable.Generic,
		Orderable.Generic,
		Limitable.Generic,
		Query.Builder
	{

		public Generic(Clause clause)
		{
			super(clause);
		}

		@Override
		public RefinedSelect.Generic having(ConstantCondition predicate)
		{
			return new RefinedSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " having " + predicate.toString();
				}
			};
		}

		@Override
		public Query build()
		{
			return Query.of(toString());
		}
	}

	public static abstract class Compiled extends WithRollupSelect implements
		Refinable.Compiled,
		Orderable.Compiled,
		Limitable.Compiled,
		Query.Compiled.Builder
	{

		public Compiled(Clause clause)
		{
			super(clause);
		}

		@Override
		public RefinedSelect.Compiled having(ConstantCondition predicate)
		{
			return new RefinedSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " having " + predicate.toString();
				}
			};
		}

		@Override
		public Query.Compiled build()
		{
			return Query.of(toString())
				.parameters(getParameters()
					.collect(Collectors.toList()));
		}
	}

}
