package gate.sql.update;

import gate.annotation.Entity;
import gate.error.PropertyError;
import gate.sql.ColumnReference;
import gate.sql.Formatter;
import gate.sql.annotation.Table;
import gate.sql.columnMapper.ColumnMapper;
import gate.sql.condition.CompiledCondition;
import gate.sql.condition.Condition;
import gate.sql.condition.ConstantCondition;
import gate.sql.statement.Sentence;
import gate.type.PropertyReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassUpdate<T> implements Update
{

	private final Class<T> type;

	ClassUpdate(Class<T> type)
	{
		Objects.requireNonNull(type);
		if (!type.isAnnotationPresent(Entity.class))
			throw new PropertyError("%s is not an Entity", type.getName());
		this.type = type;
	}

	public BatchUpdate<T> from()
	{
		return new BatchUpdate<>(toString(), type);
	}

	public BatchUpdate<T> from(Class<T> type)
	{
		return new BatchUpdate<>(toString(), type);
	}

	public Compiled set(String column, Object value)
	{
		return new Compiled().set(column, value);
	}

	public <R> Compiled set(Class<R> type, String column, R value)
	{
		return new Compiled().set(type, column, value);
	}

	public <R> Compiled set(PropertyReference<T, R> property, R value)
	{
		return new Compiled().set(Objects.requireNonNull(property), value);
	}

	public final <R> Compiled set(T object, List<PropertyReference<T, R>> properties)
	{
		return new Compiled().set(object, properties);
	}

	public When when(boolean assertion)
	{
		return assertion ? new When() : new DisabledWhen();
	}

	public Compiled compiled()
	{
		return new Compiled();
	}

	@Override
	public String toString()
	{
		return "update " + Table.Extractor.getFullName(type);
	}

	public class Compiled implements Sentence.Compiled.Builder
	{

		private final List<Object> values;
		private final StringJoiner columns = new StringJoiner(", ");

		Compiled()
		{
			this(new ArrayList<>());
		}

		Compiled(List<Object> values)
		{
			this.values = values;
		}

		@SuppressWarnings("unchecked")
		public Compiled set(String column, Object value)
		{
			if (value != null)
				return set((Class<Object>) value.getClass(), column, value);

			columns.add(Formatter.identifier(column) + " = ?");
			values.add(null);
			return this;
		}

		public <R> Compiled set(Class<R> type, String column, R value)
		{
			values.add(value);
			ColumnMapper.getColumnMapper(type)
					.getColumns(column)
					.map(Formatter::identifier)
					.map(name -> name + " = ?")
					.forEach(columns::add);
			return this;
		}

		public Compiled set(PropertyReference<T, ?> property, Object value)
		{
			var column = ColumnReference.of(property);
			ColumnMapper.getColumnMapper(property.metadata().method().getReturnType())
					.getColumns(column.name())
					.map(Formatter::identifier)
					.map(name -> name + " = ?")
					.forEach(columns::add);
			@SuppressWarnings({"rawtypes", "unchecked"})
			Object extracted = ((ColumnReference) column).extractor().apply(value);
			values.add(extracted);
			return this;
		}

		public final <R> Compiled set(T object, List<PropertyReference<T, R>> properties)
		{
			Objects.requireNonNull(object);
			for (PropertyReference<T, R> property : Objects.requireNonNull(properties))
			{
				var column = ColumnReference.of(Objects.requireNonNull(property));
				ColumnMapper.getColumnMapper(property.metadata().method().getReturnType())
						.getColumns(column.name())
						.map(Formatter::identifier)
						.map(name -> name + " = ?")
						.forEach(columns::add);
				values.add(column.extractor().apply(property.apply(object)));
			}
			return this;
		}

		public CompiledWhere where(ConstantCondition condition)
		{
			return new CompiledWhere(condition);
		}

		public CompiledWhere where(CompiledCondition condition)
		{
			return new CompiledWhere(condition);
		}

		@Override
		public Sentence.Compiled build()
		{
			return Sentence.of(toString()).parameters(values);
		}

		public When when(boolean assertion)
		{
			return assertion ? new When() : new DisabledWhen();
		}

		@Override
		public String toString()
		{
			return ClassUpdate.this + " set " + columns;
		}

		public class CompiledWhere implements Sentence.Compiled.Builder
		{

			private final Condition condition;

			CompiledWhere(ConstantCondition condition)
			{
				this.condition = condition;
			}

			CompiledWhere(CompiledCondition condition)
			{
				this.condition = condition;
			}

			@Override
			public Sentence.Compiled build()
			{
				return Sentence.of(toString()).parameters(Stream.concat(values.stream(), condition.getParameters()).collect(Collectors.toList()));
			}

			@Override
			public String toString()
			{
				return Compiled.this + " where " + condition;
			}

			public LimitedCompiledWhere limit(int limit)
			{
				return new LimitedCompiledWhere(limit);
			}

			public class LimitedCompiledWhere implements Sentence.Compiled.Builder
			{

				private final int limit;

				LimitedCompiledWhere(int limit)
				{
					this.limit = limit;
				}

				@Override
				public Sentence.Compiled build()
				{
					return Sentence.of(toString()).parameters(Stream.concat(values.stream(), condition.getParameters()).collect(Collectors.toList()));
				}

				@Override
				public String toString()
				{
					return CompiledWhere.this + " limit " + limit;
				}
			}
		}

		public class When
		{

			public Compiled set(String column, Supplier<?> supplier)
			{
				return Compiled.this.set(column, supplier.get());
			}

			public <R> Compiled set(Class<R> type, String column, Supplier<R> supplier)
			{
				return Compiled.this.set(type, column, supplier.get());
			}

			public Compiled set(PropertyReference<T, ?> property, Supplier<?> supplier)
			{
				return Compiled.this.set(property, supplier.get());
			}

			public <R> Compiled set(T object, List<PropertyReference<T, R>> properties)
			{
				return Compiled.this.set(object, properties);
			}

			public When when(boolean assertion)
			{
				return assertion ? this : new DisabledWhen();
			}
		}

		public class DisabledWhen extends When
		{

			@Override
			public Compiled set(String column, Supplier<?> supplier)
			{
				return Compiled.this;
			}

			@Override
			public <R> Compiled set(Class<R> type, String column, Supplier<R> supplier)
			{
				return Compiled.this;
			}

			@Override
			public Compiled set(PropertyReference<T, ?> property, Supplier<?> supplier)
			{
				return Compiled.this;
			}

			@Override
			public <R> Compiled set(T object, List<PropertyReference<T, R>> properties)
			{
				return Compiled.this;
			}

			@Override
			public When when(boolean assertion)
			{
				return this;
			}
		}
	}

	public class When
	{

		public Compiled set(String column, Object value)
		{
			return new Compiled().set(column, value);
		}

		public Compiled set(String column, Supplier<?> supplier)
		{
			return new Compiled().set(column, supplier.get());
		}

		public <R> Compiled set(Class<R> type, String column, R value)
		{
			return new Compiled().set(type, column, value);
		}

		public <R> Compiled set(Class<R> type, String column, Supplier<R> supplier)
		{
			return new Compiled().set(type, column, supplier.get());
		}

		public <R> Compiled set(PropertyReference<T, R> property, Supplier<R> supplier)
		{
			return new Compiled().set(property, supplier.get());
		}

		public <R> Compiled set(T object, List<PropertyReference<T, R>> properties)
		{
			return new Compiled().set(object, properties);
		}

		public When when(boolean assertion)
		{
			return assertion ? this : new DisabledWhen();
		}
	}

	public class DisabledWhen extends When
	{

		@Override
		public Compiled set(String column, Object value)
		{
			return new Compiled();
		}

		@Override
		public Compiled set(String column, Supplier<?> supplier)
		{
			return new Compiled();
		}

		@Override
		public <R> Compiled set(Class<R> type, String column, R value)
		{
			return new Compiled();
		}

		@Override
		public <R> Compiled set(Class<R> type, String column, Supplier<R> supplier)
		{
			return new Compiled();
		}

		@Override
		public <R> Compiled set(PropertyReference<T, R> property, Supplier<R> supplier)
		{
			return new Compiled();
		}

		@Override
		public <R> Compiled set(T object, List<PropertyReference<T, R>> properties)
		{
			return new Compiled();
		}

		@Override
		public When when(boolean assertion)
		{
			return this;
		}
	}
}