package gate.sql;

import gate.error.AppError;
import gate.error.ConstraintViolationException;
import gate.error.DatabaseException;
import gate.io.StringReader;
import gate.producer.AppProducer;
import gate.sql.condition.CompiledCondition;
import gate.sql.delete.Delete;
import gate.sql.select.Select;
import gate.sql.statement.BasicInsertOperation;
import gate.sql.statement.DeleteOperation;
import gate.sql.statement.DeleteOperation.Criteria;
import gate.sql.statement.InsertOperation;
import gate.sql.statement.Operation;
import gate.sql.statement.Query;
import gate.sql.statement.SearchOperation;
import gate.sql.statement.SelectOperation;
import gate.sql.statement.Sentence;
import gate.sql.statement.UpdateOperation;
import gate.sql.update.Update;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.CDI;
import javax.sql.DataSource;

/**
 * Holds the connection to the database and provides a clean interface for
 * transaction management, querying and execution of statements.
 *
 * @author davins
 */
@Dependent
public class Link implements AutoCloseable
{

	private final Connection connection;

	public SearchCommand searchCommand(String string)
	{
		return new SearchCommand(this, string);
	}

	public SearchCommand searchCommand(String string, Object... parameters)
	{
		return new SearchCommand(this, string, parameters);
	}

