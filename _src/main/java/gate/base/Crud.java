package gate.base;

import gate.error.AppException;
import gate.error.NotFoundException;
import gate.type.ID;
import java.util.List;

public interface Crud<T>
{

	/**
	 * Fetches a list of entities from the database based on the specified criteria.
	 *
	 * @param filter the entity to be used as search criteria
	 * @return the fetched entities
	 */
	List<T> search(T filter);

	/**
	 * Fetches an entity from the database.
	 *
	 * @param id the id of the entity to be fetched
	 * @return the fetched entity
	 * @throws NotFoundException if the specified entity could not be found
	 */
	T select(ID id) throws NotFoundException;

	/**
	 * Inserts the specified entity on the database.
	 *
	 * @param value the entity to be inserted
	 * @throws AppException if the specified entity cannot be inserted
	 */
	void insert(T value) throws AppException;

	/**
	 * Updates the specified entity on the database.
	 *
	 * @param value the entity to be updated
	 * @throws AppException if the specified entity cannot be updated
	 */
	void update(T value) throws AppException;

	/**
	 * Delete the specified entity from the database.
	 *
	 * @param value the entity to be deleted
	 * @throws AppException if the specified entity cannot be deleted
	 */
	void delete(T value) throws AppException;
}
