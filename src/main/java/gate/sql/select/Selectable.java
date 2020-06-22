package gate.sql.select;

import gate.sql.Clause;
import gate.sql.statement.Query;
import java.util.stream.Stream;

public interface Selectable extends Clause
{

	SelectedSelect from(String exp);

	interface Constant extends Selectable
	{

		@Override
		default SelectedSelect.Constant from(String exp)
		{
			return new SelectedSelect.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " from " + exp;
				}
			};
		}

		default SelectedSubqueryAlias.Constant from(Query.Constant query)
		{
			return new SelectedSubqueryAlias.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " from (" + query.toString() + ")";
				}
			};
		}

		default SelectedSubqueryAlias.Generic from(Query query)
		{
			return new SelectedSubqueryAlias.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " from (" + query.toString() + ")";
				}
			};
		}

		default SelectedSubqueryAlias.Compiled from(Query.Compiled query)
		{
			return new SelectedSubqueryAlias.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " from (" + query.toString() + ")";
				}

				@Override
				public Stream<Object> getParameters()
				{
					return Stream.concat(getClause().getParameters(),
							query.getParameters().stream());
				}
			};
		}

		default SelectedSubqueryAlias.Constant from(Query.Constant.Builder query)
		{
			return from(query.build());
		}

		default SelectedSubqueryAlias.Generic from(Query.Builder query)
		{
			return from(query.build());
		}

		default SelectedSubqueryAlias.Compiled from(Query.Compiled.Builder query)
		{
			return from(query.build());
		}
	}

	interface Generic extends Selectable
	{

		@Override
		default SelectedSelect.Generic from(String exp)
		{
			return new SelectedSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " from " + exp;
				}
			};
		}

		default SelectedSubqueryAlias.Generic from(Query.Constant query)
		{
			return new SelectedSubqueryAlias.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " from (" + query.toString() + ")";
				}
			};
		}

		default SelectedSubqueryAlias.Generic from(Query query)
		{
			return new SelectedSubqueryAlias.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " from (" + query.toString() + ")";
				}
			};
		}

		default SelectedSubqueryAlias.Generic from(Query.Constant.Builder query)
		{
			return from(query.build());
		}

		default SelectedSubqueryAlias.Generic from(Query.Builder query)
		{
			return from(query.build());
		}
	}

	interface Compiled extends Selectable
	{

		@Override
		default SelectedSelect.Compiled from(String exp)
		{
			return new SelectedSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " from " + exp;
				}
			};
		}

		default SelectedSubqueryAlias.Compiled from(Query.Constant query)
		{
			return new SelectedSubqueryAlias.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " from (" + query.toString() + ")";
				}
			};
		}

		default SelectedSubqueryAlias.Compiled from(Query.Compiled query)
		{
			return new SelectedSubqueryAlias.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " from (" + query.toString() + ")";
				}

				@Override
				public Stream<Object> getParameters()
				{
					return Stream.concat(getClause().getParameters(),
							query.getParameters().stream());
				}
			};
		}

		default SelectedSubqueryAlias.Compiled from(Query.Constant.Builder query)
		{
			return from(query.build());
		}

		default SelectedSubqueryAlias.Compiled from(Query.Compiled.Builder query)
		{
			return from(query.build());
		}

	}
}
