package gate.sql;

import gate.lang.json.JsonArray;
import gate.lang.json.JsonObject;
import gate.lang.property.Property;
import gate.sql.fetcher.ArrayFetcher;
import gate.sql.fetcher.ArrayListFetcher;
import gate.sql.fetcher.DataGridFetcher;
import gate.sql.fetcher.EntityFetcher;
import gate.sql.fetcher.EntityListFetcher;
import gate.sql.fetcher.Fetcher;
import gate.sql.fetcher.JsonArrayFetcher;
import gate.sql.fetcher.JsonObjectFetcher;
import gate.sql.fetcher.MapFetcher;
import gate.sql.fetcher.MapListFetcher;
import gate.sql.fetcher.ObjectFetcher;
import gate.sql.fetcher.ObjectListFetcher;
import gate.sql.fetcher.PropertyEntityListFetcher;
import gate.sql.fetcher.TypedArrayFetcher;
import gate.sql.fetcher.TypedArrayListFetcher;
import gate.sql.fetcher.TypedDataGridFetcher;
import gate.sql.fetcher.TypedMapFetcher;
import gate.sql.fetcher.TypedMapListFetcher;
import gate.sql.fetcher.TypedObjectFetcher;
import gate.sql.fetcher.TypedObjectListFetcher;
import gate.type.DataGrid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Object that can be fetched for data on different formats.
 */
public interface Fetchable
{

	/**
	 * Fetches results using the specified Fetcher.
	 *
	 * @param <T> type of results to be fetched
	 * @param fecher Fetcher to be used to fetch results
	 *
	 * @return the results fetched as a java object of the specified type
	 */
	<T> T fetch(Fetcher<T> fecher);

	/**
	 * Fetches the first column of the first row as a java object.
	 *
	 * @return an Optional describing the first column of the first row as a java object or an empty Optional if the result is empty
	 */
	default Optional<Object> fetchObject()
	{
		return fetch(new ObjectFetcher());
	}

	/**
	 * Fetches the first column of the first row as a java object of the specified type.
	 *
	 * @param <T> type of the object fetched
	 * @param type type of the object to be fetched
	 *
	 * @return an Optional describing the first column of the first row as a java object of the specified type or an empty Optional if the result is empty
	 */
	default <T> Optional<T> fetchObject(Class<T> type)
	{
		return fetch(new TypedObjectFetcher<>(type));
	}

	/**
	 * Fetches the first column of each row as as list of java objects.
	 *
	 * @return the first column of each row as as list of java objects
	 */
	default List<Object> fetchObjectList()
	{
		return fetch(new ObjectListFetcher());
	}

	/**
	 * Fetches the first column of each row as as list of java objects of the specified type.
	 *
	 * @param <T> type of the objects fetched
	 * @param type type of the objects to be fetched
	 *
	 * @return the first column of each row as as list of java objects of the specified type
	 */
	default <T> List<T> fetchObjectList(Class<T> type)
	{
		return fetch(new TypedObjectListFetcher<>(type));
	}

	/**
	 * Fetches the first row as a java Array.
	 *
	 * @return an Optional describing the first row as a java array or an empty Optional if the result is empty
	 */
	default Optional<Object[]> fetchArray()
	{
		return fetch(new ArrayFetcher());
	}

	/**
	 * Fetches the first row as a java Array of objects of the specified types.
	 *
	 * @param types types of objects to be fetched
	 *
	 * @return an Optional describing the first row as a java array of the specified types or an empty Optional if the result is empty
	 */
	default Optional<Object[]> fetchArray(Class<?>... types)
	{
		return fetch(new TypedArrayFetcher(types));
	}

	/**
	 * Fetches each row as a list of java arrays.
	 *
	 * @return each row fetched as a list of java arrays
	 */
	default List<Object[]> fetchArrayList()
	{
		return fetch(new ArrayListFetcher());
	}

	/**
	 * Fetches each row as a list of java arrays of Objects of the specified types.
	 *
	 * @param types types of objects to be fetched
	 *
	 * @return each row fetched as a list of java arrays of Objects of the specified types
	 */
	default List<Object[]> fetchArrayList(Class<?>... types)
	{
		return fetch(new TypedArrayListFetcher(types));
	}

	/**
	 * Fetches the first row as a map whose keys are the column names and values are the column values.
	 *
	 * @return an Optional describing the first row as a map whose keys are the column names and values are the column values or an empty Optional if the
	 * result is empty
	 */
	default Optional<Map<String, Object>> fetchMap()
	{
		return fetch(new MapFetcher());
	}

	/**
	 * Fetches the first row as a map whose keys are the column names and values are the column values as objects of the specified types.
	 *
	 * @param types types of the objects to be fetched
	 *
	 * @return an Optional describing the first row as a map whose keys are the column names and values are the column values as objects of the specified
	 * type or an empty Optional if the result is empty
	 */
	default Optional<Map<String, Object>> fetchMap(Class<?>... types)
	{
		return fetch(new TypedMapFetcher(types));
	}

