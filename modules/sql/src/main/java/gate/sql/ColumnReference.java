package gate.sql;

import gate.type.PropertyReference;

import java.util.function.Function;

/**
 * Resolves SQL column names from {@link PropertyReference} method references.
 */
public record ColumnReference<T, R>(PropertyReference<T, R> property, String name,
                                    Function<R, ?> extractor)
{
}