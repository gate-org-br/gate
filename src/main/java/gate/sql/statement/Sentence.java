package gate.sql.statement;

import gate.error.ConstraintViolationException;
import gate.sql.Executable;
import gate.sql.Link;
import gate.sql.SQLBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A SQL sentence not linked to a database and whose parameters are not defined.
 * <p>
 * A sentence must be linked to a database and compiled with a set of parameters before execution.
 *
 * @author Davi Nunes da Silva
 */
public interface Sentence extends SQL
{

	/**
	 * Creates a new sentence for the specified SQL string.
	 *
	 * @param sql the SQL string of the sentence to be created
	 *
	 * @return a new sentence for the SQL string specified
	 */
	public static Sentence of(String sql)
	{
		return new BasicSentence(sql);
	}

	/**
	 * Binds the sentence to a database link.
	 *
	 * @param link database link to be bound to the sentence
	 *
	 * @return the same sentence bound to the specified database link
	 */
	public Connected connect(Link link);

	/**
	 * Compiles the sentence with a list of parameters.
	 *
	 * @param parameters the list of parameters to be compiled with the sentence
	 *
	 * @return the same sentence compiled with the specified parameters
	 */
	public Compiled parameters(List<Object> parameters);

	/**
	 * Compiles the sentence with a list of parameters.
	 *
	 * @param parameters the list of parameters to be compiled with the sentence
	 *
	 * @return the same sentence compiled with the specified parameters
	 */
	default Compiled parameters(Object... parameters)
	{
		return parameters(Arrays.asList(parameters));
	}

	/**
	 * Prepares the sentence to be compiled with a list of entities and functions.
	 *
	 * @param <T> type of the entities to be compiled with the sentence
	 * @param entities the list of entities whose attributes will be updated
	 *
	 * @return the same sentence prepared to be compiled with the specified entities and a list of functions
	 */
	public <T> Prepared<T> entities(Collection<T> entities);

	/**
	 * Prepares the sentence to be compiled with a list of entities and functions.
	 *
	 * @param <T> type of the entities to be compiled with the sentence
	 * @param entities the list of entities whose attributes will be updated
	 *
	 * @return the same sentence prepared to be compiled with the specified entities and a list of functions
	 */
	default <T> Prepared<T> entities(T... entities)
	{
		return entities(Arrays.asList(entities));
	}

	/**
	 * Compiles the sentence with a batch of parameters.
	 *
	 * @param batch the batch of parameters to be compiled with the sentence
	 *
	 * @return the same sentence compiled with the specified batch of parameters
	 */
	public Compiled batch(List<List<Object>> batch);

	@Override
	public Sentence print();

	/**
	 * A SQL sentence compiled with a set of parameters and not linked to a database.
	 * <p>
	 * A compiled sentence must be linked to a database before execution.
	 */
	public interface Compiled extends SQL
	{

		/**
		 * Binds the sentence to a database link.
		 *
		 * @param link database link to be bound to the sentence
		 *
		 * @return the same sentence bound to the specified database link
		 */
		public Connected connect(Link link);

		/**
		 * A SQL sentence linked to a database and compiled with a set of parameters.
		 * <p>
		 * A compiled and connected sentence is ready for execution
		 */
		public interface Connected extends SQL, Executable
		{

			/**
			 * Executes the sentence on the database with the specified parameters and fetches generated keys as objects of the specified type.
			 *
			 * @param <K> type of the keys to be fetched
			 * @param type type of the keys to be fetched
			 * @param consumer the consumer to be used to process the fetched keys
			 *
			 * @throws gate.error.ConstraintViolationException if any database constraint is violated during sentence execution
			 */
			public <K> void fetchGeneratedKeys(Class<K> type, Consumer<K> consumer) throws ConstraintViolationException;

