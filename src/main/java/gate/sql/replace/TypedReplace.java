package gate.sql.replace;

import gate.lang.property.Entity;
import gate.lang.property.Property;
import gate.sql.statement.Operation;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Replace SQL builder for a java class.
 *
 * 
 */
public class TypedReplace<T> implements Replace, Operation.Builder<T>
{

	private final Class<T> type;

	TypedReplace(Class<T> type)
	{
		this.type = type;
	}

	/**
	 * Adds a property to be persisted.
	 *
	 * @param property the new property to be persisted
	 *
	 * @return the same instance with the added property
	 */
	public Generic set(Property property)
	{
		return new Generic().set(property);
	}

	/**
	 * Adds properties to be persisted.
	 *
	 * @param properties the new properties to be persisted
	 *
	 * @return the same builder with the added properties
	 */
	public Generic set(List<Property> properties)
	{
		return new Generic().set(properties);
	}

	/**
	 * Adds a property to be persisted.
	 *
	 * @param property the new property to be persisted
	 *
	 * @return the same instance with the added property
	 */
	public Generic set(String property)
	{
		return new Generic().set(property);
	}

	/**
	 * Adds properties to be persisted.
	 *
	 * @param properties the new properties to be persisted
	 *
	 * @return the same builder with the added properties
	 */
	public Generic set(String... properties)
	{
		return new Generic().set(properties);
	}

	/**
	 * Creates an full replace sentence for the specified java class.
	 *
	 * @return full replace sentence for the specified java class
	 */
	@Override
	public Operation<T> build()
	{
		return set(Entity.getProperties(type, e -> !e.isEntityId())).build();
	}

	/**
	 * Properties of the builder.
	 */
	public class Generic implements Operation.Builder<T>
	{

		private final StringJoiner columns = new StringJoiner(", ", "(", ")");
		private final StringJoiner parameters = new StringJoiner(", ", "(", ")");
		private final List<Property> properties = new ArrayList<>();

		private Generic()
		{
		}

		/**
		 * Adds a property to be persisted.
		 *
		 * @param property the new property to be persisted
		 *
		 * @return the same instance with the added property
		 */
		public Generic set(Property property)
		{
			properties.add(property);
			property.getConverter()
					.getColumns(property.getColumnName())
					.peek(columns::add)
					.map(e -> "?")
					.forEach(parameters::add);
			return this;
		}

		/**
		 * Adds properties to be persisted.
		 *
		 * @param properties the new properties to be persisted
		 *
		 * @return the same instance with the added properties
		 */
		public Generic set(List<Property> properties)
		{
			properties.forEach(this::set);
			return this;
		}

		/**
		 * Adds a property to be persisted.
		 *
		 * @param property the new property to be persisted
		 *
		 * @return the same instance with the added property
		 */
		public Generic set(String property)
		{
			return set(Property.getProperty(type, property));
		}

		/**
		 * Adds properties to be persisted.
		 *
		 * @param properties the new properties to be persisted
		 *
		 * @return the same instance with the added properties
		 */
		public Generic set(String... properties)
		{
			return set(Property.getProperties(type, properties));
		}

		/**
		 * Creates a Sentence to be executed on the database
		 *
		 * @return The Sentence to be executed on the database
		 */
		@Override
		public Operation<T> build()
		{
			return Operation.of(type, properties, toString());
		}

		/**
		 * Returns the SQL string to be executed on the database
		 *
		 * @return the SQL string to be executed on the database
		 */
		@Override
		public String toString()
		{
			return "replace into " + Entity.getFullTableName(type) + " " + columns + " values " + parameters;
		}
	}
}
