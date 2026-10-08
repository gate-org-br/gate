package gate.sql.select;

import gate.sql.Clause;
import gate.sql.Formatter;
import gate.sql.condition.CompiledCondition;
import gate.sql.condition.Condition;
import gate.sql.statement.Query;
import gate.util.Resource;

import java.net.URL;
import java.util.Objects;
import java.util.stream.Stream;

public class Select implements SelectClause
{

	@Override
	public Clause getClause()
	{
		return null;
	}

	@Override
	public String toString()
	{
		return "select";
	}

	@Override
	public Stream<Object> getParameters()
	{
		return Stream.empty();
	}

	/**
	 * Creates a select builder from the specified SQL string.
	 *
	 * @param sql the query to be executed
	 * @return An SQLBuilder to describe the selection criteria, grouping and ordering clauses
	 */
	public static SelectedSelect.Constant of(String sql)
	{
		return new SelectedSelect.Constant(null)
		{
			@Override
			public String toString()
			{
				return sql;
			}

			@Override
			public Stream<Object> getParameters()
			{
				return Stream.empty();
			}
		};
	}

	/**
	 * Creates a select builder from the specified SQL file.
	 *
	 * @param resource the SQL file with the query to be executed
	 * @return An SQLBuilder to describe the selection criteria, grouping and ordering clauses
	 */
	public static SelectedSelect.Constant of(URL resource)
	{
		Objects.requireNonNull(resource);
		return of(Resource.read(resource));
	}

	/**
	 * Creates a select builder from the specified SQL string.
	 * <p>
	 * Each @ symbol found on the query will be replaced by its respective format argument.
	 *
	 * @param sql  the query to be executed
	 * @param args arguments referenced by the @ symbols in the SQL string
	 * @return An SQLBuilder to describe the selection criteria, grouping and ordering clauses
	 */
	public static SelectedSelect.Constant of(String sql, String... args)
	{
		return new SelectedSelect.Constant(null)
		{
			@Override
			public String toString()
			{
				return Formatter.sql(sql, (Object[]) args);
			}

			@Override
			public Stream<Object> getParameters()
			{
				return Stream.empty();
			}
		};
	}

	/**
	 * Creates a select builder from the specified SQL string.
	 * <p>
	 * Each @ symbol found on the query will be replaced by its respective format argument.
	 *
	 * @param resource the SQL file with the query to be executed
	 * @param args     arguments referenced by the @ symbols in the SQL string
	 * @return An SQLBuilder to describe the selection criteria, grouping and ordering clauses
	 */
	public static SelectedSelect.Constant of(URL resource, String... args)
	{
		return of(Resource.read(resource), args);
	}

	/**
	 * Creates a select builder from the specified SQL string.
	 * <p>
	 * Each @ symbol found on the query will be replaced by its respective format argument.
	 *
	 * @param sql        the query to be executed
	 * @param conditions arguments referenced by the @ symbols in the SQL string
	 * @return An SQLBuilder to describe the selection criteria, grouping and ordering clauses
	 */
	public static SelectedSelect.Compiled of(String sql, CompiledCondition... conditions)
	{
		return new SelectedSelect.Compiled(null)
		{
			@Override
			public String toString()
			{
				return Formatter.sql(sql, (Object[]) conditions);
			}

			@Override
			public Stream<Object> getParameters()
			{
				return Stream.of(conditions)
						.flatMap(Condition::getParameters);
			}

		};
	}

	/**
	 * Creates a select builder from the specified SQL string.
	 * <p>
	 * Each @ symbol found on the query will be replaced by its respective format argument.
	 *
	 * @param resource   the SQL file with the query to be executed
	 * @param conditions arguments referenced by the @ symbols in the SQL string
	 * @return An SQLBuilder to describe the selection criteria, grouping and ordering clauses
	 */
	public static SelectedSelect.Compiled of(URL resource, CompiledCondition... conditions)
	{
		return of(Resource.read(resource), conditions);
	}

	public static ProjectedSelect.Constant expression(String expression)
	{
		return new ProjectedSelect.Constant(new Select())
		{
			@Override
			public String toString()
			{
				return getClause() + " " + expression;
			}
		};
	}

	public static ProjectedSelect.Compiled expression(Query.Compiled query)
	{
		return new ProjectedSelect.Compiled(new Select())
		{
			@Override
			public String toString()
			{
				return getClause() + ", (" + query.toString() + ")";
			}
		};
	}

	public static ProjectedSelect.Compiled expression(Query.Compiled.Builder query)
	{
		return expression(query.build());
	}

	public static Query.Compiled.Builder exists(SelectClause select)
	{
		return new Query.Compiled.Builder()
		{
			@Override
			public String toString()
			{
				return "select exists (" + select.toString() + ")";
			}

			@Override
			public Query.Compiled build()
			{
				return Query.of(toString(), select.getParameters().toList());
			}
		};
	}

}
