package gate.sql.select;

import gate.io.StringReader;
import gate.lang.property.Entity;
import gate.sql.Clause;
import gate.sql.Formatter;
import gate.sql.GQN;
import gate.sql.OrderBy;
import gate.sql.condition.CompiledCondition;
import gate.sql.condition.Condition;
import gate.sql.statement.Query;
import gate.util.Resources;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.stream.Stream;

public class Select implements Clause
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
	 *
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
	 *
	 * @return An SQLBuilder to describe the selection criteria, grouping and ordering clauses
	 */
	public static SelectedSelect.Constant of(URL resource)
	{
		return of(Resources.getTextResource(resource));
	}

	/**
	 * Creates a select builder from the specified SQL string.
	 * <p>
	 * Each @ symbol found on the query will be replaced by it's respective format argument.
	 *
	 * @param sql the query to be executed
	 * @param args arguments referenced by the @ symbols in the SQL string
	 *
	 * @return An SQLBuilder to describe the selection criteria, grouping and ordering clauses
	 */
	public static SelectedSelect.Constant of(String sql, String... args)
	{
		return new SelectedSelect.Constant(null)
		{
			@Override
			public String toString()
			{
				return Formatter.format(sql, (Object[]) args);
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
	 * Each @ symbol found on the query will be replaced by it's respective format argument.
	 *
	 * @param resource the SQL file with the query to be executed
	 * @param args arguments referenced by the @ symbols in the SQL string
	 *
	 * @return An SQLBuilder to describe the selection criteria, grouping and ordering clauses
	 */
	public static SelectedSelect.Constant of(URL resource, String... args)
	{
		try
		{
			return of(StringReader.read(resource), args);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * Creates a select builder from the specified SQL string.
	 * <p>
	 * Each @ symbol found on the query will be replaced by it's respective format argument.
	 *
	 * @param sql the query to be executed
	 * @param conditions arguments referenced by the @ symbols in the SQL string
	 *
	 * @return An SQLBuilder to describe the selection criteria, grouping and ordering clauses
	 */
	public static SelectedSelect.Compiled of(String sql, CompiledCondition... conditions)
	{
		return new SelectedSelect.Compiled(null)
		{
			@Override
			public String toString()
			{
				return Formatter.format(sql, (Object[]) conditions);
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
	 * Each @ symbol found on the query will be replaced by it's respective format argument.
	 *
	 * @param resource the SQL file with the query to be executed
	 * @param conditions arguments referenced by the @ symbols in the SQL string
	 *
	 * @return An SQLBuilder to describe the selection criteria, grouping and ordering clauses
	 */
	public static SelectedSelect.Compiled of(URL resource, CompiledCondition... conditions)
	{
		try
		{
			return of(StringReader.read(resource), conditions);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
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

	public static ProjectedSelect.Constant expression(Query query)
	{
		return new ProjectedSelect.Constant(new Select())
		{
			@Override
			public String toString()
			{
				return getClause() + ", (" + query.toString() + ")";
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

	public static ProjectedSelect.Constant expression(Query.Builder query)
	{
		return expression(query.build());
	}

	public static ProjectedSelect.Compiled expression(Query.Compiled.Builder query)
	{
		return expression(query.build());
	}

	/**
	 * Creates a query from GQN notation.
	 *
	 * 
	 * @param type the type of the entity to be selected
	 * @param notation GQN notation to be used to generate the query
	 *
	 * @return a new query object based on the specified type and the specified GQN notation
	 *
	 * @throws gate.error.PropertyError if specified type is not an entity
	 * @throws gate.error.NoSuchPropertyError if any of the specified properties is invalid
	 */
	public static <T> Query of(Class<T> type, String... notation)
	{
		GQN<T> GQN = new GQN<>(type, notation);
		OrderBy.Ordering ordering = GQN.getOrderBy();

		return ordering != null
			? Select.from(type)
				.properties(GQN.getProperties())
				.where(GQN.getCondition(Entity::getFullColumnName))
				.orderBy(ordering)
				.build()
			: Select.from(type)
				.properties(GQN.getProperties())
				.where(GQN.getCondition(Entity::getFullColumnName))
				.build();
	}

	/**
	 * Creates a compiled query from GQN notation ignoring null values.
	 *
	 * 
	 * @param type the type of the entity to be selected
	 * @param object the object whose properties will be compiled with the query
	 * @param notation GQN notation to be used to generate the query
	 *
	 * @return the entity matching the specified filter and the specified GQN notation
	 * @throws gate.error.PropertyError if specified type is not an entity
	 * @throws gate.error.NoSuchPropertyError if any of the specified properties is invalid
	 */
	public static <T> Query.Compiled of(Class<T> type, T object, String... notation)
	{
		GQN<T> GQN = new GQN<>(type, notation);
		OrderBy.Ordering ordering = GQN.getOrderBy();

		return ordering != null
			? Select.from(type)
				.properties(GQN.getProperties())
				.where(GQN.getCondition(object))
				.orderBy(ordering)
				.build()
			: Select.from(type)
				.properties(GQN.getProperties())
				.where(GQN.getCondition(object))
				.build();
	}

	public static <T> TypedSelect<T> from(Class<T> type)
	{
		return new TypedSelect<>(type);
	}
}
