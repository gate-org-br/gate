package gate.sql.select;

import gate.sql.Clause;
import gate.sql.statement.Query;
import java.util.Collections;
import java.util.stream.Collectors;

public abstract class RefinedSelect implements Clause, Orderable, Limitable
{

	private final Clause clause;

	public RefinedSelect(Clause clause)
	{
		this.clause = clause;
	}

	@Override
	public Clause getClause()
	{
		return clause;
	}

	public abstract static class Constant extends RefinedSelect implements
			Orderable.Constant,
			Limitable.Constant,
			Query.Compiled.Builder
	{

		public Constant(Clause clause)
		{
			super(clause);
		}

		@Override
		public Query.Compiled build()
		{
			return Query.of(toString())
					.parameters(Collections.emptyList());
		}
	}

	public abstract static class Generic extends RefinedSelect implements
			Orderable.Constant,
			Limitable.Constant,
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

	public abstract static class Compiled extends RefinedSelect implements
			Orderable.Compiled,
			Limitable.Compiled,
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
