package gate.sql.statement;

import gate.error.ConstraintViolationException;
import gate.lang.property.Property;
import gate.sql.Link;
import gate.sql.SQLBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A database operation associated with a java type and a list of properties.
 * <p>
 * An operation must be connected to a database and compiled with the list of
 * java objects to be operated on before execution.
 *
 *
 *
 * @author Davi Nunes da Silva
 */
public interface Operation<T> extends SQL
{

	/**
	 * Creates a new operation for the specified SQL string, java type and a
	 * list of properties.
	 *
	 *
	 * @param type java type to be operated on
	 * @param sql the SQL string of the operation
	 * @param properties list of properties associated with the operation to
	 * be created
	 *
	 * @return a new operation for the specified SQL string, java type and
	 * list of properties
	 */
	static <T> Operation<T> of(Class<T> type, List<Property> properties, String sql)
	{
		return new BasicOperation<>(type, properties, sql);
	}

	/**
	 * Connects the operation to a database.
	 *
	 * @param link the database link to be associated with the operation
	 *
	 * @return the same operation connected with the specified database
	 */
	Connected<T> connect(Link link);

	/**
	 * Defines the java object to be operated on.
	 *
	 * @param value the java object to be operated on
	 *
	 * @return the same operation compiled with the specified java object
	 */
	Compiled<T> value(T value);

	/**
	 * Defines the list of java objects to be operated on.
	 *
	 * @param values the list of java objects to be operated on
	 *
	 * @return the same operation compiled with the specified list of java
	 * objects
	 */
	Compiled<T> values(Collection<T> values);

	/**
	 * Defines the list of java objects to be operated on.
	 *
	 * @param values the list of java objects to be operated on
	 *
	 * @return the same operation compiled with the specified list of java
	 * objects
	 */
	default Compiled<T> values(T... values)
	{
		return values(Arrays.asList(values));
	}

	@Override
	Operation print();

	/**
	 * An operation compiled with the list of java objects to be operated on
	 * but not yet connected to a database.
	 * <p>
	 * An compiled operation must be connected to a database before
	 * execution.
	 *
	 *
	 */
	interface Compiled<T> extends SQL
	{

		/**
		 * Connects the operation to a database.
		 *
		 * @param link the database link to be associated with the
		 * operation
		 *
		 * @return the same operation connected with the specified
		 * database
		 */
		Connected<T> connect(Link link);

		@Override
		Compiled<T> print();

		/**
		 * An operation compiled with the list of java objects to be
		 * operated on and connected to a database.
		 * <p>
		 * An compiled and connected operation is ready for execution.
		 *
		 *
		 */
		interface Connected<T> extends SQL
		{

			/**
			 * Executes the operation on the specified database with
			 * the specified java objects.
			 *
			 * @return the number of records affected by the
			 * operation
			 *
			 * @throws gate.error.ConstraintViolationException if
			 * any database constraint is violated during execution
			 */
			int execute() throws ConstraintViolationException;

			default <T extends Exception> void orElseThrow(Supplier<T> supplier)
				throws ConstraintViolationException, T
			{
				if (execute() == 0)
					throw supplier.get();
			}

			Connected<T> observe(Consumer<T> observer) throws ConstraintViolationException;

			@Override
			Connected<T> print();

		}

		interface Builder extends SQLBuilder<Operation.Compiled>
		{

		}
	}

	/**
	 * A operation connected to a database, but lacking definition of the
	 * objects to be operated on.
	 * <p>
	 * An connected operation must be compiled with the objects to be
	 * operated on before it can be executed.
	 *
	 *
	 */
	interface Connected<T> extends SQL
	{

		/**
		 * Defines the java object to be operated on.
		 *
		 * @param value the java object to be operated on
		 *
		 * @return the same operation compiled with the specified java
		 * object
		 */
		Compiled<T> value(T value);

		/**
		 * Defines the list of java objects to be operated on.
		 *
		 * @param values the list of java objects to be operated on
		 *
		 * @return the same operation compiled with the specified list
		 * of java objects
		 */
		Compiled<T> values(Collection<T> values);

		/**
		 * Defines the list of java objects to be operated on.
		 *
		 * @param values the list of java objects to be operated on
		 *
		 * @return the same operation compiled with the specified list
		 * of java objects
		 */
		default Compiled<T> values(T... values)
		{
			return values(Arrays.asList(values));
		}

		@Override
		Connected<T> print();

		/**
		 * An operation compiled with the list of java objects to be
		 * operated on and connected to a database.
		 * <p>
		 * An compiled and connected operation is ready for execution.
		 *
		 *
		 */
		interface Compiled<T> extends SQL
		{

			/**
			 * Executes the operation on the specified database with
			 * the specified java objects.
			 *
			 * @return the number of records affected by the
			 * operation
			 *
			 * @throws gate.error.ConstraintViolationException if
			 * any database constraint is violated during execution
			 */
			int execute() throws ConstraintViolationException;

			default <T extends Exception> void orElseThrow(Supplier<T> supplier)
				throws ConstraintViolationException, T
			{
				if (execute() == 0)
					throw supplier.get();
			}

			Compiled<T> observe(Consumer<T> observer) throws ConstraintViolationException;

			@Override
			Compiled<T> print();
		}
	}

	interface Builder<T> extends SQLBuilder<Operation<T>>
	{

	}
}
