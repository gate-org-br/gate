package gate.sql;

import gate.lang.json.JsonArray;
import gate.lang.json.JsonObject;
import gate.lang.property.Property;
import gate.sql.fetcher.*;
import gate.sql.mapper.*;
import gate.type.DataGrid;
import gate.type.PivotTable;
import gate.type.TempFile;
import gate.util.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Object that can be fetched for data on different formats.
 */
public interface Fetchable
{

    /**
     * Fetches results using the specified Fetcher.
     *
     * @param fetcher Fetcher to be used to fetch results
     * @return the results fetched as a Java object of the specified type
     */
    <T> T fetch(Fetcher<T> fetcher);

    /**
     * Fetches results using the specified Function as a mapper.
     *
     * @param mapper Function to be used to fetch results
     * @return the results fetched as a stream of java object of the specified type
     */
    <T> Stream<T> stream(Mapper<T> mapper);

    /**
     * Fetches the first column of each row as a stream of java objects.
     *
     * @return the first column of each row as a stream of java objects
     */
    default Stream<Object> objectStream()
    {
        return stream(new ObjectMapper());
    }

    /**
     * Fetches the first column of each row as a stream of java objects.
     *
     * @param type type of the object to be fetched
     * @return the first column of each row as a stream of java objects
     */
    @SuppressWarnings("unchecked")
    default <T> Stream<T> objectStream(Class<T> type)
    {
        return stream(new TypedObjectMapper(type));
    }

    /**
     * Fetches the first column of the first row as a Java object.
     *
     * @return an Optional describing the first column of the first row as a Java object or an empty Optional if the result is empty
     */
    default Optional<Object> fetchObject()
    {
        return fetch(new ObjectFetcher());
    }

    /**
     * Fetches the first column of the first row as a Java object of the specified type.
     *
     * @param type type of the object to be fetched
     * @return an Optional describing the first column of the first row as a Java object of the specified type or an empty Optional if the result is empty
     */
    default <T> Optional<T> fetchObject(Class<T> type)
    {
        return fetch(new TypedObjectFetcher<>(type));
    }

    /**
     * Fetches the first column of each row as a list of Java objectss.
     *
     * @return the first column of each row as a list of Java objectss
     */
    default List<Object> fetchObjectList()
    {
        return fetch(new ObjectListFetcher());
    }

    /**
     * Fetches the first column of each row as a set of Java objectss.
     *
     * @return the first column of each row as a set of Java objectss
     */
    default Set<Object> fetchObjectSet()
    {
        return fetch(new ObjectSetFetcher());
    }

    /**
     * Fetches the first column of each row as a list of Java objectss of the specified type.
     *
     * @param type type of the objects to be fetched
     * @return the first column of each row as a list of Java objectss of the specified type
     */
    default <T> List<T> fetchObjectList(Class<T> type)
    {
        return fetch(new TypedObjectListFetcher<>(type));
    }

    /**
     * Fetches the first column of each row as a set of Java objectss of the specified type.
     *
     * @param type type of the objects to be fetched
     * @return the first column of each row as a set of Java objectss of the specified type
     */
    default <T> Set<T> fetchObjectSet(Class<T> type)
    {
        return fetch(new TypedObjectSetFetcher<>(type));
    }

    /**
     * Fetches the first row as a Java array.
     *
     * @return an Optional describing the first row as a Java array or an empty Optional if the result is empty
     */
    default Optional<Object[]> fetchArray()
    {
        return fetch(new ArrayFetcher());
    }

    /**
     * Fetches the first row as an int array.
     *
     * @return an Optional describing the first row as an int array or an empty Optional if the result is empty
     */
    default Optional<int[]> fetchIntArray()
    {
        return fetch(new IntArrayFetcher());
    }

    /**
     * Fetches each row as a Java array.
     *
     * @return each row as a Java array
     */
    default Stream<Object[]> arrayStream()
    {
        return stream(new ArrayMapper());
    }

    /**
     * Fetches each row as a Java array of objects of the specified types.
     *
     * @param types types of objects to be fetched
     * @return each row as a Java array of objects of the specified types
     */
    default Stream<Object[]> arrayStream(Class<?>... types)
    {
        return stream(new TypedArrayMapper(types));
    }

