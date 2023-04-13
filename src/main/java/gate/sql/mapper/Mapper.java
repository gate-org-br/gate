package gate.sql.mapper;

import gate.sql.Cursor;
import java.util.function.Function;

/**
 * Maps the current row of a cursor to a java object of the specified type
 *
 * @param <T> type of the object to be created from the cursor
 */
@FunctionalInterface
public interface Mapper<T> extends Function<Cursor, T>
{

}