			/**
			 * Executes the sentence on the database with the specified parameters and fetches generated keys as objects of the specified type.
			 *
			 * @param <K> type of the keys to be fetched
			 * @param type type of the keys to be fetched
			 * @param consumer the consumer to be used to process the fetched keys
			 *
			 * @throws gate.error.ConstraintViolationException if any database constraint is violated during sentence execution
			 */
			public <K> void fetchGeneratedKeyLists(Class<K> type, Consumer<List<K>> consumer) throws ConstraintViolationException;

			/**
			 * Defines a Consumer to be called each time a set or parameters is processed by the statement.
			 * <p>
			 * Very useful for tracking the progress of long batch operations
			 *
			 * @param consumer the consumer to be called each time the statement is executed. Only a single consumer can be defined at any time. If
			 * null, removes any previously defined consumer.
			 *
			 * @return the current sentence, for chained invocations
			 */
			public Connected observe(Consumer<List<Object>> consumer);

			@Override
			public Connected print();

		}

		@Override
		public Compiled print();

		/**
		 * Compiled sentence builder.
		 */
		interface Builder extends SQLBuilder<Sentence.Compiled>
		{

		}
	}

	public interface Prepared<T> extends SQL
	{

		/**
		 * Specify the functions to be used to extract the attributes to be updated on the database.
		 *
		 * @param extractors list of functions to be used to extract the attributes to be updated on the database
		 *
		 * @return the same sentence compiled with the specified functions
		 */
		public Compiled<T> parameters(List<Function<T, ?>> extractors);

		/**
		 * Specify the functions to be used to extract the attributes to be updated on the database.
		 *
		 * @param extractors list of functions to be used to extract the attributes to be updated on the database
		 *
		 * @return the same sentence compiled with the specified functions
		 */
		default Compiled<T> parameters(Function<T, ?>... extractors)
		{
			return Prepared.this.parameters(Arrays.asList(extractors));
		}

		@Override
		public Prepared<T> print();

		/**
		 * A SQL sentence linked to a database and compiled with a set or parameters.
		 * <p>
		 * A connected and compiled sentence is ready for execution
		 *
		 * @param <T> type of the parameters compiled with sentence
		 */
		public interface Compiled<T> extends SQL
		{

			/**
			 * Binds the sentence to a database link.
			 *
			 * @param link database link to be bound to the sentence
			 *
			 * @return the same sentence bound to the specified database link
			 */
			public Connected<T> connect(Link link);

			/**
			 * A SQL sentence linked to a database and compiled with a set of parameters.
			 * <p>
			 * A compiled and connected sentence is ready for execution
			 *
			 * @param <T> type of the parameters compiled with sentence
			 */
			public interface Connected<T> extends SQL, Executable
			{

				/**
				 * Executes the sentence on the database with the specified parameters and fetches generated keys as objects of the specified
				 * type.
				 *
				 * @param <K> type of the keys to be fetched
				 * @param type type of the keys to be fetched
				 * @param consumer the consumer to be used to process the fetched keys
				 *
				 * @throws gate.error.ConstraintViolationException if any database constraint is violated during sentence execution
				 */
				public <K> void fetchGeneratedKeys(Class<K> type, BiConsumer<T, K> consumer) throws ConstraintViolationException;

				/**
				 * Executes the sentence on the database with the specified parameters and fetches generated keys as objects of the specified
				 * type.
				 *
				 * @param <K> type of the keys to be fetched
				 * @param type type of the keys to be fetched
				 * @param consumer the consumer to be used to process the fetched keys
				 *
				 * @throws gate.error.ConstraintViolationException if any database constraint is violated during sentence execution
				 */
				public <K> void fetchGeneratedKeyLists(Class<K> type, BiConsumer<T, List<K>> consumer) throws ConstraintViolationException;

				/**
				 * Defines a Consumer to be called each time a set or parameters is processed by the statement.
				 * <p>
				 * Very useful for tracking the progress of long batch operations
				 *
				 * @param consumer the consumer to be called each time the statement is executed. Only a single consumer can be defined at any
				 * time. If null, removes any previously defined consumer.
				 *
				 * @return the current sentence, for chained invocations
				 */
				public Connected<T> observe(Consumer<T> consumer);

