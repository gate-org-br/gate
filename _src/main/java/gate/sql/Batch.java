package gate.sql;

import gate.error.ConstraintViolationException;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public interface Batch<T>
{

	/**
	 * Creates a command for the statement.
	 *
	 * @return the new command created
	 */
	Command createCommand();

	/**
	 * Executes the sentence on the database with the specified parameters.
	 *
	 * @param values objects to be processed
	 * @return the number of records affected by the sentence execution
	 *
	 * @throws gate.error.ConstraintViolationException if any database constraint is violated during sentence execution
	 */
	int execute(List<? extends T> values) throws ConstraintViolationException;

	/**
	 * Executes the sentence on the database with the specified parameters.
	 *
	 * @param values objects to be processed
	 *
	 * @throws gate.error.ConstraintViolationException if any database constraint is violated during sentence execution
	 */
	void execute(Stream<? extends T> values) throws ConstraintViolationException;

	/**
	 * Executes the sentence on the database with the specified parameters and fetches a generated key an object of the specified type.
	 *
	 * @param <K> type of the key to be fetched
	 * @param values objects to be processed
	 * @param type type of the key to be fetched
	 * @param consumer a consumer to process the generated key
	 *
	 * @throws gate.error.ConstraintViolationException if any database constraint is violated during sentence execution
	 */
	<K> void fetchGeneratedKey(List<? extends T> values, Class<K> type, BiConsumer<T, K> consumer) throws ConstraintViolationException;

	/**
	 * Executes the sentence on the database with the specified parameters and fetches a generated key an object of the specified type.
	 *
	 * @param <K> type of the key to be fetched
	 * @param values objects to be processed
	 * @param type type of the key to be fetched
	 * @param consumer a consumer to process the generated key
	 *
	 * @throws gate.error.ConstraintViolationException if any database constraint is violated during sentence execution
	 */
	<K> void fetchGeneratedKey(Stream<? extends T> values, Class<K> type, BiConsumer<T, K> consumer) throws ConstraintViolationException;

	/**
	 * Executes the sentence on the database with the specified parameters and fetches generated keys as objects of the specified type.
	 *
	 * @param <K> type of the keys to be fetched
	 * @param values objects to be processed
	 * @param type type of the keys to be fetched
	 * @param consumer a consumer to process the generated keys
	 *
	 * @throws gate.error.ConstraintViolationException if any database constraint is violated during sentence execution
	 */
	<K> void fetchGeneratedKeys(List<? extends T> values, Class<K> type, BiConsumer<T, List<K>> consumer) throws ConstraintViolationException;

	/**
	 * Executes the sentence on the database with the specified parameters and fetches generated keys as objects of the specified type.
	 *
	 * @param <K> type of the keys to be fetched
	 * @param values objects to be processed
	 * @param type type of the keys to be fetched
	 * @param consumer a consumer to process the generated keys
	 *
	 * @throws gate.error.ConstraintViolationException if any database constraint is violated during sentence execution
	 */
	<K> void fetchGeneratedKeys(Stream<? extends T> values, Class<K> type, BiConsumer<T, List<K>> consumer) throws ConstraintViolationException;
}
