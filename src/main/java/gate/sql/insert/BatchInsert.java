package gate.sql.insert;

import gate.converter.Converter;
import gate.sql.ColumnReference;
import gate.sql.Formatter;
import gate.sql.statement.Sentence;
import gate.type.PropertyReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;

/**
 * Batch insert builder based on extractor functions.
 */
public class BatchInsert<T> implements Insert, Sentence.Extractor.Compiled.Builder<T>
{

	private final String table;
	private final Class<T> type;
	private final boolean ignore;
	private final List<Function<T, ?>> extractors = new ArrayList<>();
	private final StringJoiner columns = new StringJoiner(", ", "(", ")");
	private final StringJoiner parameters = new StringJoiner(", ", "(", ")");

	BatchInsert(String table, Class<T> type, boolean ignore)
	{
		this.table = Formatter.identifier(Objects.requireNonNull(table));
		this.type = Objects.requireNonNull(type);
		this.ignore = ignore;
	}

	public BatchInsert<T> set(String column, Function<T, ?> extractor)
	{
		columns.add(Formatter.identifier(Objects.requireNonNull(column)));
		parameters.add("?");
		extractors.add(Objects.requireNonNull(extractor));
		return this;
	}

	public <R> BatchInsert<T> set(Class<R> type, String column, Function<T, R> extractor)
	{
		extractors.add(Objects.requireNonNull(extractor));
		Converter.getConverter(Objects.requireNonNull(type))
				.getColumns(Objects.requireNonNull(column))
				.map(Formatter::identifier)
				.peek(columns::add)
				.map(e -> "?")
				.forEach(parameters::add);
		return this;
	}

	public <R> BatchInsert<T> set(PropertyReference<T, R> property)
	{
		var column = ColumnReference.of(Objects.requireNonNull(property));
		Converter.getConverter(property.metadata().method().getReturnType())
				.getColumns(column.name())
				.map(Formatter::identifier)
				.peek(columns::add)
				.map(e -> "?")
				.forEach(parameters::add);
		extractors.add(value -> column.extractor().apply(property.apply(value)));
		return this;
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public BatchInsert<T> set(PropertyReference<T, ?> property, Function<T, ?> extractor)
	{
		Objects.requireNonNull(property);
		Objects.requireNonNull(extractor);
		ColumnReference column = ColumnReference.of((PropertyReference) property);
		Converter.getConverter(property.metadata().method().getReturnType())
				.getColumns(column.name())
				.map(Formatter::identifier)
				.peek(columns::add)
				.map(e -> "?")
				.forEach(parameters::add);
		extractors.add(value -> column.extractor().apply(extractor.apply(value)));
		return this;
	}

	@Override
	public Sentence.Extractor.Compiled<T> build()
	{
		return Sentence.of(toString()).from(type).parameters(extractors);
	}

	@Override
	public String toString()
	{
		return "insert " + (ignore ? "ignore " : "") + "into " + table + " " + columns + " values "
		       + parameters;
	}
}
