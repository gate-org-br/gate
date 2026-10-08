package gate.sql;

import gate.sql.statement.SQL;

/**
 * Builder of SQL objects.
 *
 * @param <T> SQL type produced by this builder
 */
public interface SQLBuilder<T extends SQL>
{

	/**
	 * Builds the SQL object represented by this builder.
	 *
	 * @return built SQL object
	 */
	T build();
}
