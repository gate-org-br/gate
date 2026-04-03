package gate.sql.replace;

import java.util.Objects;

/**
 * SQL replace sentence builder.
 *
 * @author Davi Nunes da Silva
 */
public interface Replace
{

	/**
	 * Creates a new SQL replace sentence builder for a table.
	 *
	 * @param table name of the table where to replace
	 *
	 * @return the new replace sentence builder created
	 */
	static TableReplace into(String table)
	{
		return new TableReplace(table);
	}

	/**
	 * Creates a new SQL replace sentence builder for a java type using
	 * {@link gate.type.PropertyReference} accessors.
	 *
	 * @param type the java type to be persisted
	 * @return the new replace sentence builder created
	 */
	static <T> ClassReplace<T> into(Class<T> type)
	{
		return new ClassReplace<>(type);
	}

	/**
	 * Creates a new SQL replace sentence builder bound to an object.
	 *
	 * @param object source object to be persisted
	 * @return the new replace sentence builder created
	 */
	static <T> ObjectReplace<T> into(T object)
	{
		@SuppressWarnings("unchecked")
		Class<T> type = (Class<T>) Objects.requireNonNull(object).getClass();
		return new ObjectReplace<>(type, object);
	}

}
