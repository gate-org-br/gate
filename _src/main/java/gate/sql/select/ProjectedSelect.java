package gate.sql.select;

import gate.sql.Clause;

public abstract class ProjectedSelect implements Clause, Aliasable, Projectable, Selectable
{

	private final Clause clause;

	public ProjectedSelect(Clause clause)
	{
		this.clause = clause;
	}

	@Override
	public Clause getClause()
	{
		return clause;
	}

	public abstract static class Constant extends ProjectedSelect implements
			Projectable.Constant,
			Selectable.Constant
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
					return getClause() + " as '" + alias + "'";
				}
			};
		}
	}

	public abstract static class Generic extends ProjectedSelect implements
			Projectable.Generic,
			Selectable.Generic
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
					return getClause() + " as '" + alias + "'";
				}
			};
		}
	}

	public abstract static class Compiled extends ProjectedSelect implements
			Projectable.Compiled,
			Selectable.Compiled
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
					return getClause() + " as '" + alias + "'";
				}
			};
		}
	}

	public static abstract class Aliased implements Clause, Projectable, Selectable
	{

		private final Clause clause;

		public Aliased(Clause clause)
		{
			this.clause = clause;
		}

		@Override
		public Clause getClause()
		{
			return clause;
		}

		public abstract static class Constant extends Aliased implements
				Projectable.Constant,
				Selectable.Constant
		{

			public Constant(Clause clause)
			{
				super(clause);
			}
		}

		public abstract static class Generic extends Aliased implements
				Projectable.Generic,
				Selectable.Generic
		{

			public Generic(Clause clause)
			{
				super(clause);
			}
		}

		public abstract static class Compiled extends Aliased implements
				Projectable.Compiled,
				Selectable.Compiled
		{

			public Compiled(Clause clause)
			{
				super(clause);
			}
		}
	}
}
