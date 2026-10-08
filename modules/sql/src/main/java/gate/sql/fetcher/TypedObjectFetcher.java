package gate.sql.fetcher;

import gate.sql.Cursor;
import java.util.Optional;

/**
 * Fetches the first column of the first row as an object of the specified type.
 *
 * @param <T> target type
 */
public class TypedObjectFetcher<T> implements Fetcher<Optional<T>> {

	private final Class<T> type;

	/**
	 * Creates a new TypedObjectFetcher with the specified type.
	 *
	 * @param type the type of the object to be fetched
	 */
	public TypedObjectFetcher(Class<T> type) {
		this.type = type;
	}

	/**
	 * Fetches the first column of the first row as the requested type.
	 *
	 * @param cursor cursor to be fetched
	 * @return optional containing the first value converted to the requested type, or empty if the cursor has no rows
	 */
	@Override
	public Optional<T> fetch(Cursor cursor) {
		if (!cursor.next())
			return Optional.empty();
		return Optional.ofNullable(cursor.getValue(type, 1));
	}
}
