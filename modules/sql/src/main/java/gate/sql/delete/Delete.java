package gate.sql.delete;

/**
 * SQL delete sentence builder.
 *
 * @author Davi Nunes da Silva
 */
public interface Delete
{

	/**
	 * Creates a new SQL delete sentence builder for a table.
	 *
	 * @param table name of the table to delete
	 *
	 * @return the new delete sentence builder created
	 */
	static TableDelete from(String table)
	{
		return new TableDelete(table);
	}

	/**
	 * Creates a new SQL delete sentence builder for a java type.
	 *
	 * 
	 * @param type the java type to be deleted
	 *
	 * @return the new deleted sentence builder created
	 */
	static <T> TypedDelete<T> from(Class<T> type)
	{
		return new TypedDelete<>(type);
	}

}
