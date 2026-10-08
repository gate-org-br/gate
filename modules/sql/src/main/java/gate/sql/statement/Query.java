package gate.sql.statement;

import gate.sql.Command;
import gate.sql.Fetchable;
import gate.sql.Link;
import gate.sql.SQLBuilder;
import gate.sql.fetcher.Fetcher;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;

public interface Query extends SQL, Compilable
{

	static Query of(String sql)
	{
		return new BasicQuery(sql);
	}

	static Compiled of(String sql, List<Object> parameters)
	{
		return Query.of(sql).parameters(parameters);
	}

	static Compiled of(String sql, Object... parameters)
	{
		return Query.of(sql).parameters(parameters);
	}

	Connected connect(Link link);

	@Override
	Compiled parameters(List<Object> parameters);

	@Override
	Compiled parameters(Object... parameters);

	@Override
	Constant constant();

	@Override
	Query print(Logger logger);

	@Override
	Query print();

	@Override
	String toString();

	interface Compiled extends SQL
	{

		Connected connect(Link connection);

		@Override
		Compiled print(Logger logger);

		@Override
		Compiled print();

		@Override
		String toString();

		interface Connected extends SQL, Fetchable
		{

			Command createCommand();

			@Override
			<T> T fetch(Fetcher<T> fetcher);

			@Override
			Connected print(Logger logger);

			@Override
			Connected print();

			@Override
			String toString();
		}

		interface Builder extends SQLBuilder<Query.Compiled>
		{

			@Override
			Query.Compiled build();

			@FunctionalInterface
			interface Supplier
			{
				Builder get();
			}
		}

		@FunctionalInterface
		interface Supplier
		{
			Compiled get();
		}
	}

	interface Constant extends SQL
	{

		Connected connect(Link connection);

		@Override
		Constant print(Logger logger);

		@Override
		Constant print();

		@Override
		String toString();

		interface Connected extends SQL, Fetchable
		{

			Command createCommand();

			@Override
			<T> T fetch(Fetcher<T> fetcher);

			@Override
			Connected print(Logger logger);

			@Override
			Connected print();

			@Override
			String toString();
		}

		interface Builder extends SQLBuilder<Query.Constant>
		{

			@Override
			Query.Constant build();
		}
	}

	interface Connected extends SQL, Compilable
	{

		@Override
		Compiled parameters(List<Object> parameters);

		@Override
		default Compiled parameters(Object... parameters)
		{
			return Connected.this.parameters(Arrays.asList(parameters));
		}

		@Override
		Constant constant();

		@Override
		Connected print(Logger logger);

		@Override
		Connected print();

		@Override
		String toString();

		interface Compiled extends SQL, Fetchable
		{

			Command createCommand();

			@Override
			<T> T fetch(Fetcher<T> fetcher);

			@Override
			Compiled print(Logger logger);

			@Override
			Compiled print();

			@Override
			String toString();
		}

		interface Constant extends SQL, Fetchable
		{

			Command createCommand();

			@Override
			<T> T fetch(Fetcher<T> fetcher);

			@Override
			Constant print(Logger logger);

			@Override
			Constant print();

			@Override
			String toString();
		}
	}

	interface Builder extends SQLBuilder<Query>
	{

		@Override
		Query build();

		@FunctionalInterface
		interface Supplier
		{
			Builder get();
		}
	}

	@FunctionalInterface
	interface Supplier
	{
		Query get();
	}
}