    /**
     * Fetches the first row as a Java array of objects of the specified types.
     *
     * @param types types of objects to be fetched
     * @return an Optional describing the first row as a Java array of the specified types or an empty Optional if the result is empty
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
     * Fetches each row as a set of java arrays.
     *
     * @return each row fetched as a set of java arrays
     */
    default Set<Object[]> fetchArraySet()
    {
        return fetch(new ArraySetFetcher());
    }

    /**
     * Fetches each row as a list of java arrays of Objects of the specified types.
     *
     * @param types types of objects to be fetched
     * @return each row fetched as a list of java arrays of Objects of the specified types
     */
    default List<Object[]> fetchArrayList(Class<?>... types)
    {
        return fetch(new TypedArrayListFetcher(types));
    }

    /**
     * Fetches each row as a set of java arrays of Objects of the specified types.
     *
     * @param types types of objects to be fetched
     * @return each row fetched as a set of java arrays of Objects of the specified types
     */
    default Set<Object[]> fetchArraySet(Class<?>... types)
    {
        return fetch(new TypedArraySetFetcher(types));
    }

    /**
     * Fetches each row as stream of maps whose keys are the column names and values are the column values.
     *
     * @return each row as stream of maps whose keys are the column names and values are the column values
     */
    default Stream<Map<String, Object>> mapStream()
    {
        return stream(new MapMapper());
    }

