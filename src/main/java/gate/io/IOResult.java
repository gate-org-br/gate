package gate.io;

import gate.lang.json.JsonArray;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonObject;
import gate.stream.CheckedConsumer;
import gate.stream.CheckedPredicate;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Represents a generic I/O result backed by a consumable {@link InputStream}.
 *
 * <p>
 * This interface defines multiple high-level ways to consume structured or
 * unstructured data produced by an I/O operation such as an HTTP request,
 * file access or resource loading.
 * </p>
 *
 * <p>
 * The underlying stream is assumed to be consumable only once.
 * Implementations must ensure proper resource management.
 * </p>
 */
public interface IOResult
{

    /**
     * Reads the result using a {@link Reader}.
     *
     * @param reader reader implementation
     * @param <T>    result type
     * @return parsed result
     * @throws IOException on I/O error
     */
    <T> T read(Reader<T> reader) throws IOException;

    /**
     * Processes the result using a {@link Processor}.
     *
     * @param processor processor implementation
     * @param <T>       processing context type
     * @return number of processed elements
     * @throws IOException               on I/O error
     * @throws InvocationTargetException if the processor throws a checked exception
     */
    <T> long process(Processor<T> processor)
            throws IOException, InvocationTargetException;

    /**
     * Creates a lazy {@link Stream} over the result input stream.
     *
     * <p>
     * The provided function must convert the {@link InputStream} into a
     * {@link Spliterator}. The returned stream must be closed by the caller.
     * </p>
     *
     * @param spliterator spliterator factory
     * @param <T>         stream element type
     * @return lazy stream
     * @throws IOException on I/O error
     */
    <T> Stream<T> stream(Function<InputStream, Spliterator<T>> spliterator)
            throws IOException;

    /**
     * Reads the result as a list of lines using the default charset.
     *
     * @return list of lines
     * @throws IOException on I/O error
     */
    default List<String> readLines() throws IOException
    {
        return read(LineReader.getInstance());
    }

    /**
     * Processes each line using a predicate.
     *
     * @param action line predicate
     * @return number of processed lines
     * @throws IOException               on I/O error
     * @throws InvocationTargetException if the predicate throws a checked exception
     */
    default long processLines(CheckedPredicate<String> action)
            throws IOException, InvocationTargetException
    {
        return process(new LineProcessor(action));
    }

    /**
     * Processes each line using a consumer.
     *
     * @param action line consumer
     * @return number of processed lines
     * @throws IOException               on I/O error
     * @throws InvocationTargetException if the consumer throws a checked exception
     */
    default long processLines(CheckedConsumer<String> action)
            throws IOException, InvocationTargetException
    {
        return process(new LineProcessor(action));
    }

    /**
     * Creates a lazy stream of lines using the default charset.
     *
     * @return line stream
     * @throws IOException on I/O error
     */
    default Stream<String> lineStream() throws IOException
    {
        return stream(LineSpliterator::new);
    }

    /**
     * Reads the result as CSV rows using the default charset.
     *
     * @return list of CSV rows
     * @throws IOException on I/O error
     */
    default List<List<String>> readCSV() throws IOException
    {
        return read(CSVReader.getInstance());
    }

    /**
     * Creates a lazy stream of CSV rows.
     *
     * @return CSV stream
     * @throws IOException on I/O error
     */
    default Stream<List<String>> csvStream() throws IOException
    {
        return stream(CSVSpliterator::new);
    }

    /**
     * Processes CSV rows using a predicate.
     *
     * @param action row predicate
     * @return number of processed rows
     * @throws IOException               on I/O error
     * @throws InvocationTargetException if the predicate throws a checked exception
     */
    default long processCSV(CheckedPredicate<List<String>> action)
            throws IOException, InvocationTargetException
    {
        return process(new CSVProcessor(action));
    }

