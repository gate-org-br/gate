package gate.sql;

import gate.error.AppError;
import gate.error.DatabaseException;
import gate.io.StringReader;
import gate.producer.AppProducer;
import gate.sql.condition.CompiledCondition;
import gate.sql.statement.Operation;
import gate.sql.statement.Query;
import gate.sql.statement.Sentence;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.literal.NamedLiteral;
import jakarta.enterprise.inject.spi.CDI;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Holds the connection to the database and provides a clean interface for
 * transaction management, querying and execution of statements.
 *
 * @author davins
 */
public class Link implements AutoCloseable
{

	private final Connection connection;

	/**
	 * Creates a Link for the specified JDBC connection.
	 *
	 * @param connection the JDBC connection to be associated with the new
	 *                   Link
	 */
	private Link(Connection connection)
	{
		this.connection = connection;
	}

	/**
	 * Creates a Link connected to the current application database.
	 *
	 * @return a new link connected to the default data source
	 */
	public static Link of()
	{

		try
		{
			Instance<DataSource> ds
					= CDI.current().select(DataSource.class);
			if (ds.isResolvable())
				return new Link(ds.get().getConnection());

			var datasource = CDI.current()
					.select(AppProducer.class).get().produce().getId();

			try
			{
				return Link.of((DataSource) InitialContext.doLookup("java:/comp/env/" + datasource));
			} catch (NamingException e1)
			{
				throw new IllegalArgumentException("Data source not found: " + datasource);
			}
		} catch (SQLException ex)
		{
			throw new DatabaseException(ex);
		}
	}

	/**
	 * Creates a Link for the specified JDBC connection.
	 *
	 * @param datasource the DataSource from where to get the connection
	 * @return a new link connected to the specified data source
	 */
	public static Link of(DataSource datasource)
	{
		try
		{
			return new Link(datasource.getConnection());
		} catch (SQLException ex)
		{
			throw new AppError(ex);
		}
	}

	/**
	 * Creates a Link for the specified database.
	 *
	 * @param driver   driver to be used
	 * @param url      URL where to connect
	 * @param username user to be used on connection
	 * @param password password to be used on connection
	 * @return a new link connected to the specified database
	 */
	public static Link of(String driver, String url, String username, String password)
	{
		try
		{
			Thread.currentThread().getContextClassLoader().loadClass(driver);
			return new Link(DriverManager.getConnection(url, username, password));
		} catch (SQLException | ClassNotFoundException ex)
		{
			throw new AppError(ex);
		}
	}

	/**
	 * Creates a Link for the specified JNDI data source.
	 *
	 * @param datasource name of the data source from where to get the JDBC
	 *                   connection
	 * @return a new link connected to the specified data source
	 */
	public static Link of(String datasource)
	{
		try
		{
			Instance<DataSource> ds
					= CDI.current().select(DataSource.class,
					NamedLiteral.of(datasource));
			if (ds.isResolvable())
				return new Link(ds.get().getConnection());

			return Link.of((DataSource) InitialContext.doLookup("java:/comp/env/" + datasource));
		} catch (SQLException ex)
		{
			throw new DatabaseException(ex);
		} catch (NamingException e1)
		{
			throw new IllegalArgumentException("Data source not found: " + datasource);
		}
	}

	/**
	 * Returns the JDBC connection associated with this link.
	 *
	 * @return the JDBC connection associated with this link
	 */
	public Connection getConnection()
	{
		return connection;
	}

	/**
	 * Returns the JDBC database product name associated with this link.
	 *
	 * <p>The returned value is normalized to uppercase so it can be used safely in database-specific comparisons and
	 * switch expressions.
	 *
	 * @return the uppercase JDBC database product name for the current connection
	 */
	public String getDatabase()
	{
		try
		{
			return connection.getMetaData()
					.getDatabaseProductName().toUpperCase(Locale.ROOT);
		} catch (SQLException e)
		{
			throw new DatabaseException(e);
		}
	}

