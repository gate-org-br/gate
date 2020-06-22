package gate.sql.select;

import gate.sql.Clause;
import gate.sql.statement.Query;
import java.util.stream.Stream;

public interface Unitable extends Clause
{

	UnitedSelect union(Query.Constant query);

	UnitedSelect union(Query.Constant.Builder query);

	interface Constant extends Unitable
	{

		@Override
		default UnitedSelect.Constant union(Query.Constant query)
		{
			return new UnitedSelect.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " union " + query.toString();
				}
			};
		}

		@Override
		default UnitedSelect.Constant union(Query.Constant.Builder query)
		{
			return union(query.build());
		}

		default UnitedSelect.Generic union(Query query)
		{
			return new UnitedSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " union " + query.toString();
				}
			};
		}

		default UnitedSelect.Generic union(Query.Builder query)
		{
			return union(query.build());
		}

		default UnitedSelect.Compiled union(Query.Compiled query)
		{
			return new UnitedSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " union " + query.toString();
				}

				@Override
				public Stream<Object> getParameters()
				{
					return Stream.concat(getClause().getParameters(),
						query.getParameters().stream());
				}
			};
		}

		default UnitedSelect.Compiled union(Query.Compiled.Builder query)
		{
			return union(query.build());
		}
	}

	interface Generic extends Unitable
	{

		@Override
		default UnitedSelect.Generic union(Query.Constant query)
		{
			return new UnitedSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " union " + query.toString();
				}
			};
		}

		@Override
		default UnitedSelect.Generic union(Query.Constant.Builder query)
		{
			return union(query.build());
		}

		default UnitedSelect.Generic union(Query query)
		{
			return new UnitedSelect.Generic(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " union " + query.toString();
				}
			};
		}

		default UnitedSelect.Generic union(Query.Builder query)
		{
			return union(query.build());
		}

	}

	interface Compiled extends Unitable
	{

		@Override
		default UnitedSelect.Compiled union(Query.Constant query)
		{
			return new UnitedSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " union " + query.toString();
				}
			};
		}

		@Override
		default UnitedSelect.Compiled union(Query.Constant.Builder query)
		{
			return union(query.build());
		}

		default UnitedSelect.Compiled union(Query.Compiled query)
		{
			return new UnitedSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + " union " + query.toString();
				}

				@Override
				public Stream<Object> getParameters()
				{
					return Stream.concat(getClause().getParameters(),
						query.getParameters().stream());
				}
			};
		}

		default UnitedSelect.Compiled union(Query.Compiled.Builder query)
		{
			return union(query.build());
		}
	}
}
