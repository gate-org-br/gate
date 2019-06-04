package gate.sql.statement;

import gate.sql.Command;
import gate.sql.Fetchable;
import gate.sql.Link;
import gate.sql.SQLBuilder;
import gate.sql.fetcher.Fetcher;
import java.util.Arrays;
import java.util.List;

/**
 * A SQL query not linked to a database and whose parameters are not defined.
 * <p>
 * A query must be linked to a database and compiled with a set of parameters before execution
 *
 * @author Davi Nunes da Silva
 */
public interface Query extends SQL, Compilable
{

	/**
	 * Creates a new query.
	 *
	 * @param sql the SQL string of the query
	 *
	 * @return the new query created
	 */
	static Query of(String sql)
	{
		return new BasicQuery(sql);
	}

	/**
	 * Binds the query to a link
	 *
	 * @param link link to be bound to the query
	 *
	 * @return the same query bound to the specified link
	 */
	Connected connect(Link link);

	@Override
	Compiled parameters(List<Object> parameters);

	@Override
	Compiled parameters(Object... parameters);

	@Override
	Constant constant();

	@Override
	Query print();

	@Override
	String toString();

	/**
	 * A SQL query compiled with a set of parameters and not linked to a database.
	 * <p>
	 * A compiled query must be linked to a database before execution
	 *
	 * @author Davi Nunes da Silva
	 */
	interface Compiled extends SQL
	{

		/**
		 * Binds the query to a connection
		 *
		 * @param connection connection to be bound to the query
		 *
		 * @return the same query bound to the specified connection
		 */
		Connected connect(Link connection);

		@Override
		Compiled print();

		@Override
		String toString();

		/**
		 * A SQL query linked to a database and compiled with a set of parameters.
		 * <p>
		 * A Compiled and Connected Query is ready for execution
		 *
		 * @author Davi Nunes da Silva
		 */
		interface Connected extends SQL, Fetchable
		{

			/**
			 * Creates a command for the query.
			 *
			 * @return the new command created
			 */
			Command createCommand();

			@Override
			<T> T fetch(Fetcher<T> handler);

			@Override
			Connected print();

			@Override
			String toString();
		}

		/**
		 * Compiled query builder.
		 */
		interface Builder extends SQLBuilder<Query.Compiled>
		{

			/**
			 * Creates a new compiled query.
			 *
			 * @return the compile query created
			 */
			@Override
			Query.Compiled build();
		}
	}

	/**
	 * A SQL query without parameters and not linked to a database.
	 * <p>
	 * A constant query must be linked to a database before execution
	 *
	 * @author Davi Nunes da Silva
	 */
	interface Constant extends SQL
	{

		/**
		 * Binds the query to a connection
		 *
		 * @param connection connection to be bound to the query
		 *
		 * @return the same query bound to the specified connection
		 */
		Connected connect(Link connection);

		@Override
		Constant print();

		@Override
		String toString();

		/**
		 * A SQL query without parameters and linked to a database.
		 * <p>
		 * A Constant and Connected Query is ready for execution
		 *
		 * @author Davi Nunes da Silva
		 */
		interface Connected extends SQL, Fetchable
		{

			/**
			 * Creates a command for the query.
			 *
			 * @return the new command created
			 */
			Command createCommand();

			@Override
			<T> T fetch(Fetcher<T> handler);

			@Override
			Connected print();

			@Override
			String toString();
		}

		/**
		 * Compiled query builder.
		 */
		interface Builder extends SQLBuilder<Query.Constant>
		{

			/**
			 * Creates a new compiled query.
			 *
			 * @return the compile query created
			 */
			@Override
			Query.Constant build();
		}
	}

	/**
	 * A SQL query linked to a database whose parameters are not defined.
	 * <p>
	 * A connected query must be compiled with a set of parameters before execution
	 */
	interface Connected extends SQL, Compilable
	{

		/**
		 * Compiles the query with a list of parameters making it ready for execution.
		 *
		 * @param parameters the list of parameters to be compiled with the query
		 *
		 * @return the same query compiled with the specified parameters
		 */
		@Override
		Compiled parameters(List<Object> parameters);

		/**
		 * Compiles the query with a list of parameters making it ready for execution.
		 *
		 * @param parameters the list of parameters to be compiled with the query
		 *
		 * @return the same query compiled with the specified parameters and ready for execution
		 */
		@Override
		default Compiled parameters(Object... parameters)
		{
			return Connected.this.parameters(Arrays.asList(parameters));
		}

		@Override
		Constant constant();

		@Override
		Connected print();

		@Override
		String toString();

		/**
		 * A SQL query linked to a database and compiled with a set or parameters.
		 * <p>
		 * A connected and compiled query is ready for execution
		 */
		interface Compiled extends SQL, Fetchable
		{

			/**
			 * Creates a command for the query.
			 *
			 * @return the new command created
			 */
			Command createCommand();

			@Override
			<T> T fetch(Fetcher<T> handler);

			@Override
			Compiled print();

			@Override
			String toString();
		}

		/**
		 * A SQL query without parameters and linked to a database.
		 * <p>
		 * A connected and constant query is ready for execution
		 */
		interface Constant extends SQL, Fetchable
		{

			/**
			 * Creates a command for the query.
			 *
			 * @return the new command created
			 */
			Command createCommand();

			@Override
			<T> T fetch(Fetcher<T> handler);

			@Override
			Constant print();

			@Override
			String toString();
		}
	}

	/**
	 * Query builder.
	 */
	interface Builder extends SQLBuilder<Query>
	{

		/**
		 * Creates a new query.
		 *
		 * @return the query created
		 */
		@Override
		Query build();
	}
}
