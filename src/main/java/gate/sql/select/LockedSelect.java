package gate.sql.select;

import gate.sql.Clause;
import gate.sql.statement.Query;

import java.util.stream.Collectors;

public abstract class LockedSelect implements SelectClause, OfAble
{

	private final Clause clause;

	public LockedSelect(Clause clause)
	{
		this.clause = clause;
	}


	@Override
	public Clause getClause()
	{
		return clause;
	}

	public abstract static class Constant extends LockedSelect implements OfAble.Constant, Query.Constant.Builder
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

	public static abstract class Compiled extends LockedSelect implements OfAble.Compiled, Query.Compiled.Builder
	{

		public Compiled(Clause clause)
		{
			super(clause);
		}

		@Override
		public Query.Compiled build()
		{
			return Query.of(toString(), getParameters()
					.collect(Collectors.toList()));
		}
	}

}
