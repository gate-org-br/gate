package gate.sql.insert;

import gate.sql.ColumnReference;
import gate.sql.EntityHelper;
import gate.sql.statement.Sentence;
import gate.type.PropertyReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Insert sentence builder for a java class mapped as entity.
 */
public class ClassInsert<T> implements Insert
{

	private final Class<T> type;
	private boolean ignore;

	ClassInsert(Class<T> type)
	{
		Objects.requireNonNull(type);
		EntityHelper.check(type);
		this.type = type;
	}

	/**
	 * Creates a new SQL insert sentence builder bound to a source object.
	 *
	 * @param object source object used to extract values from property references
	 * @return the new object insert builder created
	 */

	public ObjectInsert<T> from(T object)
	{
		return new ObjectInsert<>(type, Objects.requireNonNull(object));
	}

	/**
	 * Adds an ignore modifier to the sentence.
	 *
	 * @return the same builder with ignore enabled
	 */
	public ClassInsert<T> ignore()
	{
		this.ignore = true;
		return this;
	}

	/**
	 * Adds a new column to be persisted, resolving the target column from a
	 * getter method reference or record accessor.
	 *
	 * @param property property reference used to resolve the target column
	 * @return the same builder with the added column
	 */
	public Generic set(PropertyReference<T, ?> property)
	{
		return new Generic().set(Objects.requireNonNull(property));
	}

	/**
	 * Adds new columns to be persisted, resolving each target column from getter
	 * method references or record accessors.
	 *
	 * @param properties property references used to resolve target columns
	 * @return the same builder with the added columns
	 */
	@SafeVarargs
	public final Generic set(PropertyReference<T, ?>... properties)
	{
		return new Generic().set(properties);
	}

	/**
	 * Adds a new column to be persisted with the specified value, resolving the
	 * target column from a getter method reference or record accessor.
	 *
	 * @param property property reference used to resolve the target column
	 * @param value    value associated with the column
	 * @param <R>      property type
	 * @return the same builder with the added column and value
	 */
	public <R> Compiled set(PropertyReference<T, R> property, R value)
	{
		return new Compiled().set(Objects.requireNonNull(property), value);
	}

	/**
	 * Adds new columns and associated values to the builder extracting values
	 * from the specified object.
	 *
	 * @param object     source object from where values are extracted
	 * @param properties property references used to resolve columns and extract values
	 * @return the same builder with the added columns and values
	 */
	@SafeVarargs
	public final Compiled set(T object, PropertyReference<T, ?>... properties)
	{
		return new Compiled().set(object, properties);
	}

	/**
	 * Adds the next column(s) only if previous specified condition is true.
	 *
	 * @param assertion condition to be checked
	 * @return conditional builder
	 */
	public When when(boolean assertion)
	{
		return assertion ? new When() : new DisabledWhen();
	}

	@Override
	public String toString()
	{
		return "insert " + (ignore ? "ignore " : "") + "into " + EntityHelper.getFullTableName(type);
	}

	/**
	 * SQL insert sentence builder for a mapped type with no values specified.
	 */
	public class Generic implements Sentence.Builder
	{

		private final StringJoiner columns = new StringJoiner(", ", "(", ")");
		private final StringJoiner parameters = new StringJoiner(", ", "(", ")");

		/**
		 * Adds a new column to be persisted, resolving the target column from a
		 * getter method reference or record accessor.
		 *
		 * @param property property reference used to resolve the target column
		 * @return the same builder with the added column
		 */
		public Generic set(PropertyReference<T, ?> property)
		{
			columns.add(ColumnReference.of(property));
			parameters.add("?");
			return this;
		}

		/**
		 * Adds new columns to be persisted.
		 *
		 * @param properties property references used to resolve target columns
		 * @return the same builder with the added columns
		 */
		@SafeVarargs
		public final Generic set(PropertyReference<T, ?>... properties)
		{
			for (PropertyReference<T, ?> property : Objects.requireNonNull(properties))
				set(Objects.requireNonNull(property));
			return this;
		}

		@Override
		public Sentence build()
		{
			return Sentence.of(toString());
		}

		@Override
		public String toString()
		{
			return ClassInsert.this + " " + columns + " values " + parameters;
		}

		public class When
		{
			/**
			 * Adds a new column to be persisted if previous specified condition was true.
			 *
			 * @param property property reference used to resolve the target column
			 * @return the same builder with the added column
			 */
			public Generic set(PropertyReference<T, ?> property)
			{
				return Generic.this.set(property);
			}

