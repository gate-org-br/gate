package gate.sql;

import gate.sql.statement.SQL;

/**
 * Builder of SQL
 *
 * 
 */
public interface SQLBuilder<T extends SQL>
{

	/**
	 * Creates the SQL to be executed on the database.
	 *
	 * @return the SQL to be executed on the database
	 */
	T build();
}
