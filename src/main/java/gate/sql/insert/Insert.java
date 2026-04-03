package gate.sql.insert;

import java.util.Objects;

/**
 * SQL insert sentence builder.
 *
 * @author Davi Nunes da Silva
 */
public interface Insert
{

	static IgnoredInsert ignore()
	{
		return new IgnoredInsert();
	}

	/**
	 * Creates a new SQL insert sentence builder for a table.
	 *
	 * @param table name of the table where to insert
	 * @return the new insert sentence builder created
	 */
	static TableInsert into(String table)
	{
		return new TableInsert(table, false);
	}

	/**
	 * Creates a new SQL insert sentence builder for a java type using
	 * {@link gate.type.PropertyReference} accessors.
	 *
	 * @param type the java type to be persisted
	 * @return the new insert sentence builder created
	 */
	static <T> ClassInsert<T> into(Class<T> type) {return new ClassInsert<>(type, false);}

	/**
	 * Creates a new SQL insert sentence builder bound to an object.
	 *
	 * @param object source object to be persisted
	 * @return the new insert sentence builder created
	 */
	static <T> ObjectInsert<T> into(T object)
	{
		@SuppressWarnings("unchecked")
		Class<T> type = (Class<T>) Objects.requireNonNull(object).getClass();
		return new ObjectInsert<>(type, object, false);
	}
}
