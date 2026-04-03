package gate.sql.replace;

import gate.converter.Converter;
import gate.sql.ColumnReference;
import gate.sql.statement.Sentence;
import gate.type.PropertyReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;

/**
 * Batch replace builder based on extractor functions.
 */
public class BatchReplace<T> implements Replace, Sentence.Extractor.Compiled.Builder<T>
{

	private final String table;
	private final Class<T> type;
	private final List<Function<T, ?>> extractors = new ArrayList<>();
	private final StringJoiner columns = new StringJoiner(", ", "(", ")");
	private final StringJoiner parameters = new StringJoiner(", ", "(", ")");

	BatchReplace(String table, Class<T> type)
	{
		this.table = Objects.requireNonNull(table);
		this.type = Objects.requireNonNull(type);
	}

	public BatchReplace<T> set(String column, Function<T, ?> extractor)
	{
		columns.add(Objects.requireNonNull(column));
		parameters.add("?");
		extractors.add(Objects.requireNonNull(extractor));
		return this;
	}

	public <R> BatchReplace<T> set(Class<R> type, String column, Function<T, R> extractor)
	{
		extractors.add(Objects.requireNonNull(extractor));
		Converter.getConverter(Objects.requireNonNull(type))
				.getColumns(Objects.requireNonNull(column))
				.peek(columns::add)
				.map(e -> "?")
				.forEach(parameters::add);
		return this;
	}

	public <R> BatchReplace<T> set(PropertyReference<T, R> property)
	{
		var column = ColumnReference.of(Objects.requireNonNull(property));
		Converter.getConverter(property.metadata().method().getReturnType())
				.getColumns(column.name())
				.peek(columns::add)
				.map(e -> "?")
				.forEach(parameters::add);
		extractors.add(value -> column.extractor().apply(property.apply(value)));
		return this;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public BatchReplace<T> set(PropertyReference<T, ?> property, Function<T, ?> extractor)
	{
		Objects.requireNonNull(property);
		Objects.requireNonNull(extractor);
		ColumnReference column = ColumnReference.of((PropertyReference) property);
		Converter.getConverter(property.metadata().method().getReturnType())
				.getColumns(column.name())
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
		return "replace into " + table + " " + columns + " values " + parameters;
	}
}
