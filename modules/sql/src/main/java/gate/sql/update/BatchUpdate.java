package gate.sql.update;

import gate.sql.columnMapper.ColumnMapper;
import gate.sql.ColumnReference;
import gate.sql.Formatter;
import gate.sql.condition.ConstantCondition;
import gate.sql.condition.ExtractorCondition;
import gate.sql.statement.Sentence;
import gate.type.PropertyReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Stream;

public class BatchUpdate<T> implements Update, Sentence.Extractor.Compiled.Builder<T>
{

	private final String table;
	private final Class<T> type;
	private final StringJoiner columns = new StringJoiner(", ");
	private final List<Function<T, ?>> extractors = new ArrayList<>();

	BatchUpdate(String table, Class<T> type)
	{
		this.table = Objects.requireNonNull(table);
		this.type = Objects.requireNonNull(type);
	}

	public BatchUpdate<T> set(String column, Function<T, ?> extractor)
	{
		columns.add(Formatter.identifier(column) + " = ?");
		extractors.add(Objects.requireNonNull(extractor));
		return this;
	}

	public <R> BatchUpdate<T> set(Class<R> type, String column, Function<T, R> extractor)
	{
		extractors.add(Objects.requireNonNull(extractor));
		ColumnMapper.getColumnMapper(Objects.requireNonNull(type))
				.getColumns(Objects.requireNonNull(column))
				.map(Formatter::identifier)
				.map(name -> name + " = ?")
				.forEach(columns::add);
		return this;
	}

	public <R> BatchUpdate<T> set(PropertyReference<T, R> property)
	{
		var column = ColumnReference.of(Objects.requireNonNull(property));
		ColumnMapper.getColumnMapper(property.metadata().method().getReturnType())
				.getColumns(column.name())
				.map(Formatter::identifier)
				.map(name -> name + " = ?")
				.forEach(columns::add);
		extractors.add(value -> column.extractor().apply(property.apply(value)));
		return this;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public BatchUpdate<T> set(PropertyReference<T, ?> property, Function<T, ?> extractor)
	{
		Objects.requireNonNull(property);
		Objects.requireNonNull(extractor);

		ColumnReference column = ColumnReference.of((PropertyReference) property);
		ColumnMapper.getColumnMapper(property.metadata().method().getReturnType())
				.getColumns(column.name())
				.map(Formatter::identifier)
				.map(name -> name + " = ?")
				.forEach(columns::add);
		extractors.add(value -> column.extractor().apply(extractor.apply(value)));
		return this;
	}

	public Sentence.Extractor.Compiled.Builder<T> where(ConstantCondition condition)
	{
		return new Sentence.Extractor.Compiled.Builder<>()
		{
			@Override
			public Sentence.Extractor.Compiled<T> build()
			{
				return Sentence.of(toString()).from(type).parameters(extractors);
			}

			@Override
			public String toString()
			{
				return BatchUpdate.this + " where " + condition;
			}
		};
	}

	public Sentence.Extractor.Compiled.Builder<T> where(ExtractorCondition<T> condition)
	{
		@SuppressWarnings("unchecked")
		var parameters = Stream.concat(extractors.stream(), condition.getParameters().map(e -> (Function<T, ?>) e)).toList();

		return new Sentence.Extractor.Compiled.Builder<>()
		{
			@Override
			public Sentence.Extractor.Compiled<T> build()
			{
				return Sentence.of(toString()).from(type).parameters(parameters);
			}

			@Override
			public String toString()
			{
				return BatchUpdate.this + " where " + condition;
			}
		};
	}

	@Override
	public Sentence.Extractor.Compiled<T> build()
	{
		return Sentence.of(toString()).from(type).parameters(extractors);
	}

	@Override
	public String toString()
	{
		return table + " set " + columns;
	}
}