	/**
	 * Prepares a new Command for execution.
	 *
	 * @param sql SQL string to be executed
	 * @return the new Command created
	 */
	@SuppressWarnings("SqlSourceToSinkFlow")
	public Command createCommand(String sql)
	{
		try
		{
			return new Command(this,
					connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS));
		} catch (SQLException e)
		{
			throw new AppError(e);
		}
	}

	/**
	 * Prepares a new Command for execution.
	 *
	 * @param resource a resource containing the SQL string to be executed
	 * @return the new Command created
	 */
	public Command createCommand(URL resource)
	{
		return createCommand(StringReader.read(resource));
	}

	/**
	 * Starts a new transaction.
	 *
	 * @return this, for chained invocations
	 */
	public Link beginTran()
	{
		try
		{
			if (connection.getAutoCommit())
				connection.setAutoCommit(false);
			return this;
		} catch (SQLException e)
		{
			throw new AppError(e);
		}
	}

	/**
	 * Checks for a pending transaction.
	 *
	 * @return true if there is a pending transaction and false otherwise
	 */
	public boolean isInTran()
	{

		try
		{
			return !connection.getAutoCommit();
		} catch (SQLException e)
		{
			throw new AppError(e);
		}
	}

	/**
	 * Commits the current transaction.
	 *
	 * @return this, for chained invocations
	 */
	public Link commit()
	{
		try
		{
			if (!connection.getAutoCommit())
			{
				connection.commit();
				connection.setAutoCommit(true);
			}
			return this;
		} catch (SQLException e)
		{
			throw new AppError(e);
		}
	}

	/**
	 * Rollback the current transaction.
	 *
	 * @return this, for chained invocations
	 */
	public Link rollback()
	{
		try
		{
			if (!connection.getAutoCommit())
			{
				connection.rollback();
				connection.setAutoCommit(true);
			}
			return this;
		} catch (SQLException e)
		{
			throw new AppError(e);
		}
	}

	/**
	 * Check if the connection is closed.
	 *
	 * @return true if the connection is closed and false otherwise
	 */
	public boolean isClosed()
	{
		try
		{
			return connection.isClosed();
		} catch (SQLException e)
		{
			throw new AppError(e);
		}
	}

	/**
	 * Rollback any pending transaction and closes the connection.
	 */
	@Override
	@SuppressWarnings("resource")
	public void close()
	{
		try
		{
			if (!connection.isClosed())
			{
				try (connection)
				{
					if (isInTran())
						rollback();
				}
			}
		} catch (SQLException ex)
		{
			throw new AppError(ex);
		}
	}

	/**
	 * Prepares a new sentence for execution.
	 *
	 * @param resource a resource containing the SQL string to be executed
	 * @return a connected sentence to describe the execution parameters
	 */
	public Sentence.Connected prepare(URL resource)
	{
		return Sentence.of(StringReader.read(resource)).connect(this);
	}

	/**
	 * Prepares a sentence to be executed.
	 *
	 * @param sql SQL string to be executed
	 * @return a connected sentence to describe execution parameters
	 */
	public Sentence.Connected prepare(String sql)
	{
		return Sentence.of(sql).connect(this);
	}

	/**
	 * Prepares a sentence to be executed.
	 *
	 * @param sentence the sentence to be executed
	 * @return a connected sentence to describe execution parameters
	 */
	public Sentence.Connected prepare(Sentence sentence)
	{
		return sentence.connect(this);
	}

	/**
	 * Prepares a sentence to be executed.
	 *
	 * @param sentence the sentence to be executed
	 * @return a compiled and connected sentence ready for execution
	 */
	public Sentence.Compiled.Connected prepare(Sentence.Compiled sentence)
	{
		return sentence.connect(this);
	}

	/**
	 * Prepares a sentence to be executed.
	 *
	 * @param sentence the sentence to be executed
	 * @param <T>      type of the entities to be compiled with the sentence
	 * @return a compiled and connected sentence ready for execution
	 */
	public <T> Sentence.Extractor.Compiled.Connected<T> prepare(Sentence.Extractor.Compiled<T> sentence) {return sentence.connect(this);}

	/**
	 * Prepares a sentence to be executed.
	 *
	 * @param builder the builder used to generate the sentence to be executed
	 * @return a connected sentence to describe execution parameters
	 */
	public Sentence.Connected prepare(Sentence.Builder builder)
	{
		return builder.build().connect(this);
	}

	/**
	 * Prepares a sentence to be executed.
	 *
	 * @param builder the builder used to generate the sentence to be
	 *                executed
	 * @return a compiled and connected sentence ready for execution
	 */
	public Sentence.Compiled.Connected prepare(Sentence.Compiled.Builder builder)
	{
		return builder.build().connect(this);
	}

	/**
	 * Prepares a sentence to be executed.
	 *
	 * @param builder the builder used to generate the sentence to be executed
	 * @param <T>     type of the entities to be compiled with the sentence
	 * @return a compiled and connected sentence ready for execution
	 */
	public <T> Sentence.Extractor.Compiled.Connected<T> prepare(Sentence.Extractor.Compiled.Builder<T> builder)
	{
		return builder.build().connect(this);
	}

	/**
	 * Prepares an operation to be executed.
	 *
	 * @param operation the operation to be executed
	 * @param <T>       type of the entities to be compiled with the sentence
	 * @return a connected sentence to describe execution parameters
	 */
	public <T> Operation.Connected<T> prepare(Operation<T> operation)
	{
		return operation.connect(this);
	}

	/**
	 * Prepares an operation to be executed.
	 *
	 * @param operation the operation to be executed
	 * @param <T>       type of the entities to be compiled with the sentence
	 * @return a connected sentence to describe execution parameters
	 */
	public <T> Operation.Connected<T> prepare(Operation.Builder<T> operation)
	{
		return operation.build().connect(this);
	}

	public Query.Connected from(String query)
	{
		return Query.of(query).connect(this);
	}

	public Query.Connected from(URL resource)
	{
		return from(StringReader.read(resource));
	}

	public Query.Connected from(String query, String... args)
	{
		return Query.of(Formatter.sql(query, (Object[]) args)).connect(this);
	}

	public Query.Connected from(URL resource, String... args)
	{
		return from(StringReader.read(resource), args);
	}

	public Query.Compiled.Connected from(String query, List<Object> parameters)
	{
		return Query.of(query, parameters).connect(this);
	}

	public Query.Compiled.Connected from(URL resource, List<Object> parameters)
	{
		return from(StringReader.read(resource), parameters);
	}

	/**
	 * Prepares a new query for execution.
	 * <p>
	 * Each @ symbol found on the query will be replaced by its respective
	 * condition sql string.
	 * <p>
	 * The query returned will be compiled with the parameters of the
	 * specified conditions.
	 *
	 * @param query      the SQL string to be executed
	 * @param conditions the list of conditions that will replace @ symbols
	 *                   and provide the parameters to be compiled into the query
	 * @return a connected and compiled query ready for execution
	 */
	public Query.Compiled.Connected from(String query, CompiledCondition... conditions)
	{
		return Query.of(Formatter.sql(query, (Object[]) conditions)).parameters(
						Stream.of(conditions).flatMap(Clause::getParameters).collect(Collectors.toList()))
				.connect(this);
	}

	/**
	 * Prepares a new query for execution.
	 * <p>
	 * Each @ symbol found on the query will be replaced by its respective
	 * condition sql string.
	 * <p>
	 * The query returned will be compiled with the parameters of the
	 * specified conditions.
	 *
	 * @param resource   a resource containing the SQL string to be executed
	 * @param conditions the list of conditions that will replace @ symbols
	 *                   and provide the parameters to be compiled into the query
	 * @return a connected and compiled query ready for execution
	 */
	public Query.Compiled.Connected from(URL resource, CompiledCondition... conditions)
	{
		return from(StringReader.read(resource), conditions);
	}

	public Query.Connected from(Query query)
	{
		return query.connect(this);
	}

	public Query.Connected from(Query.Builder query)
	{
		return query.build().connect(this);
	}

	public Query.Constant.Connected from(Query.Constant query)
	{
		return query.connect(this);
	}

	/**
	 * Prepares a new query for execution.
	 *
	 * @param query the query object to be executed after the definition of
	 *              its parameter values
	 * @return a connected query ready for execution
	 */
	public Query.Constant.Connected from(Query.Constant.Builder query)
	{
		return query.build().connect(this);
	}

	/**
	 * Prepares a new query for execution.
	 *
	 * @param query the query object to be executed
	 * @return a compiled and connected query ready for execution
	 */
	public Query.Compiled.Connected from(Query.Compiled query)
	{
		return query.connect(Link.this);
	}

	/**
	 * Prepares a new query for execution.
	 *
	 * @param query a query builder to generate the query to be executed
	 * @return a compiled and connected query ready for execution
	 */
	public Query.Compiled.Connected from(Query.Compiled.Builder query)
	{
		return query.build().connect(Link.this);
	}

}