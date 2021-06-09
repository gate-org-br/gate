package gate.sql.statement;

import gate.sql.Batch;
import gate.sql.Executable;
import gate.sql.Link;
import gate.sql.SQLBuilder;
import java.util.Arrays;
import java.util.List;
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
	static Sentence of(String sql)
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
	Connected connect(Link link);

	/**
	 * Compiles the sentence with a list of parameters.
	 *
	 * @param parameters the list of parameters to be compiled with the sentence
	 *
	 * @return the same sentence compiled with the specified parameters
	 */
	Compiled parameters(List<Object> parameters);

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
	 * 
	 * @param type type of the entities to be compiled with the sentence
	 *
	 * @return the same sentence prepared to be compiled with the specified entities and a list of functions
	 */
	<T> Extractor<T> from(Class<T> type);

	/**
	 * Compiles the sentence with a batch of parameters.
	 *
	 * @param batch the batch of parameters to be compiled with the sentence
	 *
	 * @return the same sentence compiled with the specified batch of parameters
	 */
	Compiled batch(List<List<Object>> batch);

	@Override
	Sentence print();

	/**
	 * A SQL sentence compiled with a set of parameters and not linked to a database.
	 * <p>
	 * A compiled sentence must be linked to a database before execution.
	 */
	interface Compiled extends SQL
	{

		/**
		 * Binds the sentence to a database link.
		 *
		 * @param link database link to be bound to the sentence
		 *
		 * @return the same sentence bound to the specified database link
		 */
		Connected connect(Link link);

		/**
		 * A SQL sentence linked to a database and compiled with a set of parameters.
		 * <p>
		 * A compiled and connected sentence is ready for execution
		 */
		interface Connected extends SQL, Executable
		{

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
			Connected observe(Consumer<List<Object>> consumer);

			@Override
			Connected print();

		}

		@Override
		Compiled print();

		/**
		 * Compiled sentence builder.
		 */
		interface Builder extends SQLBuilder<Sentence.Compiled>
		{

		}
	}

	interface Extractor<T> extends SQL
	{

		/**
		 * Specify the functions to be used to extract the attributes to be updated on the database.
		 *
		 * @param extractors list of functions to be used to extract the attributes to be updated on the database
		 *
		 * @return the same sentence compiled with the specified functions
		 */
		Compiled parameters(List<Function<T, ?>> extractors);

		/**
		 * Specify the functions to be used to extract the attributes to be updated on the database.
		 *
		 * @param extractors list of functions to be used to extract the attributes to be updated on the database
		 *
		 * @return the same sentence compiled with the specified functions
		 */
		default Compiled parameters(Function<T, ?>... extractors)
		{
			return Extractor.this.parameters(Arrays.asList(extractors));
		}

		@Override
		Extractor<T> print();

		/**
		 * A SQL sentence linked to a database and compiled with a set or parameters.
		 * <p>
		 * A connected and compiled sentence is ready for execution
		 *
		 */
		interface Compiled<T> extends SQL
		{

			/**
			 * Binds the sentence to a database link.
			 *
			 * @param link database link to be bound to the sentence
			 *
			 * @return the same sentence bound to the specified database link
			 */
			Connected<T> connect(Link link);

			/**
			 * A SQL sentence linked to a database and compiled with a set of parameters.
			 * <p>
			 * A compiled and connected sentence is ready for execution
			 */
			interface Connected<T> extends SQL, Batch<T>
			{

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
				Connected<T> observe(Consumer<T> consumer);

				@Override
				Connected<T> print();

			}

			/**
			 * Compiled sentence builder.
			 *
			 * 
			 */
			interface Builder<T> extends SQLBuilder<Sentence.Extractor.Compiled<T>>
			{

			}
		}
	}

	/**
	 * A SQL sentence linked to a database whose parameters are not defined.
	 * <p>
	 * A connected sentence must be compiled with a set of parameters before execution
	 */
	interface Connected extends SQL, Executable
	{

		/**
		 * Specify the type with the attributes to be updated on the database
		 *
		 * 
		 * @param type type with the attributes to be updated on the database
		 *
		 * @return the same sentence compiled with the specified type
		 */
		<T> Extractor<T> from(Class<T> type);

		/**
		 * Compiles the sentence with a list of parameters.
		 *
		 * @param parameters the list of parameters to be compiled with the sentence
		 *
		 * @return the same sentence compiled with the specified parameters
		 */
		Compiled parameters(List<Object> parameters);

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
		Compiled batch(List<List<?>> batch);

		@Override
		Connected print();

		/**
		 * A SQL sentence linked to a database and compiled with a set or parameters.
		 * <p>
		 * A connected and compiled sentence is ready for execution
		 */
		interface Compiled extends SQL, Executable
		{

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
			Compiled observe(Consumer<List<?>> consumer);

			@Override
			Compiled print();
		}

		interface Extractor<T> extends SQL
		{

			/**
			 * Specify the functions to be used to extract the attributes to be updated on the database.
			 *
			 * @param extractors list of functions to be used to extract the attributes to be updated on the database
			 *
			 * @return the same sentence compiled with the specified functions
			 */
			Compiled<T> parameters(List<Function<T, ?>> extractors);

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
			Extractor<T> print();

			/**
			 * A SQL sentence linked to a database and compiled with a set or parameters.
			 * <p>
			 * A connected and compiled sentence is ready for execution
			 *
			 * 
			 */
			interface Compiled<T> extends SQL, Batch<T>
			{

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
				Compiled<T> observe(Consumer<T> consumer);

				@Override
				Compiled<T> print();
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
