package gate.sql.select;

import gate.sql.Clause;

public abstract class SelectedSubqueryAlias implements Clause, Aliasable
{

	private final Clause clause;

	public SelectedSubqueryAlias(Clause clause)
	{
		this.clause = clause;
	}

	@Override
	public Clause getClause()
	{
		return clause;
	}

	public abstract static class Constant extends SelectedSubqueryAlias implements Aliasable
	{

		public Constant(Clause clause)
		{
			super(clause);
		}

		@Override
		public SelectedSelect.Constant as(String alias)
		{
			return new SelectedSelect.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " as " + alias;
				}
			};
		}
	}

	public abstract static class Generic extends SelectedSubqueryAlias implements Aliasable
	{

		public Generic(Clause clause)
		{
			super(clause);
		}

		@Override
		public SelectedSelect.Generic as(String alias)
		{
			return new SelectedSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " as " + alias;
				}
			};
		}
	}

	public abstract static class Compiled extends SelectedSubqueryAlias implements Aliasable
	{

		public Compiled(Clause clause)
		{
			super(clause);
		}

		@Override
		public SelectedSelect.Compiled as(String alias)
		{
			return new SelectedSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " as " + alias;
				}
			};
		}
	}
}
