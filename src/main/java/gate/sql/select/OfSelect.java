package gate.sql.select;

import gate.sql.Clause;
import gate.sql.statement.Query;

import java.util.stream.Collectors;

public abstract class OfSelect implements SelectClause
{

	private final Clause clause;

	public OfSelect(Clause clause)
	{
		this.clause = clause;
	}

	@Override
	public Clause getClause()
	{
		return clause;
	}

	public abstract static class Constant extends OfSelect implements Query.Constant.Builder
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

	public static abstract class Generic extends OfSelect implements Query.Builder
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

	public static abstract class Compiled extends OfSelect implements Query.Compiled.Builder
	{

		public Compiled(Clause clause)
		{
			super(clause);
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