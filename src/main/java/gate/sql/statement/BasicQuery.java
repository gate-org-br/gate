package gate.sql.statement;

import gate.error.AppError;
import gate.error.ConversionException;
import gate.sql.Command;
import gate.sql.Cursor;
import gate.sql.Link;
import gate.sql.fetcher.Fetcher;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class BasicQuery implements Query
{

	private final String sql;

	BasicQuery(String sql)
	{
		this.sql = sql;
	}

	@Override
	public Query.Compiled parameters(List<Object> parameters)
	{
		return new Compiled(parameters);
	}

	@Override
	public Query.Compiled parameters(Object... parameters)
	{
		return new Compiled(Arrays.asList(parameters));
	}

	@Override
	public Query.Constant constant()
	{
		return new Constant();
	}

	@Override
	public Connected connect(Link link)
	{
		return new Connected(link);
	}

	@Override
	public String toString()
	{
		return sql;
	}

	@Override
	public Query print()
	{
		System.out.println(toString());
		return this;
	}

	private class Constant implements Query.Constant
	{

		Constant()
		{
		}

		@Override
		public Query.Constant.Connected connect(Link connection)
		{
			return new Connected(connection);
		}

		@Override
		public List<Object> getParameters()
		{
			return Collections.emptyList();
		}

		@Override
		public Query.Constant print()
		{
			System.out.println(toString());
			return this;
		}

		@Override
		public String toString()
		{
			return BasicQuery.this.toString();
		}

		private class Connected implements Query.Constant.Connected
		{

			private final Link link;

			public Connected(Link link)
			{
				this.link = link;
			}

			@Override
			public Command createCommand()
			{
				return link.createCommand(sql);
			}

			@Override
			public <T> T fetch(Fetcher<T> fetcher)
			{
				try (Command command = createCommand())
				{
					try (Cursor cursor = command.getCursor())
					{
						return fetcher.fetch(cursor);
					}
				} catch (ConversionException | SQLException e)
				{
					throw new AppError(e);
				}
			}

			@Override
			public Query.Constant.Connected print()
			{
				System.out.println(toString());
				return this;
			}

			@Override
			public String toString()
			{
				return Constant.this.toString();
			}
		}
	}

	private class Compiled implements Query.Compiled
	{

		private final List<Object> parameters;

		Compiled(List<Object> parameters)
		{
			this.parameters = parameters;
		}

		@Override
		public Query.Compiled.Connected connect(Link connection)
		{
			return new Connected(connection);
		}

		@Override
		public List<Object> getParameters()
		{
			return Collections.unmodifiableList(parameters);
		}

		@Override
		public Query.Compiled print()
		{
			System.out.println(toString());
			System.out.println(parameters);
			return this;
		}

		@Override
		public String toString()
		{
			return BasicQuery.this.toString();
		}

		private class Connected implements Query.Compiled.Connected
		{

			private final Link link;

			public Connected(Link link)
			{
				this.link = link;
			}

			@Override
			public Command createCommand()
			{
				return link.createCommand(toString())
						.setParameters(parameters);
			}

			@Override
			public <T> T fetch(Fetcher<T> fetcher)
			{
				try (Command command = createCommand())
				{
					try (Cursor cursor = command.getCursor())
					{
						return fetcher.fetch(cursor);
					}
				} catch (ConversionException | SQLException e)
				{
					throw new AppError(e);
				}
			}

			@Override
			public Query.Compiled.Connected print()
			{
				System.out.println(toString());
				System.out.println(parameters);
				return this;
			}

			@Override
			public String toString()
			{
				return Compiled.this.toString();
			}
		}
	}

	private class Connected implements Query.Connected
	{

		private final Link link;

		public Connected(Link link)
		{
			this.link = link;
		}

		@Override
		public Query.Connected.Compiled parameters(List<Object> parameters)
		{
			return new Compiled(parameters);
		}

		@Override
		public Constant constant()
		{
			return new Constant();
		}

		@Override
		public Query.Connected print()
		{
			System.out.println(toString());
			return this;
		}

		@Override
		public String toString()
		{
			return BasicQuery.this.toString();
		}

		private class Compiled implements Query.Connected.Compiled
		{

			private final List<Object> parameters;

			Compiled(List<Object> parameters)
			{
				this.parameters = parameters;
			}

			@Override
			public Command createCommand()
			{
				return link.createCommand(sql)
						.setParameters(parameters);
			}

			@Override
			public <T> T fetch(Fetcher<T> fetcher)
			{
				try (Command command = createCommand())
				{
					try (Cursor cursor = command.getCursor())
					{
						return fetcher.fetch(cursor);
					}
				} catch (ConversionException | SQLException e)
				{
					throw new AppError(e);
				}
			}

			@Override
			public List<Object> getParameters()
			{
				return Collections.unmodifiableList(parameters);
			}

			@Override
			public Query.Connected.Compiled print()
			{
				System.out.println(toString());
				System.out.println(parameters);
				return this;
			}

			@Override
			public String toString()
			{
				return Connected.this.toString();
			}
		}

		private class Constant implements Query.Connected.Constant
		{

			Constant()
			{
			}

			@Override
			public Command createCommand()
			{
				return link.createCommand(sql);
			}

			@Override
			public <T> T fetch(Fetcher<T> fetcher)
			{
				try (Command command = createCommand())
				{
					try (Cursor cursor = command.getCursor())
					{
						return fetcher.fetch(cursor);
					}
				} catch (ConversionException | SQLException e)
				{
					throw new AppError(e);
				}
			}

			@Override
			public List<Object> getParameters()
			{
				return Collections.emptyList();
			}

			@Override
			public Query.Connected.Constant print()
			{
				System.out.println(toString());
				return this;
			}

			@Override
			public String toString()
			{
				return Connected.this.toString();
			}
		}
	}
}
