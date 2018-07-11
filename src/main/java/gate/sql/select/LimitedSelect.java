package gate.sql.select;

import gate.sql.Clause;
import gate.sql.statement.Query;
import java.util.stream.Collectors;

public abstract class LimitedSelect implements Clause
{

	private final Clause clause;

	public LimitedSelect(Clause clause)
	{
		this.clause = clause;
	}

	@Override
	public Clause getClause()
	{
		return clause;
	}

	public abstract static class Constant extends LimitedSelect implements
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

	public abstract static class Generic extends LimitedSelect implements
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

	public abstract static class Compiled extends LimitedSelect implements
			Query.Compiled.Builder
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
