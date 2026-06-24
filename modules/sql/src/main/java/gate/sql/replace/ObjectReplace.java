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
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Compiled replace builder bound to a source object.
 */
public class ObjectReplace<T> implements Replace, Sentence.Compiled.Builder
{

	private final T object;
	private final Class<T> type;
	private final List<Object> values = new ArrayList<>();
	private final StringJoiner columns = new StringJoiner(", ", "(", ")");
	private final StringJoiner parameters = new StringJoiner(", ", "(", ")");

	ObjectReplace(Class<T> type, T object)
	{
		this.type = Objects.requireNonNull(type);
		this.object = Objects.requireNonNull(object);
		if (!type.isAnnotationPresent(Entity.class))
			throw new PropertyError("%s is not an Entity", type.getName());
	}

	public ObjectReplace<T> set(String column, Object value)
	{
		columns.add(Formatter.identifier(Objects.requireNonNull(column)));
		parameters.add("?");
		values.add(value);
		return this;
	}

	public <R> ObjectReplace<T> set(Class<R> type, String column, R value)
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

	public ObjectReplace<T> set(PropertyReference<T, ?> property, Object value)
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

	public <R> ObjectReplace<T> set(PropertyReference<T, R> property)
	{
		return set(property, property.apply(object));
	}

	public final ObjectReplace<T> set(List<PropertyReference<T, ?>> properties)
	{
		for (PropertyReference<T, ?> property : Objects.requireNonNull(properties))
			set(property);
		return this;
	}

	public ObjectReplace<T> set(PropertyReference<T, ?> property, Function<T, ?> extractor)
	{
		@SuppressWarnings({"rawtypes", "unchecked"})
		ObjectReplace<T> replace = set((PropertyReference) Objects.requireNonNull(property),
				Objects.requireNonNull(extractor).apply(object));
		return replace;
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

		public ObjectReplace<T> set(String column, Supplier<?> supplier)
		{
			return assertion ? ObjectReplace.this.set(column, supplier.get()) : ObjectReplace.this;
		}

		public <R> ObjectReplace<T> set(Class<R> type, String column, Supplier<R> supplier)
		{
			return assertion ? ObjectReplace.this.set(type, column, supplier.get()) : ObjectReplace.this;
		}

		public ObjectReplace<T> set(PropertyReference<T, ?> property, Supplier<?> supplier)
		{
			return assertion ? ObjectReplace.this.set(property, supplier.get()) : ObjectReplace.this;
		}

		public <R> ObjectReplace<T> set(PropertyReference<T, R> property)
		{
			return assertion ? ObjectReplace.this.set(property) : ObjectReplace.this;
		}

		public ObjectReplace<T> set(List<PropertyReference<T, ?>> properties)
		{
			return assertion ? ObjectReplace.this.set(properties) : ObjectReplace.this;
		}

		public ObjectReplace<T> set(PropertyReference<T, ?> property, Function<T, ?> extractor)
		{
			return assertion ? ObjectReplace.this.set(property, extractor) : ObjectReplace.this;
		}

		public ObjectReplace<T> then(Function<ObjectReplace<T>, ObjectReplace<T>> function)
		{
			return assertion ? function.apply(ObjectReplace.this) : ObjectReplace.this;
		}

		public When when(boolean assertion)
		{
			return new When(this.assertion && assertion);
		}

		@Override
		public String toString()
		{
			return ObjectReplace.this.toString();
		}
	}
}