				@Override
				public Connected<T> print();

			}

			/**
			 * Compiled sentence builder.
			 *
			 * @param <T> type of the parameters compiled with sentence
			 */
			interface Builder<T> extends SQLBuilder<Sentence.Prepared.Compiled<T>>
			{

			}
		}
	}

	/**
	 * A SQL sentence linked to a database whose parameters are not defined.
	 * <p>
	 * A connected sentence must be compiled with a set of parameters before execution
	 */
	public interface Connected extends SQL, Executable
	{

		/**
		 * Compiles the sentence with a list of parameters.
		 *
		 * @param parameters the list of parameters to be compiled with the sentence
		 *
		 * @return the same sentence compiled with the specified parameters
		 */
		public Compiled parameters(List<Object> parameters);

		/**
		 * Compiles the sentence with a list of parameters.
		 *
		 * @param parameters the list of parameters to be compiled with the sentence
		 *
		 * @return the same sentence compiled with the specified parameters
		 */
		default Compiled parameters(Object... parameters)
		{
			return parameters(Arrays.asList(parameters));
		}

		/**
		 * Compiles the sentence with a batch of parameters.
		 *
		 * @param batch the batch of parameters to be compiled with the sentence
		 *
		 * @return the same sentence compiled with the specified batch of parameters
		 */
		public Compiled batch(List<List<? extends Object>> batch);

		/**
		 * Prepares the sentence to be compiled with a list of entities and functions.
		 *
		 * @param <T> type of the entities to be compiled with the sentence
		 * @param entities the list of entities whose attributes will be updated
		 *
		 * @return the same sentence prepared to be compiled with the specified entities and a list of functions
		 */
		public <T> Prepared<T> entities(List<T> entities);

		/**
		 * Prepares the sentence to be compiled with a list of entities and functions.
		 *
		 * @param <T> type of the entities to be compiled with the sentence
		 * @param entities the list of entities whose attributes will be updated
		 *
		 * @return the same sentence prepared to be compiled with the specified entities and a list of functions
		 */
		default <T> Prepared<T> entities(T... entities)
		{
			return entities(Arrays.asList(entities));
		}

		/**
		 * Executes the sentence on the database with the specified parameters and fetches generated keys as objects of the specified type.
		 *
		 * @param <K> type of the keys to be fetched
		 * @param type type of the keys to be fetched
		 * @param consumer the consumer to be used to process the fetched keys
		 *
		 * @throws gate.error.ConstraintViolationException if any database constraint is violated during sentence execution
		 */
		public <K> void fetchGeneratedKeys(Class<K> type, Consumer<K> consumer) throws ConstraintViolationException;

		/**
		 * Executes the sentence on the database with the specified parameters and fetches generated keys as objects of the specified type.
		 *
		 * @param <K> type of the keys to be fetched
		 * @param type type of the keys to be fetched
		 * @param consumer the consumer to be used to process the fetched keys
		 *
		 * @throws gate.error.ConstraintViolationException if any database constraint is violated during sentence execution
		 */
		public <K> void fetchGeneratedKeyLists(Class<K> type, Consumer<List<K>> consumer) throws ConstraintViolationException;

		@Override
		public Connected print();

		/**
		 * A SQL sentence linked to a database and compiled with a set or parameters.
		 * <p>
		 * A connected and compiled sentence is ready for execution
		 */
		public interface Compiled extends SQL, Executable
		{

			/**
			 * Executes the sentence on the database with the specified parameters and fetches generated keys as objects of the specified type.
			 *
			 * @param <K> type of the keys to be fetched
			 * @param type type of the keys to be fetched
			 * @param consumer the consumer to be used to process the fetched keys
			 *
			 * @throws gate.error.ConstraintViolationException if any database constraint is violated during sentence execution
			 */
			public <K> void fetchGeneratedKeys(Class<K> type, Consumer<K> consumer) throws ConstraintViolationException;