			/**
			 * Adds new columns to be persisted if previous specified condition was true.
			 *
			 * @param properties property references used to resolve target columns
			 * @return the same builder with the added columns
			 */
			@SuppressWarnings({"varargs", "unchecked"})
			public Generic set(PropertyReference<T, ?>... properties)
			{
				return Generic.this.set(properties);
			}

			/**
			 * Adds the next column(s) only if previous specified condition is true.
			 *
			 * @param assertion condition to be checked
			 * @return conditional builder
			 */
			public When when(boolean assertion)
			{
				return assertion ? this : new DisabledWhen();
			}
		}

		public class DisabledWhen extends When
		{
			/**
			 * Ignores the specified property and keeps current builder unchanged.
			 *
			 * @param property property reference used to resolve the target column
			 * @return the current generic builder unchanged
			 */
			@Override
			public Generic set(PropertyReference<T, ?> property)
			{
				return Generic.this;
			}

			/**
			 * Ignores the specified properties and keeps current builder unchanged.
			 *
			 * @param properties property references used to resolve target columns
			 * @return the current generic builder unchanged
			 */
			@Override
			@SuppressWarnings({"varargs", "unchecked"})
			public Generic set(PropertyReference<T, ?>... properties)
			{
				return Generic.this;
			}

			/**
			 * Keeps this conditional helper disabled for subsequent operations.
			 *
			 * @param assertion condition to be checked
			 * @return this disabled conditional helper
			 */
			@Override
			public When when(boolean assertion)
			{
				return this;
			}
		}
	}

	/**
	 * SQL insert sentence builder for a mapped type with values specified and ready for execution.
	 */
	public class Compiled implements Sentence.Compiled.Builder
	{

		private final List<Object> values = new ArrayList<>();
		private final StringJoiner columns = new StringJoiner(", ", "(", ")");
		private final StringJoiner parameters = new StringJoiner(", ", "(", ")");

		/**
		 * Adds a new column and associated value to the builder.
		 *
		 * @param property property reference used to resolve the target column
		 * @param value    value associated with the column
		 * @param <R>      property type
		 * @return the same builder with the added column and value
		 */
		public <R> Compiled set(PropertyReference<T, R> property, R value)
		{
			columns.add(ColumnReference.of(property));
			parameters.add("?");
			values.add(property.identity(value));
			return this;
		}

		/**
		 * Adds new columns and associated values extracting them from the specified object.
		 *
		 * @param object     source object
		 * @param properties property references used to resolve columns and values
		 * @return the same builder with the added columns and values
		 */
		@SafeVarargs
		public final Compiled set(T object, PropertyReference<T, ?>... properties)
		{
			T source = Objects.requireNonNull(object);
			for (PropertyReference<T, ?> property : Objects.requireNonNull(properties))
			{
				PropertyReference<T, ?> reference = Objects.requireNonNull(property);
				columns.add(ColumnReference.of(reference));
				parameters.add("?");
				values.add(reference.apply(source));
			}
			return this;
		}

		@Override
		public Sentence.Compiled build()
		{
			return Sentence.of(toString()).parameters(values);
		}

		@Override
		public String toString()
		{
			return ClassInsert.this + " " + columns + " values " + parameters;
		}

		public class When
		{
			/**
			 * Adds a new column and value if previous specified condition was true.
			 *
			 * @param property property reference used to resolve the target column
			 * @param value    value associated with the column
			 * @param <R>      property type
			 * @return the same builder with the added column and value
			 */
			public <R> Compiled set(PropertyReference<T, R> property, R value)
			{
				return Compiled.this.set(property, value);
			}

			/**
			 * Adds new columns and values if previous specified condition was true.
			 *
			 * @param object     source object
			 * @param properties property references used to resolve columns and values
			 * @return the same builder with the added columns and values
			 */
			@SuppressWarnings({"varargs", "unchecked"})
			public Compiled set(T object, PropertyReference<T, ?>... properties)
			{
				return Compiled.this.set(object, properties);
			}

			/**
			 * Adds the next column(s) only if previous specified condition is true.
			 *
			 * @param assertion condition to be checked
			 * @return conditional builder
			 */
			public When when(boolean assertion)
			{
				return assertion ? this : new DisabledWhen();
			}
		}

