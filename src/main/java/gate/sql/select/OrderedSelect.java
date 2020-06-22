package gate.sql.select;

import gate.sql.Clause;
import gate.sql.statement.Query;
import java.util.stream.Collectors;

public abstract class OrderedSelect implements Clause, Limitable, Sortable
{

	private final Clause clause;

	public OrderedSelect(Clause clause)
	{
		this.clause = clause;
	}

	@Override
	public Clause getClause()
	{
		return clause;
	}

	public abstract OrderedSelect and(String exp);

	public abstract static class Constant extends OrderedSelect implements
			Orderable.Constant,
			Sortable.Constant,
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
			return Query.of(toString())
					.constant();
		}
	}

	public abstract static class Generic extends OrderedSelect implements
			Orderable.Generic,
			Sortable.Generic,
			Limitable.Generic, Query.Builder
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

	public abstract static class Compiled extends OrderedSelect implements
			Orderable.Compiled,
			Sortable.Compiled,
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
