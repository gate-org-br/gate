package gate.sql.select;

import gate.sql.Clause;
import gate.sql.statement.Query;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class ForUpdateSelect implements Clause
{

	private final Clause clause;

	public ForUpdateSelect(Clause clause)
	{
		this.clause = clause;
	}

	public abstract ForUpdateSelect of(String... expressions);

	@Override
	public Clause getClause()
	{
		return clause;
	}

	public abstract static class Constant extends ForUpdateSelect implements Query.Constant.Builder
	{

		public Constant(Clause clause)
		{
			super(clause);
		}

		@Override
		public ForUpdateSelect.Constant of(String... expressions)
		{
			Objects.requireNonNull(expressions, "Attempt to define a null FOR UPDATE OF clause");
			return new ForUpdateSelect.Constant(this)
			{
				@Override
				public String toString()
				{
					return expressions.length == 0
						? getClause().toString()
						: getClause() + " of " + String.join(", ", expressions);
				}
			};
		}

		@Override
		public Query.Constant build()
		{
			return Query.of(toString()).constant();
		}
	}

	public abstract static class Generic extends ForUpdateSelect implements Query.Builder
	{

		public Generic(Clause clause)
		{
			super(clause);
		}

		@Override
		public ForUpdateSelect.Generic of(String... expressions)
		{
			Objects.requireNonNull(expressions, "Attempt to define a null FOR UPDATE OF clause");
			return new ForUpdateSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return expressions.length == 0
						? getClause().toString()
						: getClause() + " of " + String.join(", ", expressions);
				}
			};
		}

		@Override
		public Query build()
		{
			return Query.of(toString());
		}
	}

	public abstract static class Compiled extends ForUpdateSelect implements Query.Compiled.Builder
	{

		public Compiled(Clause clause)
		{
			super(clause);
		}

		@Override
		public ForUpdateSelect.Compiled of(String... expressions)
		{
			Objects.requireNonNull(expressions, "Attempt to define a null FOR UPDATE OF clause");
			return new ForUpdateSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return expressions.length == 0
						? getClause().toString()
						: getClause() + " of " + String.join(", ", expressions);
				}
			};
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
