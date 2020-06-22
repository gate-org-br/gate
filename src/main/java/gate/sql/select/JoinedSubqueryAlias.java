package gate.sql.select;

import gate.sql.Clause;

public abstract class JoinedSubqueryAlias implements Clause, Aliasable
{

	private final Clause clause;

	public JoinedSubqueryAlias(Clause clause)
	{
		this.clause = clause;
	}

	@Override
	public Clause getClause()
	{
		return clause;
	}

	public abstract static class Constant extends JoinedSubqueryAlias implements Aliasable
	{

		public Constant(Clause clause)
		{
			super(clause);
		}

		@Override
		public JoinedSelect.Constant as(String alias)
		{
			return new JoinedSelect.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " as " + alias;
				}
			};
		}
	}

	public abstract static class Generic extends JoinedSubqueryAlias implements Aliasable
	{

		public Generic(Clause clause)
		{
			super(clause);
		}

		@Override
		public JoinedSelect.Generic as(String alias)
		{
			return new JoinedSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " as " + alias;
				}
			};
		}
	}

	public abstract static class Compiled extends JoinedSubqueryAlias implements Aliasable
	{

		public Compiled(Clause clause)
		{
			super(clause);
		}

		@Override
		public JoinedSelect.Compiled as(String alias)
		{
			return new JoinedSelect.Compiled(this)
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
