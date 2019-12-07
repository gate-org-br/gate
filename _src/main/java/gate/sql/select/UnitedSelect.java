package gate.sql.select;

import gate.sql.Clause;
import gate.sql.statement.Query;
import java.util.stream.Collectors;

public abstract class UnitedSelect implements Clause, Unitable
{

	private final Clause clause;

	public UnitedSelect(Clause clause)
	{
		this.clause = clause;
	}

	@Override
	public Clause getClause()
	{
		return clause;
	}

	public abstract static class Constant extends UnitedSelect implements Unitable.Constant,
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

	public abstract static class Generic extends UnitedSelect implements Unitable.Generic,
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

	public abstract static class Compiled extends UnitedSelect implements Unitable.Compiled,
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