	/**
	 * Fetches each row as a list of maps whose keys are the column names and values are the column values.
	 *
	 * @return each row as a list of maps whose keys are the column names and values are the column values
	 */
	default List<Map<String, Object>> fetchMapList()
	{
		return fetch(new MapListFetcher());
	}

	/**
	 * Fetches each row as a list of maps whose keys are the column names and values are the column values as objects of the specified types.
	 *
	 * @param types types of the objects to be fetched
	 *
	 * @return each row as a list of maps whose keys are the column names and values are the column values as objects of the specified types
	 */
	default List<Map<String, Object>> fetchMapList(Class<?>... types)
	{
		return fetch(new TypedMapListFetcher(types));
	}

	/**
	 * Fetches first row as a java object of the specified type with it's properties set to their respective column values.
	 *
	 * @param <T> type of the object fetched
	 * @param type type of the object to be fetched
	 *
	 * @return an Optional describing the first row of the result as a java object of the specified type with it's properties set to their respective column
	 * values or an empty Optional if result is empty
	 */
	default <T> Optional<T> fetchEntity(Class<T> type)
	{
		return fetch(new EntityFetcher<>(type));
	}

	/**
	 * Fetches each row as a list of java objects of the specified type with it's properties set to their respective column values.
	 *
	 * @param <T> type of the objects fetched
	 * @param type type of the objects to be fetched
	 *
	 * @return each row as a list of java objects of the specified type with it's properties set to their respective column values
	 */
	default <T> List<T> fetchEntityList(Class<T> type)
	{
		return fetch(new EntityListFetcher<>(type));
	}

	/**
	 * Fetches each row as a list of java object of the specified type with the specified properties set to their respective column values.
	 *
	 * @param <T> type of the object fetched
	 * @param type Type of the java object to be fetched
	 * @param properties properties to be fetched
	 *
	 * @return a list of java object of the specified type with the specified properties set to their respective column values
	 */
	default <T> List<T> fetchEntityList(Class<T> type, List<Property> properties)
	{
		return fetch(new PropertyEntityListFetcher<>(type, properties));
	}

	/**
	 * Fetches each row as a list of java object of the specified type with the specified properties set to their respective column values.
	 *
	 * @param <T> type of the object fetched
	 * @param type Type of the java object to be fetched
	 * @param properties properties to be fetched
	 *
	 * @return a list of java object of the specified type with the specified properties set to their respective column values
	 */
	default <T> List<T> fetchEntityList(Class<T> type, String... properties)
	{
		return fetch(new PropertyEntityListFetcher<>(type, properties));
	}

	/**
	 * Fetches each row as a data grid.
	 *
	 * @return each row as a data grid
	 */
	default DataGrid fetchDataGrid()
	{
		return fetch(new DataGridFetcher());
	}

	/**
	 * Fetches each row as a data grid of the specified types.
	 *
	 * @param types types of the objects to be fetched
	 *
	 * @return each row as a data grid of the specified types
	 */
	default DataGrid fetchDataGrid(Class<?>... types)
	{
		return fetch(new TypedDataGridFetcher(types));
	}

	/**
	 * Fetches first row as a JSON object with it's properties set to their respective column values.
	 *
	 * @return an Optional describing the first row of the result as a JSON object with it's properties set to their respective column
	 * values or an empty Optional if result is empty
	 */
	default Optional<JsonObject> fetchJsonObject()
	{
		return fetch(new JsonObjectFetcher());
	}

	/**
	 * Fetches the result set as a JSON array.
	 *
	 * @return a JSON array with the result set rows as JSON objects
	 */
	default JsonArray fetchJsonArray()
	{
		return fetch(new JsonArrayFetcher());
	}

	/**
	 * Fetches a boolean value from database.
	 *
	 * @return the result fetched as a java boolean
	 */
	boolean fetchBoolean();

	/**
	 * Fetches a char value from database.
	 *
	 * @return the result fetched as a java char
	 */
	char fetchChar();

	/**
	 * Fetches a byte value from database.
	 *
	 * @return the result fetched as a java byte
	 */
	byte fetchByte();

	/**
	 * Fetches a short value from database.
	 *
	 * @return the result fetched as a java short
	 */
	short fetchShort();

	/**
	 * Fetches an int value from database.
	 *
	 * @return the result fetched as a java int
	 */
	int fetchInt();

	/**
	 * Fetches a long value from database.
	 *
	 * @return the result fetched as a java long
	 */
	long fetchLong();

	/**
	 * Fetches a float value from database.
	 *
	 * @return the result fetched as a java float
	 */
	float fetchFloat();

	/**
	 * Fetches a double value from database.
	 *
	 * @return the result fetched as a java double
	 */
	double fetchDouble();
}
