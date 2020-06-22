package gate.sql.statement;

import gate.error.ConstraintViolationException;
import gate.sql.Link;
import gate.sql.insert.Insert;
import java.util.Collection;
import java.util.function.Consumer;

public class BasicInsertOperation<T> implements InsertOperation<T>
{

	private final Link link;
	private final Class<T> type;

	public BasicInsertOperation(Link link, Class<T> type)
	{
		this.link = link;
		this.type = type;
	}

	@Override
	public InsertOperation<T> observe(Consumer<T> observer)
	{
		return observer != null ? new Observed(observer) : this;
	}

	@Override
	public int execute(Collection<T> values)
		throws ConstraintViolationException
	{
		return Insert.into(type).build().values(values).connect(link).execute();
	}

	@Override
	public InsertOperation.Properties<T> properties(String... properties)
	{
		return new Properties(properties);
	}

	public class Observed implements InsertOperation<T>
	{

		private final Consumer<T> observer;

		public Observed(Consumer<T> observer)
		{
			this.observer = observer;
		}

		@Override
		public InsertOperation observe(Consumer<T> observer)
		{
			return BasicInsertOperation.this.observe(observer);
		}

		@Override
		public int execute(Collection<T> values) throws ConstraintViolationException
		{
			return Insert.into(type).build().values(values).connect(link)
				.observe(observer)
				.execute();
		}

		@Override
		public Properties<T> properties(String... properties)
		{
			return new BasicInsertOperation.Properties(properties).observe(observer);
		}

	}

	public class Properties implements InsertOperation.Properties<T>
	{

		private final String[] properties;

		public Properties(String[] properties)
		{
			this.properties = properties;
		}

		@Override
		public InsertOperation.Properties observe(Consumer<T> observer)
		{
			return observer != null ? new Observed(observer) : this;
		}

		@Override
		public int execute(Collection<T> values) throws ConstraintViolationException
		{
			return Insert.into(type)
				.set(properties)
				.build().values(values)
				.connect(link)
				.execute();
		}

		public class Observed implements InsertOperation.Properties<T>
		{

			private final Consumer<T> observer;

			public Observed(Consumer<T> observer)
			{
				this.observer = observer;
			}

			@Override
			public InsertOperation.Properties observe(Consumer<T> observer)
			{
				return Properties.this.observe(observer);
			}

			@Override
			public int execute(Collection<T> values) throws ConstraintViolationException
			{
				return Insert
					.into(type)
					.set(properties)
					.build()
					.values(values)
					.connect(link)
					.observe(observer)
					.execute();
			}

		}
	}

}
