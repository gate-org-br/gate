package gate.sql;

import gate.error.ConstraintViolationException;
import java.util.List;

public interface Executable
{

	/**
	 * Executes the sentence on the database with the specified parameters.
	 *
	 * @return the number of records affected by the sentence execution
	 *
	 * @throws gate.error.ConstraintViolationException if any database constraint is violated during sentence execution
	 */
	public int execute() throws ConstraintViolationException;

	/**
	 * Executes the sentence on the database with the specified parameters and fetches generated keys as objects of the specified type.
	 *
	 * @param <K> type of the keys to be fetched
	 * @param type type of the keys to be fetched
	 *
	 * @return any generated keys as objects of the specified type
	 *
	 * @throws gate.error.ConstraintViolationException if any database constraint is violated during sentence execution
	 */
	public <K> List<K> fetchGeneratedKeys(Class<K> type) throws ConstraintViolationException;

	/**
	 * Executes the sentence on the database with the specified parameters and fetches generated keys as objects of the specified type.
	 *
	 * @param <K> type of the keys to be fetched
	 * @param type type of the keys to be fetched
	 *
	 * @return any generated keys as objects of the specified type
	 *
	 * @throws gate.error.ConstraintViolationException if any database constraint is violated during sentence execution
	 */
	public <K> List<List<K>> fetchGeneratedKeyLists(Class<K> type) throws ConstraintViolationException;

}
