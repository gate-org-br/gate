package gate.sql.replace;

import gate.adapter.columnMapper.ColumnMapper;
import gate.adapter.registry.ColumnReferenceRegistry;
import gate.annotation.Entity;
import gate.error.PropertyError;
import gate.sql.ColumnReference;
import gate.sql.Formatter;
import gate.sql.annotation.Table;
import gate.sql.statement.Sentence;
import gate.type.PropertyReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Supplier;

/**
 * Compiled replace builder for a mapped java class.
 */
public class ClassReplace<T> implements Replace, Sentence.Compiled.Builder
{

	private final Class<T> type;
	private final List<Object> values = new ArrayList<>();
	private final StringJoiner columns = new StringJoiner(", ", "(", ")");
	private final StringJoiner parameters = new StringJoiner(", ", "(", ")");

	ClassReplace(Class<T> type)
	{
		this.type = Objects.requireNonNull(type);
		if (!type.isAnnotationPresent(Entity.class))
			throw new PropertyError("%s is not an Entity", type.getName());
	}

	public BatchReplace<T> from()
	{
		return new BatchReplace<>(Table.Extractor.getFullName(type), type);
	}

	public BatchReplace<T> from(Class<T> type)
	{
		return from();
	}

	public ClassReplace<T> set(String column, Object value)
	{
		columns.add(Formatter.identifier(Objects.requireNonNull(column)));
		parameters.add("?");
		values.add(value);
		return this;
	}

	public <R> ClassReplace<T> set(Class<R> type, String column, R value)
	{
		values.add(value);
		ColumnMapper.getColumnMapper(Objects.requireNonNull(type))
				.getColumns(Objects.requireNonNull(column))
				.map(Formatter::identifier)
				.peek(columns::add)
				.map(e -> "?")
				.forEach(parameters::add);
		return this;
	}

	public final <R> ClassReplace<T> set(T object, List<PropertyReference<T, R>> properties)
	{
		Objects.requireNonNull(object);
		for (PropertyReference<T, R> property : Objects.requireNonNull(properties))
		{
			var column = ColumnReferenceRegistry.INSTANCE.get(Objects.requireNonNull(property));
			ColumnMapper.getColumnMapper(property.metadata().method().getReturnType())
					.getColumns(column.name())
					.map(Formatter::identifier)
					.peek(columns::add)
					.map(e -> "?")
					.forEach(parameters::add);
			values.add(column.extractor().apply(property.apply(object)));
		}
		return this;
	}

	public ClassReplace<T> set(PropertyReference<T, ?> property, Object value)
	{
		var column = ColumnReferenceRegistry.INSTANCE.get(Objects.requireNonNull(property));
		ColumnMapper.getColumnMapper(property.metadata().method().getReturnType())
				.getColumns(column.name())
				.map(Formatter::identifier)
				.peek(columns::add)
				.map(e -> "?")
				.forEach(parameters::add);
		@SuppressWarnings({"rawtypes", "unchecked"})
		Object extracted = ((ColumnReference) column).extractor().apply(value);
		values.add(extracted);
		return this;
	}

	public When when(boolean assertion)
	{
		return new When(assertion);
	}

	@Override
	public Sentence.Compiled build()
	{
		return Sentence.of(toString()).parameters(values);
	}

	@Override
	public String toString()
	{
		return "replace into " + Table.Extractor.getFullName(type) + " " + columns + " values " + parameters;
	}

	public class When
	{
		private final boolean assertion;

		private When(boolean assertion)
		{
			this.assertion = assertion;
		}

		public ClassReplace<T> set(String column, Supplier<?> supplier)
		{
			return assertion ? ClassReplace.this.set(column, supplier.get()) : ClassReplace.this;
		}

		public <R> ClassReplace<T> set(Class<R> type, String column, Supplier<R> supplier)
		{
			return assertion ? ClassReplace.this.set(type, column, supplier.get()) : ClassReplace.this;
		}

		public ClassReplace<T> set(PropertyReference<T, ?> property, Supplier<?> supplier)
		{
			return assertion ? ClassReplace.this.set(property, supplier.get()) : ClassReplace.this;
		}

		public When when(boolean assertion)
		{
			return new When(this.assertion && assertion);
		}

		@Override
		public String toString()
		{
			return ClassReplace.this.toString();
		}
	}
}