package gate.sql.update;

import gate.sql.condition.CompiledCondition;
import gate.sql.condition.ConstantCondition;
import gate.sql.statement.Sentence;
import gate.type.PropertyReference;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Update builder bound to a source object.
 * <p>
 * Column values are extracted from the source object using property references.
 */
public class ObjectUpdate<T> implements Update
{

	private final T object;
	private final ClassUpdate<T> update;

	ObjectUpdate(Class<T> type, T object)
	{
		this.object = Objects.requireNonNull(object);
		this.update = new ClassUpdate<>(type);
	}

	/**
	 * Adds a new column/value pair by reading the given property from the bound object.
	 * Regular properties map to their corresponding column names and use the property
	 * value directly. Entity-reference properties follow the Gate foreign-key naming
	 * convention, mapping to a column named with the capitalized property name followed
	 * by {@code $} and the referenced entity identifier property. For example,
	 * {@code role} maps to {@code Role$id} and {@code masterRole} maps to
	 * {@code MasterRole$id}. The bound value is the referenced entity identifier.
	 *
	 * @param property property reference used to resolve the column name and extract the value
	 * @param <R>      property type
	 * @return compiled builder with the added column/value pair
	 */
	public <R> Compiled set(PropertyReference<T, R> property)
	{
		return compiled().set(property);
	}

	/**
	 * Adds a new column/value pair using the given explicit value instead of reading
	 * it from the bound object.
	 *
	 * @param property property reference used to resolve the target column
	 * @param value    explicit value to be bound for the resolved column
	 * @return compiled builder with the added column/value pair
	 */
	public Compiled set(PropertyReference<T, ?> property, Object value)
	{
		return compiled().set(property, value);
	}

	/**
	 * Adds a new column/value pair by computing the value from the bound object with
	 * the given extractor.
	 *
	 * @param property  property reference used to resolve the target column
	 * @param extractor function used to compute the bound value from the source object
	 * @return compiled builder with the added column/value pair
	 */
	public Compiled set(PropertyReference<T, ?> property, Function<T, ?> extractor)
	{
		return compiled().set(property, extractor);
	}

	/**
	 * Adds multiple column/value pairs by reading the given properties from the bound object.
	 * Regular properties map to their corresponding column names and use their values
	 * directly. Entity-reference properties follow the Gate foreign-key naming
	 * convention, mapping to columns named with the capitalized property name followed
	 * by {@code $} and the referenced entity identifier property. For example,
	 * {@code role} maps to {@code Role$id} and {@code masterRole} maps to
	 * {@code MasterRole$id}. The bound values are the referenced entity identifiers.
	 *
	 * @param properties property references used to resolve column names and extract values
	 * @return compiled builder with the added column/value pairs
	 */
	public final Compiled set(List<PropertyReference<T, ?>> properties)
	{
		return compiled().set(properties);
	}

