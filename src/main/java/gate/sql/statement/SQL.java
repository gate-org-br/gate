package gate.sql.statement;

import java.util.Collections;
import java.util.List;

/**
 * SQL statement to be executed on the database
 */
public interface SQL
{

	/**
	 * Returns the statement SQL string.
	 *
	 * @return the statement SQL string
	 */
	@Override
	public abstract String toString();

	/**
	 * Prints the statement.
	 *
	 * @return the same statement allowing for chained invocations
	 */
	public abstract SQL print();

	/**
	 * Returns the list of parameters associated with this SQL.
	 *
	 * @return the list of parameters associated with this SQL
	 */
	public default List<Object> getParameters()
	{
		return Collections.emptyList();
	}
}
