package gate.sql.mapper;

import gate.sql.Cursor;
import java.util.function.Function;

/**
 * Maps the current row of a cursor to a Java object.
 *
 * @param <T> mapped type
 */
@FunctionalInterface
public interface Mapper<T> extends Function<Cursor, T>
{

}