			/**
			 * Executes the sentence on the database with the specified parameters and fetches generated keys as objects of the specified type.
			 *
			 * @param <K> type of the keys to be fetched
			 * @param type type of the keys to be fetched
			 * @param consumer the consumer to be used to process the fetched keys
			 *
			 * @throws gate.error.ConstraintViolationException if any database constraint is violated during sentence execution
			 */
			public <K> void fetchGeneratedKeyLists(Class<K> type, Consumer<List<K>> consumer) throws ConstraintViolationException;

			/**
			 * Defines a Consumer to be called each time a set or parameters is processed by the statement.
			 * <p>
			 * Very useful for tracking the progress of long batch operations
			 *
			 * @param consumer the consumer to be called each time the statement is executed. Only a single consumer can be defined at any time. If
			 * null, removes any previously defined consumer.
			 *
			 * @return the current sentence, for chained invocations
			 */
			public Compiled observe(Consumer<List<? extends Object>> consumer);

			@Override
			public Compiled print();
		}

		public interface Prepared<T> extends SQL
		{

			/**
			 * Specify the functions to be used to extract the attributes to be updated on the database.
			 *
			 * @param extractors list of functions to be used to extract the attributes to be updated on the database
			 *
			 * @return the same sentence compiled with the specified functions
			 */
			public Compiled<T> parameters(List<Function<T, ?>> extractors);

			/**
			 * Specify the functions to be used to extract the attributes to be updated on the database.
			 *
			 * @param extractors list of functions to be used to extract the attributes to be updated on the database
			 *
			 * @return the same sentence compiled with the specified functions
			 */
			default Compiled<T> parameters(Function<T, ?>... extractors)
			{
				return parameters(Arrays.asList(extractors));
			}

			@Override
			public Prepared<T> print();

			/**
			 * A SQL sentence linked to a database and compiled with a set or parameters.
			 * <p>
			 * A connected and compiled sentence is ready for execution
			 *
			 * @param <T> type of the parameters compiled with sentence
			 */
			public interface Compiled<T> extends SQL, Executable
			{

				/**
				 * Executes the sentence on the database with the specified parameters and fetches generated keys as objects of the specified
				 * type.
				 *
				 * @param <K> type of the keys to be fetched
				 * @param type type of the keys to be fetched
				 * @param consumer the consumer to be used to process the fetched keys
				 *
				 * @throws gate.error.ConstraintViolationException if any database constraint is violated during sentence execution
				 */
				public <K> void fetchGeneratedKeys(Class<K> type, BiConsumer<T, K> consumer) throws ConstraintViolationException;

				/**
				 * Executes the sentence on the database with the specified parameters and fetches generated keys as objects of the specified
				 * type.
				 *
				 * @param <K> type of the keys to be fetched
				 * @param type type of the keys to be fetched
				 * @param consumer the consumer to be used to process the fetched keys
				 *
				 * @throws gate.error.ConstraintViolationException if any database constraint is violated during sentence execution
				 */
				public <K> void fetchGeneratedKeyLists(Class<K> type, BiConsumer<T, List<K>> consumer) throws ConstraintViolationException;

				/**
				 * Defines a Consumer to be called each time a set or parameters is processed by the statement.
				 * <p>
				 * Very useful for tracking the progress of long batch operations
				 *
				 * @param consumer the consumer to be called each time the statement is executed. Only a single consumer can be defined at any
				 * time. If null, removes any previously defined consumer.
				 *
				 * @return the current sentence, for chained invocations
				 */
				public Compiled<T> observe(Consumer<T> consumer);

				@Override
				public Compiled<T> print();
			}
		}
	}

	/**
	 * Sentence builder.
	 */
	interface Builder extends SQLBuilder<Sentence>
	{

	}
}