    /**
     * Processes CSV rows using a consumer.
     *
     * @param action row consumer
     * @return number of processed rows
     * @throws IOException               on I/O error
     * @throws InvocationTargetException if the consumer throws a checked exception
     */
    default long processCSV(CheckedConsumer<List<String>> action)
            throws IOException, InvocationTargetException
    {
        return process(new CSVProcessor(action));
    }

    /**
     * Reads the result into an object.
     *
     * @param type target type
     * @param <T>  object type
     * @return optional object
     * @throws IOException on I/O error
     */
    default <T> Optional<T> readObject(Class<T> type) throws IOException
    {
        return read(ObjectReader.getInstance(type));
    }

    /**
     * Reads the result into an object using an explicit content type.
     *
     * @param contentType content type
     * @param type        target type
     * @param <T>         object type
     * @return optional object
     * @throws IOException on I/O error
     */
    default <T> Optional<T> readObject(String contentType, Class<T> type)
            throws IOException
    {
        return read(ObjectReader.getInstance(contentType, type));
    }

    /**
     * Reads the result into a collection-like object.
     *
     * @param type        container type
     * @param elementType element type
     * @param <T>         container type
     * @return optional object
     * @throws IOException on I/O error
     */
    default <T> Optional<T> readObject(Class<T> type, Class<?> elementType)
            throws IOException
    {
        return read(ObjectReader.getInstance(type, elementType));
    }

    /**
     * Reads the result into a collection-like object using an explicit content type.
     *
     * @param contentType content type
     * @param type        container type
     * @param elementType element type
     * @param <T>         container type
     * @return optional object
     * @throws IOException on I/O error
     */
    default <T> Optional<T> readObject(
            String contentType,
            Class<T> type,
            Class<?> elementType
    ) throws IOException
    {
        return read(ObjectReader.getInstance(contentType, type, elementType));
    }

    /**
     * Reads the result as a JSON element.
     *
     * @return optional JSON element
     * @throws IOException on I/O error
     */
    default Optional<JsonElement> readJsonElement() throws IOException
    {
        return read(JsonElementReader.getInstance());
    }

    /**
     * Reads the result as a JSON object.
     *
     * @return optional JSON object
     * @throws IOException on I/O error
     */
    default Optional<JsonObject> readJsonObject() throws IOException
    {
        return read(JsonObjectReader.getInstance());
    }

    /**
     * Reads the result as a JSON array.
     *
     * @return optional JSON array
     * @throws IOException on I/O error
     */
    default Optional<JsonArray> readJsonArray() throws IOException
    {
        return read(JsonArrayReader.getInstance());
    }

    /**
     * Reads the result as a string using the default charset.
     *
     * @return string content
     * @throws IOException on I/O error
     */
    default String readString() throws IOException
    {
        return read(StringReader.getInstance());
    }

    /**
     * Reads the result as lines using a specific charset.
     *
     * @param charset charset name
     * @return list of lines
     * @throws IOException on I/O error
     */
    default List<String> readLines(String charset) throws IOException
    {
        return read(LineReader.getInstance(charset));
    }

    /**
     * Reads the result as a string using a specific charset.
     *
     * @param charset charset name
     * @return string content
     * @throws IOException on I/O error
     */
    default String readString(String charset) throws IOException
    {
        return read(StringReader.getInstance(charset));
    }

    /**
     * Reads the result as CSV rows using a specific charset.
     *
     * @param charset charset name
     * @return list of CSV rows
     * @throws IOException on I/O error
     */
    default List<List<String>> readCSV(String charset) throws IOException
    {
        return read(CSVReader.getInstance(charset));
    }

    /**
     * Reads the result as an XLS spreadsheet.
     *
     * @return spreadsheet data
     * @throws IOException on I/O error
     */
    default List<List<Object>> readXLS() throws IOException
    {
        return read(XLSReader.getInstance());
    }

    /**
     * Reads the result as an ODS spreadsheet.
     *
     * @return spreadsheet data
     * @throws IOException on I/O error
     */
    default List<List<String>> readODS() throws IOException
    {
        return read(ODSReader.getInstance());
    }

}