	public SearchCommand searchCommand(URL resource, Object... parameters)
	{
		try
		{
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.openStream())))
			{
				try (StringWriter writer = new StringWriter())
				{
					for (int c = reader.read();
						c != -1; c = reader.read())
						writer.write((char) c);
					writer.flush();
					return new SearchCommand(this, writer.toString(), parameters);
				}
			}
		} catch (IOException ex)
		{
			throw new AppError(ex);
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
	 * Creates a Link connected to the current application database.
	 */
	public Link()
	{
		this(CDI.current()
			.select(AppProducer.class)
			.get().produce().getId());
	}

	/**
	 * Creates a Link for the specified JDBC connection.
	 *
	 * @param connection the JDBC connection to be associated with the new
	 * Link
	 */
	public Link(Connection connection)
	{
		this.connection = connection;
	}

	/**
	 * Creates a Link for the specified JDBC connection.
	 *
	 * @param datasource the DataSource from where to get the connection
	 */
	public Link(DataSource datasource)
	{
		try
		{
			this.connection = datasource.getConnection();
		} catch (SQLException ex)
		{
			throw new AppError(ex);
		}
	}

	/**
	 * Creates a Link for the specified database.
	 *
	 * @param driver driver to be used
	 * @param url URL where to connect
	 * @param username user to be used on connection
	 * @param password password to be used on connection
	 */
	public Link(String driver, String url, String username, String password)
	{
		try
		{
			Thread.currentThread().getContextClassLoader().loadClass(driver);
			this.connection = DriverManager.getConnection(url, username, password);
		} catch (SQLException | ClassNotFoundException ex)
		{
			throw new AppError(ex);
		}
	}

	/**
	 * Creates a Link for the specified JNDI data source.
	 *
	 * @param datasource name of the data source from where to get the JDBC
	 * connection
	 */
	public Link(String datasource)
	{
		try
		{
			connection = LinkSource.getNamedDataSource(datasource).getConnection();
		} catch (SQLException ex)
		{
			throw new DatabaseException(ex);
		}
	}

	/**
	 * Prepares a new Command for execution.
	 *
	 * @param sql SQL string to be executed
	 *
	 * @return the new Command created
	 */
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
	 *
	 * @return the new Command created
	 */
	public Command createCommand(URL resource)
	{
		try
		{
			return createCommand(StringReader.read(resource));
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
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
	 *
	 * @return a connected sentence to describe the execution parameters
	 */
	public Sentence.Connected prepare(URL resource)
	{
		try
		{
			return Sentence.of(StringReader.read(resource)).connect(this);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * Prepares a sentence to be executed.
	 *
	 * @param sql SQL string to be executed
	 *
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
	 *
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
	 *
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
	 * @param <T> type type of the entities to be compiled with the sentence
	 *
	 * @return a compiled and connected sentence ready for execution
	 */
	public <T> Sentence.Extractor.Compiled.Connected<T> prepare(Sentence.Extractor.Compiled<T> sentence)
	{
		return sentence.connect(this);
	}

	/**
	 * Prepares a sentence to be executed.
	 *
	 * @param builder the builder used to generate the sentence to be
	 * executed
	 *
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
	 * executed
	 *
	 * @return a compiled and connected sentence ready for execution
	 */
	public Sentence.Compiled.Connected prepare(Sentence.Compiled.Builder builder)
	{
		return builder.build().connect(this);
	}

	/**
	 * Prepares a sentence to be executed.
	 *
	 *
	 * @param builder the builder used to generate the sentence to be
	 * executed
	 * @param <T> type type of the entities to be compiled with the sentence
	 *
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
	 * @param <T> type type of the entities to be compiled with the sentence
	 *
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
	 * @param <T> type type of the entities to be compiled with the sentence
	 *
	 * @return a connected sentence to describe execution parameters
	 */
	public <T> Operation.Connected<T> prepare(Operation.Builder<T> operation)
	{
		return operation.build().connect(this);
	}

	/**
	 * Prepares a new query for execution.
	 * <p>
	 * The query returned will lack parameter values and must have them to
	 * be defined before it can be executed.
	 *
	 * @param query the SQL string to be executed after the definition of
	 * it's parameter values
	 *
	 * @return a connected query whose parameter values are yet to be
	 * defined
	 */
	public Query.Connected from(String query)
	{
		return Query.of(query).connect(this);
	}

	/**
	 * Prepares a new query for execution.
	 * <p>
	 * The query returned will lack parameter values and must have them to
	 * be defined before it can be executed.
	 *
	 * @param resource a resource containing the SQL string to be executed
	 * after the definition of it's parameter values
	 *
	 * @return a connected query whose parameter values are yet to be
	 * defined
	 */
	public Query.Connected from(URL resource)
	{
		try
		{
			return from(StringReader.read(resource));
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}

	}

	/**
	 * Prepares a new query for execution.
	 * <p>
	 * Each @ symbol found on the query will be replaced by it's respective
	 * format argument.
	 * <p>
	 * The query returned will lack parameter values and must have them to
	 * be defined before it can be executed.
	 *
	 * @param query the SQL string to be executed after the definition of
	 * it's parameter values
	 * @param args arguments referenced by the @ symbols in the SQL string
	 *
	 * @return a connected query formatted with the specified arguments
	 * whose parameter values are yet to be defined
	 */
	public Query.Connected from(String query, String... args)
	{
		return Query.of(Formatter.format(query, (Object[]) args)).connect(this);
	}

	/**
	 * Prepares a new query for execution.
	 * <p>
	 * Each @ symbol found on the query will be replaced by it's respective
	 * format argument.
	 * <p>
	 * The query returned will lack parameter values and must have them to
	 * be defined before execution.
	 *
	 * @param resource a resource containing the SQL string to be executed
	 * after the definition of it's parameter values
	 * @param args arguments referenced by the @ symbols in the SQL string
	 *
	 * @return a connected query formatted with the specified arguments
	 * whose parameter values are yet to be defined
	 */
	public Query.Connected from(URL resource, String... args)
	{
		try
		{
			return from(StringReader.read(resource), args);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * Prepares a new query for execution.
	 * <p>
	 * Each @ symbol found on the query will be replaced by it's respective
	 * condition sql string.
	 * <p>
	 * The query returned will be compiled with the parameters of the
	 * specified conditions.
	 *
	 * @param query the SQL string to be executed
	 * @param conditions the list of conditions that will replace @ symbols
	 * and provide the parameters to be compiled into the query
	 *
	 * @return a connected and compiled query ready for execution
	 */
	public Query.Compiled.Connected from(String query, CompiledCondition... conditions)
	{
		return Query.of(Formatter.format(query, (Object[]) conditions))
			.parameters(Stream.of(conditions)
				.flatMap(Clause::getParameters)
				.collect(Collectors.toList()))
			.connect(this);
	}

	/**
	 * Prepares a new query for execution.
	 * <p>
	 * Each @ symbol found on the query will be replaced by it's respective
	 * condition sql string.
	 * <p>
	 * The query returned will be compiled with the parameters of the
	 * specified conditions.
	 *
	 * @param resource a resource containing the SQL string to be executed
	 * @param conditions the list of conditions that will replace @ symbols
	 * and provide the parameters to be compiled into the query
	 *
	 * @return a connected and compiled query ready for execution
	 */
	public Query.Compiled.Connected from(URL resource, CompiledCondition... conditions)
	{
		try
		{
			return from(StringReader.read(resource), conditions);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * Prepares a new query for execution.
	 * <p>
	 * The query returned will lack parameter values and must have them to
	 * be defined before it can be executed.
	 *
	 * @param query the query object to be executed after the definition of
	 * it's parameter values
	 *
	 * @return a connected query whose parameter values are yet to be
	 * defined
	 */
	public Query.Connected from(Query query)
	{
		return query.connect(this);
	}

	/**
	 * Prepares a new query for execution.
	 * <p>
	 * The query returned will lack parameter values and must have them to
	 * be defined before it can be executed.
	 *
	 * @param query a query builder to generate the query to be executed
	 *
	 * @return a connected query whose parameter values are yet to be
	 * defined
	 */
	public Query.Connected from(Query.Builder query)
	{
		return query.build().connect(this);
	}

	/**
	 * Prepares a new query for execution.
	 *
	 * @param query the query object to be executed after the definition of
	 * it's parameter values
	 *
	 * @return a connected query ready for execution
	 */
	public Query.Constant.Connected from(Query.Constant query)
	{
		return query.connect(this);
	}

	/**
	 * Prepares a new query for execution.
	 *
	 * @param query the query object to be executed after the definition of
	 * it's parameter values
	 *
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
	 *
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
	 *
	 * @return a compiled and connected query ready for execution
	 */
	public Query.Compiled.Connected from(Query.Compiled.Builder query)
	{
		return query.build().connect(Link.this);
	}

	/**
	 * Selects a list of objects from the the database using GQN notation.
	 *
	 * @param type type of the objects to be selected
	 * @param <T> type type of the entities to be compiled with the sentence
	 *
	 * @return a SearchOperation object for definition of the properties and
	 * the criteria of selection
	 */
	public <T> SearchOperation<T> search(Class<T> type)
	{
		return (String... GQN) -> new SearchOperation.Matcher<T>()
		{
			@Override
			public List<T> matching(T filter)
			{
				return Select.of(type, filter, GQN)
					.connect(Link.this).fetchEntityList(type);
			}

			@Override
			public List<T> parameters(List<Object> parameters)
			{
				return Select.of(type, GQN)
					.parameters(parameters)
					.connect(Link.this).fetchEntityList(type);

			}
		};
	}

	/**
	 * Selects an object from the the database using GQN notation.
	 *
	 * @param type type of the object to be selected
	 * @param <T> type type of the entities to be compiled with the sentence
	 *
	 * @return a SelectOperation object for definition of the properties and
	 * the criteria of selection
	 */
	public <T> SelectOperation<T> select(Class<T> type)
	{
		return (String... GQN) -> new SelectOperation.Matcher<T>()
		{
			@Override
			public Optional<T> matching(T filter)
			{
				return Select.of(type, filter, GQN)
					.connect(Link.this).fetchEntity(type);
			}

			@Override
			public Optional<T> parameters(List<Object> parameters)
			{
				return Select.of(type, GQN)
					.parameters(parameters)
					.connect(Link.this).fetchEntity(type);
			}
		};
	}

	/**
	 * Inserts objects of the specified type on the database.
	 *
	 * @param type type of the objects to be inserted
	 * @param <T> type type of the entities to be compiled with the sentence
	 *
	 * @return an InsertOperation object for definition of the properties
	 * and the values to be inserted
	 */
	public <T> InsertOperation<T> insert(Class<T> type)
	{
		return new BasicInsertOperation<>(this, type);
	}

	/**
	 * Updates objects of the specified type from the database.
	 *
	 * @param type type of the objects to be updated
	 * @param <T> type type of the entities to be compiled with the sentence
	 *
	 * @return an UpdateOperation object for definition of the properties,
	 * update criteria and the values to be updated
	 */
	public <T> UpdateOperation<T> update(Class<T> type)
	{
		return new UpdateOperation<T>()
		{
			@Override
			public int execute(Collection<T> values) throws ConstraintViolationException
			{
				return Update.type(type).build().values(values).connect(Link.this).execute();
			}

			@Override
			public UpdateOperation.Properties<T> properties(String... GQN)
			{
				return e -> Update
					.of(type, GQN)
					.build()
					.values(e)
					.connect(Link.this)
					.execute();
			}
		};
	}

	/**
	 * Deletes objects of the specified type from the database.
	 *
	 * @param type type of the objects to be deleted
	 * @param <T> type type of the entities to be compiled with the sentence
	 *
	 * @return a DeleteOperation object for definition of the deletion
	 * criteria and the values to be deleted
	 */
	public <T> DeleteOperation<T> delete(Class<T> type)
	{
		return new DeleteOperation<T>()
		{
			@Override
			public int execute(Collection<T> values) throws ConstraintViolationException
			{
				return Delete.from(type).build().values(values).connect(Link.this).execute();
			}

			@Override
			public Criteria<T> criteria(String... GQN)
			{
				return e -> Delete.of(type, GQN).values(e).connect(Link.this).execute();
			}
		};
	}
}
