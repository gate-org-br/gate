package gate.sql.select;

import gate.sql.Clause;
import gate.sql.statement.Query;
import java.util.stream.Stream;

public interface Projectable extends Clause
{

	ProjectedSelect expression(String exp);

	interface Constant extends Projectable
	{

		@Override
		default ProjectedSelect.Constant expression(String exp)
		{
			return new ProjectedSelect.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + ", " + exp;
				}
			};
		}

		default ProjectedSelect.Constant expression(Query.Constant query)
		{
			return new ProjectedSelect.Constant(this)
			{
				@Override
				public String toString()
				{
					return getClause() + ", (" + query.toString() + ")";
				}
			};
		}

		default ProjectedSelect.Compiled expression(Query.Compiled query)
		{
			return new ProjectedSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + ", (" + query.toString() + ")";
				}

				@Override
				public Stream<Object> getParameters()
				{
					return Stream.concat(getClause().getParameters(), query.getParameters().stream());
				}
			};
		}

		default ProjectedSelect.Constant expression(Query.Constant.Builder query)
		{
			return expression(query.build());
		}

		default ProjectedSelect.Compiled expression(Query.Compiled.Builder query)
			
		{
			return expression(query.build());
		}
	}

	interface Compiled extends Projectable
	{

		@Override
		default ProjectedSelect.Compiled expression(String exp)
		{
			return new ProjectedSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + ", " + exp;
				}
			};

		}

		default ProjectedSelect.Compiled expression(Query.Constant query)
		{
			return new ProjectedSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + ", (" + query.toString() + ")";
				}
			};
		}

		default ProjectedSelect.Compiled expression(Query.Compiled query)
		{
			return new ProjectedSelect.Compiled(this)
			{
				@Override
				public String toString()
				{
					return getClause() + ", (" + query.toString() + ")";
				}

				@Override
				public Stream<Object> getParameters()
				{
					return Stream.concat(getClause().getParameters(), query.getParameters().stream());
				}
			};
		}

		default ProjectedSelect.Compiled expression(Query.Constant.Builder query)
		{
			return expression(query.build());
		}

		default ProjectedSelect.Compiled expression(Query.Compiled.Builder query)
		{
			return expression(query.build());
		}
	}
}
