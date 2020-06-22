package gate.sql.select;

import gate.sql.Clause;
import gate.sql.statement.Query;
import java.util.stream.Collectors;

public abstract class SortedSelect implements Clause, Limitable
{

	private final Clause clause;

	public SortedSelect(Clause clause)
	{
		this.clause = clause;
	}

	@Override
	public Clause getClause()
	{
		return clause;
	}

	public abstract OrderedSelect and(String exp);

	public abstract static class Constant extends SortedSelect implements
			Limitable.Constant,
			Query.Constant.Builder
	{

		public Constant(Clause clause)
		{
			super(clause);
		}

		@Override
		public OrderedSelect.Constant and(String exp)
		{
			return new OrderedSelect.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + ", " + exp;
				}
			};
		}

		@Override
		public Query.Constant build()
		{
			return Query.of(toString()).constant();
		}

	}

	public abstract static class Generic extends SortedSelect implements
			Limitable.Generic,
			Query.Builder
	{

		public Generic(Clause clause)
		{
			super(clause);
		}

		@Override
		public OrderedSelect.Generic and(String exp)
		{
			return new OrderedSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + ", " + exp;
				}
			};
		}

		@Override
		public Query build()
		{
			return Query.of(toString());
		}
	}

	public abstract static class Compiled extends SortedSelect implements
			Limitable.Compiled,
			Query.Compiled.Builder
	{

		public Compiled(Clause clause)
		{
			super(clause);
		}

		@Override
		public OrderedSelect.Compiled and(String exp)
		{
			return new OrderedSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + ", " + exp;
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