    /**
     * Fetches each row as stream of maps whose keys are the column names and values are the column values as objects of the specified types.
     *
     * @param types types of the objects to be fetched
     * @return each row as stream of maps whose keys are the column names and values are the column values as objects of the specified types
     */
    default Stream<Map<String, Object>> mapStream(Class<?>... types)
    {
        return stream(new TypedMapMapper(types));
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
     * @return each row as a list of maps whose keys are the column names and values are the column values as objects of the specified types
     */
    default List<Map<String, Object>> fetchMapList(Class<?>... types)
    {
        return fetch(new TypedMapListFetcher(types));
    }

    /**
     * Fetches each row as a list of maps whose keys are the column names and values are the column values as objects of the specified types.
     *
     * @param types    types of the objects to be fetched
     * @param pageSize number of rows per page
     * @param pageIndx index of the page
     * @return each row as a list of maps whose keys are the column names and values are the column values as objects of the specified types
     */
    default Page<Map<String, Object>> fetchMapPage(int pageSize, int pageIndx, Class<?>... types)
    {
        return fetch(new TypedMapPageFetcher(pageSize, pageIndx, types));
    }

    /**
     * Fetches each row as a stream of java objects of the specified type with its properties set to their respective column values.
     *
     * @param type type of the object to be fetched
     * @return each row as a stream of java objects of the specified type with its properties set to their respective column values
     */
    default <T> Stream<T> entityStream(Class<T> type)
    {
        return stream(new EntityMapper<>(type));
    }

    /**
     * Fetches first row as a Java object of the specified type with its properties set to their respective column values.
     *
     * @param type type of the object to be fetched
     * @return an Optional describing the first row of the result as a Java object of the specified type with its properties set to their respective column
     * values or an empty Optional if result is empty
     */
    default <T> Optional<T> fetchEntity(Class<T> type)
    {
        return fetch(new EntityFetcher<>(type));
    }

    /**
     * Fetches each row as a list of Java objectss of the specified type with its properties set to their respective column values.
     *
     * @param type type of the objects to be fetched
     * @return each row as a list of Java objectss of the specified type with its properties set to their respective column values
     */
    default <T> List<T> fetchEntityList(Class<T> type)
    {
        return fetch(new EntityListFetcher<>(type));
    }

    /**
     * Fetches each row as a list of Java objectss of the specified type with its properties set to their respective column values.
     *
     * @param type     type of the objects to be fetched
     * @param pageSize number of entities per page
     * @param pageIndx index of the page
     * @return each row as a list of Java objectss of the specified type with its properties set to their respective column values
     */
    default <T> Page<T> fetchEntityPage(Class<T> type, int pageSize, int pageIndx)
    {
        return fetch(new EntityPageFetcher<>(type, pageSize, pageIndx));
    }

    /**
     * Fetches each row as a set of Java objectss of the specified type with its properties set to their respective column values.
     *
     * @param type type of the objects to be fetched
     * @return each row as a set of Java objectss of the specified type with its properties set to their respective column values
     */
    default <T> Set<T> fetchEntitySet(Class<T> type)
    {
        return fetch(new EntitySetFetcher<>(type));
    }

    /**
     * Fetches each row as a list of Java objects of the specified type with the specified properties set to their respective column values.
     *
     * @param type       Type of the Java object to be fetched
     * @param properties properties to be fetched
     * @return a list of Java objects of the specified type with the specified properties set to their respective column values
     */
    default <T> List<T> fetchEntityList(Class<T> type, List<Property> properties)
    {
        return fetch(new PropertyEntityListFetcher<>(type, properties));
    }

    /**
     * Fetches each row as a set of Java objects of the specified type with the specified properties set to their respective column values.
     *
     * @param type       Type of the Java object to be fetched
     * @param properties properties to be fetched
     * @return a set of Java objects of the specified type with the specified properties set to their respective column values
     */
    default <T> Set<T> fetchEntitySet(Class<T> type, List<Property> properties)
    {
        return fetch(new PropertyEntitySetFetcher<>(type, properties));
    }

    /**
     * Fetches each row as a list of Java objects of the specified type with the specified properties set to their respective column values.
     *
     * @param type       Type of the Java object to be fetched
     * @param properties properties to be fetched
     * @return a list of Java objects of the specified type with the specified properties set to their respective column values
     */
    default <T> List<T> fetchEntityList(Class<T> type, String... properties)
    {
        return fetch(new PropertyEntityListFetcher<>(type, properties));
    }

    /**
     * Fetches each row as a set of Java objects of the specified type with the specified properties set to their respective column values.
     *
     * @param type       Type of the Java object to be fetched
     * @param properties properties to be fetched
     * @return a set of Java objects of the specified type with the specified properties set to their respective column values
     */
    default <T> Set<T> fetchEntitySet(Class<T> type, String... properties)
    {
        return fetch(new PropertyEntitySetFetcher<>(type, properties));
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
     * Fetches each row as a pivot table.
     *
     * @param <T>  type of the pivot table to be fetched
     * @param type type of the pivot table to be fetched
     * @return each row as a pivot table
     */
    default <T> PivotTable<T> fetchPivotTable(Class<T> type)
    {
        return fetch(new PivotTableFetcher<>(type, null));
    }

    /**
     * Fetches each row as a pivot table.
     *
     * @param <T>          type of the pivot table to be fetched
     * @param type         type of the pivot table to be fetched
     * @param defaultValue value to be used as default
     * @return each row as a pivot table
     */
    default <T> PivotTable<T> fetchPivotTable(Class<T> type, T defaultValue)
    {
        return fetch(new PivotTableFetcher<>(type, defaultValue));
    }

    /**
     * Fetches each row as a data grid of the specified types.
     *
     * @param types types of the objects to be fetched
     * @return each row as a data grid of the specified types
     */
    default DataGrid fetchDataGrid(Class<?>... types)
    {
        return fetch(new TypedDataGridFetcher(types));
    }

    /**
     * Fetches first row as a JSON object with its properties set to their respective column values.
     *
     * @return an Optional describing the first row of the result as a JSON object with its properties set to their respective column values or an empty
     * Optional if result is empty
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
     * Fetches the result set as a JSON tree.
     *
     * @return a JSON tree with the result set rows as JSON objects
     */
    default JsonArray fetchJsonTree()
    {
        return fetch(new JsonTreeFetcher());
    }

    /**
     * Fetches the result set as a JSON data set.
     *
     * @return a JSON data set with the result set rows as JSON objects
     */
    default JsonArray fetchJsonDataset()
    {
        return fetch(new JsonDatasetFetcher(false));
    }

    /**
     * Fetches the result set as a JSON data set.
     *
     * @param includeHeader true if the header is to be included on the dataset
     * @return a JSON data set with the result set rows as JSON objects
     */
    default JsonArray fetchJsonDataset(boolean includeHeader)
    {
        return fetch(new JsonDatasetFetcher(includeHeader));
    }

    /**
     * Fetches all rows as a temporary zipped multipart file.
     *
     * @return all rows as a temporary zipped multipart file
     */
    default TempFile fetchZipPackage()
    {
        return fetch(ZipPackageFetcher.INSTANCE);
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
