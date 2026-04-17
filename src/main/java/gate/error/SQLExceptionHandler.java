package gate.error;

import gate.sql.Link;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;

public abstract class SQLExceptionHandler
{
	private static final String SQL_STATE_NOT_NULL_VIOLATION = "23502";
	private static final String SQL_STATE_FOREIGN_KEY_VIOLATION = "23503";
	private static final String SQL_STATE_UNIQUE_VIOLATION = "23505";
	private static final String SQL_STATE_CHECK_VIOLATION = "23514";
	private static final String SQL_STATE_INTEGRITY_CLASS = "23";

	private enum DatabaseVendor
	{
		MYSQL
				{
					@Override
					String resolveState(SQLException cause)
					{
						return switch (cause.getErrorCode())
						{
							case 1062, 1169, 1586 -> SQL_STATE_UNIQUE_VIOLATION;
							case 1216, 1217, 1451, 1452 -> SQL_STATE_FOREIGN_KEY_VIOLATION;
							case 1048, 1364 -> SQL_STATE_NOT_NULL_VIOLATION;
							case 3819 -> SQL_STATE_CHECK_VIOLATION;
							default -> cause.getSQLState();
						};
					}
				},
		MARIADB
				{
					@Override
					String resolveState(SQLException cause)
					{
						return switch (cause.getErrorCode())
						{
							case 1062, 1169, 1586 -> SQL_STATE_UNIQUE_VIOLATION;
							case 1216, 1217, 1451, 1452 -> SQL_STATE_FOREIGN_KEY_VIOLATION;
							case 1048, 1364 -> SQL_STATE_NOT_NULL_VIOLATION;
							case 4025 -> SQL_STATE_CHECK_VIOLATION;
							default -> cause.getSQLState();
						};
					}
				},
		ORACLE
				{
					@Override
					String resolveState(SQLException cause)
					{
						return switch (cause.getErrorCode())
						{
							case 1 -> SQL_STATE_UNIQUE_VIOLATION;
							case 2291, 2292 -> SQL_STATE_FOREIGN_KEY_VIOLATION;
							case 1400, 1407 -> SQL_STATE_NOT_NULL_VIOLATION;
							case 2290 -> SQL_STATE_CHECK_VIOLATION;
							default -> cause.getSQLState();
						};
					}
				},
		SQL_SERVER
				{
					@Override
					String resolveState(SQLException cause)
					{
						return switch (cause.getErrorCode())
						{
							case 2601, 2627 -> SQL_STATE_UNIQUE_VIOLATION;
							case 547 -> SQL_STATE_FOREIGN_KEY_VIOLATION;
							case 515 -> SQL_STATE_NOT_NULL_VIOLATION;
							default -> cause.getSQLState();
						};
					}
				},
		POSTGRESQL,
		H2,
		UNKNOWN;

		private static final Map<String, DatabaseVendor> BY_PRODUCT_NAME = Map.of(
				"mysql", MYSQL,
				"mariadb", MARIADB,
				"postgresql", POSTGRESQL,
				"h2", H2,
				"oracle", ORACLE,
				"microsoft sql server", SQL_SERVER
		);

		static DatabaseVendor from(DatabaseMetaData metadata) throws SQLException
		{
			if (metadata == null)
				return UNKNOWN;
			return BY_PRODUCT_NAME.getOrDefault(metadata.getDatabaseProductName().toLowerCase(), UNKNOWN);
		}

		String resolveState(SQLException cause) {return cause.getSQLState();}
	}

	public static void handle(Link link, SQLException cause)
	{
		try
		{
			String sqlState = DatabaseVendor.from(link.getConnection().getMetaData()).resolveState(cause);
			if (sqlState != null)
				switch (sqlState)
				{
					case SQL_STATE_UNIQUE_VIOLATION -> throw new UKViolationException(cause);
					case SQL_STATE_FOREIGN_KEY_VIOLATION -> throw new FKViolationException(cause);
					case SQL_STATE_NOT_NULL_VIOLATION -> throw new InternalServerException("NOT NULL constraint violation", cause);
					case SQL_STATE_CHECK_VIOLATION -> throw new InternalServerException("CHECK constraint violation", cause);
					default ->
					{
						if (sqlState.startsWith(SQL_STATE_INTEGRITY_CLASS))
							throw new InternalServerException("Integrity constraint violation", cause);
					}
				}
			if (cause instanceof SQLIntegrityConstraintViolationException)
				throw new InternalServerException("Integrity constraint violation", cause);
			throw new InternalServerException(cause);
		} catch (SQLException ex)
		{
			throw new InternalServerException(ex);
		}
	}
}