	/**
	 * Creates a new compiled builder for this object update.
	 *
	 * @return compiled builder bound to this source object
	 */
	public Compiled compiled()
	{
		return new Compiled();
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

	/**
	 * Compiled object update builder.
	 */
	public class Compiled implements Sentence.Compiled.Builder
	{

		private final ClassUpdate<T>.Compiled delegate;

		private Compiled()
		{
			this.delegate = update.compiled();
		}

		/**
		 * Adds a new column/value pair, extracting value from the bound object.
		 *
		 * @param property property reference used to resolve column and value
		 * @param <R>      property type
		 * @return the same builder with the added column/value pair
		 */
		public <R> Compiled set(PropertyReference<T, R> property)
		{
			PropertyReference<T, R> reference = Objects.requireNonNull(property);
			delegate.set(reference, reference.apply(object));
			return this;
		}

		/**
		 * Adds a new column/value pair using the given explicit value.
		 *
		 * @param property property reference used to resolve the target column
		 * @param value    explicit value to be bound for the resolved column
		 * @return the same builder with the added column/value pair
		 */
		public Compiled set(PropertyReference<T, ?> property, Object value)
		{
			delegate.set(Objects.requireNonNull(property), value);
			return this;
		}

		/**
		 * Adds a new column/value pair by computing the value from the bound object.
		 *
		 * @param property  property reference used to resolve the target column
		 * @param extractor function used to compute the bound value from the source object
		 * @return the same builder with the added column/value pair
		 */
		public Compiled set(PropertyReference<T, ?> property, Function<T, ?> extractor)
		{
			delegate.set(Objects.requireNonNull(property), Objects.requireNonNull(extractor).apply(object));
			return this;
		}

		/**
		 * Adds new column/value pairs, extracting values from the bound object.
		 *
		 * @param properties property references used to resolve columns and values
		 * @return the same builder with the added column/value pairs
		 */
		@SuppressWarnings("unchecked")
		public final Compiled set(List<PropertyReference<T, ?>> properties)
		{
			for (PropertyReference<T, ?> property : Objects.requireNonNull(properties))
			{
				PropertyReference<T, Object> reference = (PropertyReference<T, Object>) property;
				delegate.set(reference, reference.apply(object));
			}
			return this;
		}

		/**
		 * Adds a constant condition to this update sentence.
		 *
		 * @param condition condition to be added to the builder
		 * @return builder section that accepts the update predicate
		 */
		public ClassUpdate<T>.Compiled.CompiledWhere where(ConstantCondition condition)
		{
			return delegate.where(condition);
		}

		/**
		 * Adds a compiled condition to this update sentence.
		 *
		 * @param condition condition to be added to the builder
		 * @return the same builder with the added condition
		 */
		public ClassUpdate<T>.Compiled.CompiledWhere where(CompiledCondition condition)
		{
			return delegate.where(condition);
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


		/**
		 * Builds the compiled SQL sentence for this update.
		 *
		 * @return compiled SQL sentence
		 */
		@Override
		public Sentence.Compiled build()
		{
			return delegate.build();
		}

		/**
		 * Returns the SQL text for the current update builder state.
		 *
		 * @return SQL text representation of this builder
		 */
		@Override
		public String toString()
		{
			return delegate.toString();
		}


		/**
		 * Conditional helper for compiled object update builders.
		 */
		public class When
		{
			/**
			 * Adds a new column/value pair if previous specified condition was true.
			 *
			 * @param property property reference used to resolve column and value
			 * @param <R>      property type
			 * @return the same builder with the added column/value pair
			 */
			public <R> Compiled set(PropertyReference<T, R> property)
			{
				return Compiled.this.set(property);
			}

			/**
			 * Adds a new column/value pair if previous specified condition was true,
			 * computing the value from the bound object.
			 *
			 * @param property  property reference used to resolve the target column
			 * @param extractor function used to compute the bound value from the source object
			 * @return the same builder with the added column/value pair
			 */
			public Compiled set(PropertyReference<T, ?> property, Function<T, ?> extractor)
			{
				return Compiled.this.set(property, extractor);
			}

			/**
			 * Adds new column/value pairs if previous specified condition was true.
			 *
			 * @param properties property references used to resolve columns and values
			 * @return the same builder with the added column/value pairs
			 */
			public Compiled set(List<PropertyReference<T, ?>> properties)
			{
				return Compiled.this.set(properties);
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
		 * Conditional helper for ignored branches on compiled object update builders.
		 */
		public class DisabledWhen extends When
		{
			/**
			 * Ignores the specified column/value pair and keeps current builder unchanged.
			 *
			 * @param property property reference used to resolve column and value
			 * @param <R>      property type
			 * @return the current compiled builder unchanged
			 */
			@Override
			public <R> Compiled set(PropertyReference<T, R> property)
			{
				return Compiled.this;
			}

			/**
			 * Ignores the specified computed column/value pair and keeps current builder unchanged.
			 *
			 * @param property  property reference used to resolve the target column
			 * @param extractor function that would compute the bound value
			 * @return the current compiled builder unchanged
			 */
			@Override
			public Compiled set(PropertyReference<T, ?> property, Function<T, ?> extractor)
			{
				return Compiled.this;
			}

			/**
			 * Ignores the specified column/value pairs and keeps current builder unchanged.
			 *
			 * @param properties property references used to resolve columns and values
			 * @return the current compiled builder unchanged
			 */
			@Override
			public Compiled set(List<PropertyReference<T, ?>> properties)
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
	 * Conditional helper for object update builders.
	 */
	public class When
	{
		/**
		 * Adds a new column/value pair if previous specified condition was true.
		 *
		 * @param property property reference used to resolve column and value
		 * @param <R>      property type
		 * @return compiled builder with the added column/value pair
		 */
		public <R> Compiled set(PropertyReference<T, R> property)
		{
			return compiled().set(property);
		}

		/**
		 * Adds a new column/value pair if previous specified condition was true,
		 * computing the value from the bound object.
		 *
		 * @param property  property reference used to resolve the target column
		 * @param extractor function used to compute the bound value from the source object
		 * @return compiled builder with the added column/value pair
		 */
		public Compiled set(PropertyReference<T, ?> property, Function<T, ?> extractor)
		{
			return compiled().set(property, extractor);
		}

		/**
		 * Adds new column/value pairs if previous specified condition was true.
		 *
		 * @param properties property references used to resolve columns and values
		 * @return compiled builder with the added column/value pairs
		 */
		public Compiled set(List<PropertyReference<T, ?>> properties)
		{
			return compiled().set(properties);
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
	 * Conditional helper for ignored branches on object update builders.
	 */
	public class DisabledWhen extends When
	{
		/**
		 * Ignores the specified column/value pair and returns an empty compiled builder.
		 *
		 * @param property property reference used to resolve column and value
		 * @param <R>      property type
		 * @return an empty compiled builder
		 */
		@Override
		public <R> Compiled set(PropertyReference<T, R> property)
		{
			return compiled();
		}

		/**
		 * Ignores the specified computed column/value pair and returns an empty compiled builder.
		 *
		 * @param property  property reference used to resolve the target column
		 * @param extractor function that would compute the bound value
		 * @return an empty compiled builder
		 */
		@Override
		public Compiled set(PropertyReference<T, ?> property, Function<T, ?> extractor)
		{
			return compiled();
		}

		/**
		 * Ignores the specified column/value pairs and returns an empty compiled builder.
		 *
		 * @param properties property references used to resolve columns and values
		 * @return an empty compiled builder
		 */
		@Override
		public Compiled set(List<PropertyReference<T, ?>> properties)
		{
			return compiled();
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