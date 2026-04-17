package gate.error;

import gate.sql.Link;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SQLExceptionHandlerTest
{
	@Test
	void shouldMapMySqlDuplicateKeyToUniqueViolation()
	{
		SQLException exception = new SQLException(
				"Duplicate entry 'john@doe' for key 'user.uk_email'", "23000", 1062);

		RuntimeException thrown = assertThrows(RuntimeException.class,
				() -> SQLExceptionHandler.handle(link("MySQL"), exception));

		assertInstanceOf(UKViolationException.class, thrown);
	}

	@Test
	void shouldMapOracleForeignKeyViolationByErrorCode()
	{
		SQLException exception = new SQLException("ORA-02292", "72000", 2292);

		RuntimeException thrown = assertThrows(RuntimeException.class,
				() -> SQLExceptionHandler.handle(link("Oracle"), exception));

		assertInstanceOf(FKViolationException.class, thrown);
	}

	@Test
	void shouldMapSqlServerUniqueViolationByErrorCode()
	{
		SQLException exception = new SQLException("Cannot insert duplicate key row", "23000", 2627);

		RuntimeException thrown = assertThrows(RuntimeException.class,
				() -> SQLExceptionHandler.handle(link("Microsoft SQL Server"), exception));

		assertInstanceOf(UKViolationException.class, thrown);
	}

	@Test
	void shouldMapPostgreSqlForeignKeyViolationBySqlState()
	{
		SQLException exception = new SQLException(
				"insert or update on table violates foreign key constraint", "23503", 0);

		RuntimeException thrown = assertThrows(RuntimeException.class,
				() -> SQLExceptionHandler.handle(link("PostgreSQL"), exception));

		assertInstanceOf(FKViolationException.class, thrown);
	}

	@Test
	void shouldMapH2IntegrityViolationBySqlStateClass()
	{
		SQLException exception = new SQLException("Referential integrity violation", "23506", 23506);

		RuntimeException thrown = assertThrows(RuntimeException.class,
				() -> SQLExceptionHandler.handle(link("H2"), exception));

		assertInstanceOf(InternalServerException.class, thrown);
	}

	@Test
	void shouldFallbackToInternalServerExceptionForUnknownDatabaseError()
	{
		SQLException exception = new SQLException("Unexpected failure", null, 9999);

		RuntimeException thrown = assertThrows(RuntimeException.class,
				() -> SQLExceptionHandler.handle(link("UnknownDB"), exception));

		assertInstanceOf(InternalServerException.class, thrown);
	}

	private static Link link(String databaseProductName)
	{
		DatabaseMetaData metadata = (DatabaseMetaData) Proxy.newProxyInstance(
				SQLExceptionHandlerTest.class.getClassLoader(),
				new Class[]{DatabaseMetaData.class},
				(proxy, method, args) ->
				{
					if ("getDatabaseProductName".equals(method.getName()))
						return databaseProductName;

					if ("unwrap".equals(method.getName()))
						return null;

					if ("isWrapperFor".equals(method.getName()))
						return false;

					throw new UnsupportedOperationException(method.getName());
				});

		Connection connection = (Connection) Proxy.newProxyInstance(
				SQLExceptionHandlerTest.class.getClassLoader(),
				new Class[]{Connection.class},
				(proxy, method, args) ->
				{
					if ("getMetaData".equals(method.getName()))
						return metadata;

					if ("unwrap".equals(method.getName()))
						return null;

					if ("isWrapperFor".equals(method.getName()))
						return false;

					throw new UnsupportedOperationException(method.getName());
				});

		return new Link(connection);
	}
}