		public class DisabledWhen extends When
		{
			/**
			 * Ignores the specified property and keeps current builder unchanged.
			 *
			 * @param property property reference used to resolve the target column
			 * @param value    value associated with the column
			 * @param <R>      property type
			 * @return the current compiled builder unchanged
			 */
			@Override
			public <R> Compiled set(PropertyReference<T, R> property, R value)
			{
				return Compiled.this;
			}

			/**
			 * Ignores the specified properties and keeps current builder unchanged.
			 *
			 * @param object     source object
			 * @param properties property references used to resolve columns and values
			 * @return the current compiled builder unchanged
			 */
			@Override
			@SuppressWarnings({"varargs", "unchecked"})
			public Compiled set(T object, PropertyReference<T, ?>... properties)
			{
				return Compiled.this;
			}

			/**
			 * Keeps this conditional helper disabled for subsequent operations.
			 *
			 * @param assertion condition to be checked
			 * @return this disabled conditional helper
			 */
			@Override
			public When when(boolean assertion)
			{
				return this;
			}
		}
	}

	/**
	 * Conditional helper for class insert builders.
	 */
	public class When
	{
		/**
		 * Adds a new column to be persisted if previous specified condition was true.
		 *
		 * @param property property reference used to resolve the target column
		 * @return the same builder with the added column
		 */
		public Generic set(PropertyReference<T, ?> property)
		{
			return new Generic().set(property);
		}

		/**
		 * Adds new columns to be persisted if previous specified condition was true.
		 *
		 * @param properties property references used to resolve target columns
		 * @return the same builder with the added columns
		 */
		@SuppressWarnings({"varargs", "unchecked"})
		public Generic set(PropertyReference<T, ?>... properties)
		{
			return new Generic().set(properties);
		}

		/**
		 * Adds a new column and associated value if previous specified condition was true.
		 *
		 * @param property property reference used to resolve the target column
		 * @param value    value associated with the column
		 * @param <R>      property type
		 * @return the same builder with the added column and value
		 */
		public <R> Compiled set(PropertyReference<T, R> property, R value)
		{
			return new Compiled().set(property, value);
		}

		/**
		 * Adds new columns and values if previous specified condition was true.
		 *
		 * @param object     source object
		 * @param properties property references used to resolve columns and values
		 * @return the same builder with the added columns and values
		 */
		@SuppressWarnings({"varargs", "unchecked"})
		public Compiled set(T object, PropertyReference<T, ?>... properties)
		{
			return new Compiled().set(object, properties);
		}

		/**
		 * Adds the next column(s) only if previous specified condition is true.
		 *
		 * @param assertion condition to be checked
		 * @return conditional builder
		 */
		public When when(boolean assertion)
		{
			return assertion ? this : new DisabledWhen();
		}
	}

	/**
	 * Conditional helper for ignored branches on class insert builders.
	 */
	public class DisabledWhen extends When
	{
		/**
		 * Ignores the specified property and returns an empty generic builder.
		 *
		 * @param property property reference used to resolve the target column
		 * @return an empty generic builder
		 */
		@Override
		public Generic set(PropertyReference<T, ?> property)
		{
			return new Generic();
		}

		/**
		 * Ignores the specified properties and returns an empty generic builder.
		 *
		 * @param properties property references used to resolve target columns
		 * @return an empty generic builder
		 */
		@Override
		@SuppressWarnings({"varargs", "unchecked"})
		public Generic set(PropertyReference<T, ?>... properties)
		{
			return new Generic();
		}

		/**
		 * Ignores the specified property/value pair and returns an empty compiled builder.
		 *
		 * @param property property reference used to resolve the target column
		 * @param value    value associated with the column
		 * @param <R>      property type
		 * @return an empty compiled builder
		 */
		@Override
		public <R> Compiled set(PropertyReference<T, R> property, R value)
		{
			return new Compiled();
		}

		/**
		 * Ignores the specified properties and returns an empty compiled builder.
		 *
		 * @param object     source object
		 * @param properties property references used to resolve columns and values
		 * @return an empty compiled builder
		 */
		@Override
		@SuppressWarnings({"varargs", "unchecked"})
		public Compiled set(T object, PropertyReference<T, ?>... properties)
		{
			return new Compiled();
		}

		/**
		 * Keeps this conditional helper disabled for subsequent operations.
		 *
		 * @param assertion condition to be checked
		 * @return this disabled conditional helper
		 */
		@Override
		public When when(boolean assertion)
		{
			return this;
		}
	}
}
