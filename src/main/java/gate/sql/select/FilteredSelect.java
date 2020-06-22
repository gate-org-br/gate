package gate.sql.select;

import gate.sql.Clause;
import gate.sql.statement.Query;
import java.util.stream.Collectors;

public abstract class FilteredSelect implements
	Clause,
	Groupable,
	Orderable,
	Limitable
{

	private final Clause clause;

	public FilteredSelect(Clause clause)
	{
		this.clause = clause;
	}

	@Override
	public Clause getClause()
	{
		return clause;
	}

	public abstract static class Constant extends FilteredSelect implements
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

	public abstract static class Generic extends FilteredSelect implements
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

	public abstract static class Compiled extends FilteredSelect implements
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
			return Query.of(toString())
				.parameters(getParameters()
					.collect(Collectors.toList()));
		}
	}
}
