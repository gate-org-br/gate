package gate.sql.statement;

import gate.error.ConstraintViolationException;
import gate.lang.property.Entity;
import gate.lang.property.Property;
import gate.sql.Command;
import gate.sql.Link;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A Sentence associated with a java type a and a list of properties
 *
 *
 */
class BasicOperation<T> implements Operation<T>
{

	private final String sql;
	private final Class<T> type;
	private final List<Property> properties;

	BasicOperation(Class<T> type, List<Property> properties, String sql)
	{
		Objects.requireNonNull(type);
		Objects.requireNonNull(properties);
		Objects.requireNonNull(sql);

		this.sql = sql;
		this.type = type;
		this.properties = properties;
	}

	@Override
	public Operation.Compiled<T> values(Collection<T> values)
	{
		return new Compiled(values);
	}

	@Override
	public Operation.Compiled<T> value(T value)
	{
		return new Compiled(Collections.singleton(value));
	}

	@Override
	public Operation.Connected<T> connect(Link connection)
	{
		return new Connected(connection);
	}

	@Override
	public String toString()
	{
		return sql;
	}

	@Override
	public Operation<T> print()
	{
		System.out.println(toString());
		return this;
	}

	public class Compiled implements Operation.Compiled<T>
	{

		private final Collection<T> values;

		public Compiled(Collection<T> values)
		{
			Objects.requireNonNull(values);

			this.values = values;
		}

		@Override
		public Connected connect(Link connection)
		{
			return new Connected(connection);
		}

		@Override
		public String toString()
		{
			return sql;
		}

		@Override
		public Compiled print()
		{
			System.out.println(toString());
			return this;
		}

		public class Connected implements Operation.Compiled.Connected<T>
		{

			private final Link link;

			public Connected(Link connection)
			{
				Objects.requireNonNull(connection);

				this.link = connection;
			}

			@Override
			public int execute()
				throws ConstraintViolationException
			{
				try (Command command = link.createCommand(sql))
				{
					int result = 0;

					if (Entity.isEntity(type))
					{
						Property entityId
							= Property.getProperty(type, Entity.getId(type));

						for (T value : values)
						{
							properties.forEach(e -> command.setParameter(e.getRawType(), e.getValue(value)));
							result += command.execute();
							if (entityId.getValue(value) == null)
								command
									.getGeneratedKey(entityId.getRawType())
									.ifPresent(id -> entityId.setValue(value, id));
						}
					} else
					{
						for (T value : values)
						{
							properties.forEach(e -> command.setParameter(e.getRawType(), e.getValue(value)));
							result += command.execute();
						}
					}

					return result;
				}
			}

			@Override
			public Operation.Compiled.Connected<T> observe(Consumer<T> observer)
			{
				return observer != null ? new Observed(observer) : this;
			}

			@Override
			public String toString()
			{
				return sql;
			}

			@Override
			public Connected print()
			{
				System.out.println(toString());
				return this;
			}

			public class Observed implements Operation.Compiled.Connected<T>
			{

				private final Consumer<T> observer;

				public Observed(Consumer<T> observer)
				{
					Objects.requireNonNull(observer);
					this.observer = observer;
				}

				@Override
				public int execute()
					throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						int result = 0;

						if (Entity.isEntity(type))
						{
							Property entityId
								= Property.getProperty(type, Entity.getId(type));

							for (T value : values)
							{
								properties.forEach(e -> command.setParameter(e.getRawType(), e.getValue(value)));
								result += command.execute();
								if (entityId.getValue(value) == null)
									command
										.getGeneratedKey(entityId.getRawType())
										.ifPresent(id -> entityId.setValue(value, id));
								observer.accept(value);
							}
						} else
						{
							for (T value : values)
							{
								properties.forEach(e -> command.setParameter(e.getRawType(), e.getValue(value)));
								result += command.execute();
								observer.accept(value);
							}
						}

						return result;
					}
				}

				@Override
				public Operation.Compiled.Connected<T> observe(Consumer<T> observer)
					throws ConstraintViolationException
				{
					return Connected.this.observe(observer);
				}

				@Override
				public String toString()
				{
					return sql;
				}

				@Override
				public Operation.Compiled.Connected print()
				{
					System.out.println(toString());
					return this;
				}
			}
		}
	}

	public class Connected implements Operation.Connected<T>
	{

		private final Link link;

		public Connected(Link connection)
		{
			Objects.requireNonNull(connection);

			this.link = connection;
		}

		@Override
		public Operation.Connected.Compiled<T> values(Collection<T> values)
		{
			return new Compiled(values);
		}

		@Override
		public Operation.Connected.Compiled<T> value(T value)
		{
			return new Compiled(Collections.singleton(value));
		}

		@Override
		public String toString()
		{
			return sql;
		}

		@Override
		public Operation.Connected<T> print()
		{
			System.out.println(toString());
			return this;
		}

		public class Compiled implements Operation.Connected.Compiled<T>
		{

			private final Collection<T> values;

			public Compiled(Collection<T> values)
			{
				Objects.requireNonNull(values);

				this.values = values;
			}

			@Override
			public int execute()
				throws ConstraintViolationException
			{
				try (Command command = link.createCommand(sql))
				{
					int result = 0;

					if (Entity.isEntity(type))
					{
						Property entityId = Property.getProperty(type, Entity.getId(type));

						for (T value : values)
						{
							properties.forEach(e -> command.setParameter(e.getRawType(), e.getValue(value)));
							result += command.execute();
							command.getGeneratedKey(entityId.getRawType()).ifPresent(id -> entityId.setValue(value, id));
						}
					} else
					{
						for (T value : values)
						{
							properties.forEach(e -> command.setParameter(e.getRawType(), e.getValue(value)));
							result += command.execute();
						}
					}

					return result;
				}
			}

			@Override
			public Operation.Connected.Compiled<T> observe(Consumer<T> observer)
			{
				return observer != null ? new Observed(observer) : this;
			}

			@Override
			public String toString()
			{
				return sql;
			}

			@Override
			public Operation.Connected.Compiled print()
			{
				System.out.println(toString());
				System.out.println(values);
				return this;
			}

			public class Observed implements Operation.Connected.Compiled<T>
			{

				private final Consumer<T> observer;

				public Observed(Consumer<T> observer)
				{
					Objects.requireNonNull(observer);
					this.observer = observer;
				}

				@Override
				public int execute()
					throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						int result = 0;

						if (Entity.isEntity(type))
						{
							Property entityId
								= Property.getProperty(type, Entity.getId(type));

							for (T value : values)
							{
								properties.forEach(e -> command.setParameter(e.getRawType(), e.getValue(value)));
								result += command.execute();
								if (entityId.getValue(value) == null)
									command
										.getGeneratedKey(entityId.getRawType())
										.ifPresent(id -> entityId.setValue(value, id));
								observer.accept(value);
							}
						} else
						{
							for (T value : values)
							{
								properties.forEach(e -> command.setParameter(e.getRawType(), e.getValue(value)));
								result += command.execute();
								observer.accept(value);
							}
						}

						return result;
					}
				}

				@Override
				public Operation.Connected.Compiled<T> observe(Consumer<T> observer)
					throws ConstraintViolationException
				{
					return Compiled.this.observe(observer);
				}

				@Override
				public String toString()
				{
					return sql;
				}

				@Override
				public Operation.Connected.Compiled<T> print()
				{
					System.out.println(toString());
					return this;
				}
			}

		}
	}
}
