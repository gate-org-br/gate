package gate.sql.select;

import gate.sql.Clause;
import gate.sql.statement.Query;
import java.util.stream.Collectors;

public abstract class SelectedSelect implements Clause, Groupable, Orderable, Limitable
{

	private final Clause clause;

	public SelectedSelect(Clause clause)
	{
		this.clause = clause;
	}

	@Override
	public Clause getClause()
	{
		return clause;
	}

	public abstract static class Constant extends SelectedSelect implements
			Joinable.Constant,
			Filterable.Constant,
			Groupable.Constant,
			Orderable.Constant,
			Limitable.Constant,
			Unitable.Constant,
			Aliasable,
			Query.Constant.Builder
	{

		public Constant(Clause clause)
		{
			super(clause);
		}

		@Override
		public Aliased.Constant as(String alias)
		{
			return new Aliased.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " as " + alias;
				}
			};
		}

		@Override
		public Query.Constant build()
		{
			return Query.of(toString()).constant();
		}
	}

	public abstract static class Generic extends SelectedSelect implements
			Joinable.Generic,
			Filterable.Generic,
			Groupable.Generic,
			Orderable.Generic,
			Limitable.Generic,
			Unitable.Generic,
			Aliasable,
			Query.Builder
	{

		public Generic(Clause clause)
		{
			super(clause);
		}

		@Override
		public Aliased.Generic as(String alias)
		{
			return new Aliased.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " as " + alias;
				}
			};
		}

		@Override
		public Query build()
		{
			return Query.of(toString());
		}
	}

	public abstract static class Compiled extends SelectedSelect implements
			Joinable.Compiled,
			Filterable.Compiled,
			Groupable.Compiled,
			Orderable.Compiled,
			Limitable.Compiled,
			Unitable.Compiled,
			Aliasable,
			Query.Compiled.Builder
	{

		public Compiled(Clause clause)
		{
			super(clause);
		}

		@Override
		public Aliased.Compiled as(String alias)
		{
			return new Aliased.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " as " + alias;
				}
			};
		}

		@Override
		public Query.Compiled build()
		{
			return Query.of(toString()).parameters(getParameters()
					.collect(Collectors.toList()));
		}
	}

	public static abstract class Aliased extends SelectedSelect
	{

		public Aliased(Clause clause)
		{
			super(clause);
		}

		public abstract static class Constant extends Aliased implements
				Joinable.Constant,
				Filterable.Constant,
				Groupable.Constant,
				Orderable.Constant,
				Limitable.Constant,
				Unitable.Constant,
				Query.Constant.Builder
		{

			public Constant(Clause clause)
			{
				super(clause);
			}

			@Override
			public Query.Constant build()
			{
				return Query.of(toString()).constant();
			}
		}

		public abstract static class Generic extends Aliased implements
				Joinable.Generic,
				Filterable.Generic,
				Groupable.Generic,
				Orderable.Generic,
				Limitable.Generic,
				Unitable.Generic,
				Query.Builder
		{

			public Generic(Clause clause)
			{
				super(clause);
			}

			@Override
			public Query build()
			{
				return Query.of(toString());
			}
		}

		public abstract static class Compiled extends Aliased implements
				Joinable.Compiled,
				Filterable.Compiled,
				Groupable.Compiled,
				Orderable.Compiled,
				Limitable.Compiled,
				Unitable.Compiled,
				Query.Compiled.Builder
		{

			public Compiled(Clause clause)
			{
				super(clause);
			}

			@Override
			public Query.Compiled build()
			{
				return Query.of(toString()).parameters(getParameters()
						.collect(Collectors.toList()));
			}

		}
	}

}
