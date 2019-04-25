package gate.sql.statement;

import gate.error.ConstraintViolationException;
import gate.sql.Command;
import gate.sql.Link;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

class BasicSentence implements Sentence
{

	private final String sql;

	BasicSentence(String sql)
	{
		this.sql = sql;
	}

	@Override
	public Sentence.Connected connect(Link link)
	{
		return new Connected(link);
	}

	@Override
	public Sentence.Compiled parameters(List<Object> parameters)
	{
		return new Compiled(Arrays.asList(parameters));
	}

	@Override
	public Sentence.Compiled batch(List<List<Object>> parameters)
	{
		return new Compiled(parameters);
	}

	@Override
	public String toString()
	{
		return sql;
	}

	@Override
	public Sentence print()
	{
		System.out.println(toString());
		return this;
	}

	@Override
	public <T> Prepared<T> entities(Collection<T> entities)
	{
		return new Prepared<>(entities);
	}

	private class Prepared<T> implements Sentence.Prepared<T>
	{

		private final Collection<T> entities;

		public Prepared(Collection<T> entities)
		{
			this.entities = entities;
		}

		@Override
		public String toString()
		{
			return sql;
		}

		@Override
		public Compiled parameters(List<Function<T, ?>> extractors)
		{
			return new Compiled(extractors);
		}

		@Override
		public Prepared<T> print()
		{
			System.out.println(toString());
			System.out.println(entities);
			return this;
		}

		private class Compiled implements Sentence.Prepared.Compiled<T>
		{

			private final List<Function<T, ?>> extractors;

			public Compiled(List<Function<T, ?>> extractors)
			{
				this.extractors = extractors;
			}

			@Override
			public Connected connect(Link link)
			{
				return new Connected(link);
			}

			@Override
			public SQL print()
			{
				System.out.println(toString());
				System.out.println(entities);
				System.out.println(extractors);
				return this;
			}

			private class Connected implements Sentence.Prepared.Compiled.Connected<T>
			{

				private final Link link;

				private Connected(Link link)
				{
					this.link = link;
				}

				@Override
				public int execute() throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						int count = 0;
						for (T entity : entities)
						{
							extractors.forEach(e -> command.setParameter(e.apply(entity)));
							count += command.execute();
						}
						return count;
					}
				}

				@Override
				public <K> List<K> fetchGeneratedKeys(Class<K> type) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						List<K> generatedKeys = new ArrayList<>();
						for (T entity : entities)
						{
							extractors.forEach(e -> command.setParameter(e.apply(entity)));
							command.execute(type).ifPresent(generatedKeys::add);
						}
						return generatedKeys;
					}
				}

				@Override
				public <K> void fetchGeneratedKeys(Class<K> type, BiConsumer<T, K> consumer) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						for (T entity : entities)
						{
							extractors.forEach(e -> command.setParameter(e.apply(entity)));
							command.execute(type).ifPresent(e -> consumer.accept(entity, e));
						}
					}
				}

				@Override
				public <K> List<List<K>> fetchGeneratedKeyLists(Class<K> type) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						List<List<K>> generatedKeys = new ArrayList<>();
						for (T entity : entities)
						{
							extractors.forEach(e -> command.setParameter(e.apply(entity)));
							command.execute();
							generatedKeys.add(command.getGeneratedKeys(type));
						}
						return generatedKeys;
					}
				}

				@Override
				public <K> void fetchGeneratedKeyLists(Class<K> type, BiConsumer<T, List<K>> consumer) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						for (T entity : entities)
						{
							extractors.forEach(e -> command.setParameter(e.apply(entity)));
							command.execute();
							consumer.accept(entity, command.getGeneratedKeys(type));
						}
					}
				}

				@Override
				public Sentence.Prepared.Compiled.Connected<T> observe(Consumer<T> consumer)
				{
					return consumer != null
						? new Observed(consumer) : this;
				}

				@Override
				public Connected print()
				{
					System.out.println(toString());
					System.out.println(entities);
					System.out.println(extractors);
					return this;
				}

				public class Observed implements Sentence.Prepared.Compiled.Connected<T>
				{

					private final Consumer<T> observer;

					public Observed(Consumer<T> observer)
					{
						this.observer = observer;
					}

					@Override
					public int execute() throws ConstraintViolationException
					{
						try (Command command = link.createCommand(sql))
						{
							int count = 0;
							for (T entity : entities)
							{
								extractors.forEach(e -> command.setParameter(e.apply(entity)));
								count += command.execute();
								observer.accept(entity);
							}
							return count;
						}
					}

					@Override
					public <K> List<K> fetchGeneratedKeys(Class<K> type) throws ConstraintViolationException
					{
						try (Command command = link.createCommand(sql))
						{
							List<K> generatedKeys = new ArrayList<>();
							for (T entity : entities)
							{
								extractors.forEach(e -> command.setParameter(e.apply(entity)));
								command.execute(type).ifPresent(generatedKeys::add);
								observer.accept(entity);
							}
							return generatedKeys;
						}
					}

					@Override
					public <K> void fetchGeneratedKeys(Class<K> type, BiConsumer<T, K> consumer) throws ConstraintViolationException
					{
						try (Command command = link.createCommand(sql))
						{
							for (T entity : entities)
							{
								extractors.forEach(e -> command.setParameter(e.apply(entity)));
								command.execute(type).ifPresent(e -> consumer.accept(entity, e));
								observer.accept(entity);
							}
						}
					}

					@Override
					public <K> List<List<K>> fetchGeneratedKeyLists(Class<K> type) throws ConstraintViolationException
					{
						try (Command command = link.createCommand(sql))
						{
							List<List<K>> generatedKeys = new ArrayList<>();
							for (T entity : entities)
							{
								extractors.forEach(e -> command.setParameter(e.apply(entity)));
								command.execute();
								generatedKeys.add(command.getGeneratedKeys(type));
								observer.accept(entity);
							}
							return generatedKeys;
						}
					}

					@Override
					public <K> void fetchGeneratedKeyLists(Class<K> type, BiConsumer<T, List<K>> consumer) throws ConstraintViolationException
					{
						try (Command command = link.createCommand(sql))
						{
							for (T entity : entities)
							{
								extractors.forEach(e -> command.setParameter(e.apply(entity)));
								command.execute();
								consumer.accept(entity, command.getGeneratedKeys(type));
								observer.accept(entity);
							}
						}
					}

					@Override
					public Sentence.Prepared.Compiled.Connected<T>
						observe(Consumer<T> consumer)
					{
						return Connected.this.observe(consumer);
					}

					@Override
					public String toString()
					{
						return Connected.this.toString();
					}

					@Override
					public Sentence.Prepared.Compiled.Connected<T> print()
					{
						return Connected.this.print();
					}
				}
			}
		}
	}

	private class Connected implements Sentence.Connected
	{

		private final Link link;

		private Connected(Link link)
		{
			this.link = link;
		}

		@Override
		public Sentence.Connected.Compiled parameters(List<Object> parameters)
		{
			return new Compiled(Arrays.asList(parameters));
		}

		@Override
		public Sentence.Connected.Compiled batch(List<List<? extends Object>> batch)
		{
			return new Compiled(batch);
		}

		@Override
		public String toString()
		{
			return sql;
		}

		@Override
		public Sentence.Connected print()
		{
			System.out.println(toString());
			return this;
		}

		@Override
		public <T> Prepared<T> entities(List<T> entities)
		{
			return new Prepared<>(entities);
		}

		@Override
		public int execute() throws ConstraintViolationException
		{
			return batch(Collections.singletonList(Collections.emptyList())).execute();
		}

		@Override
		public <K> List<K> fetchGeneratedKeys(Class<K> type) throws ConstraintViolationException
		{
			return parameters(Collections.singletonList(Collections.emptyList())).fetchGeneratedKeys(type);
		}

		@Override
		public <K> void fetchGeneratedKeys(Class<K> type, Consumer<K> consumer) throws ConstraintViolationException
		{
			parameters(Collections.singletonList(Collections.emptyList())).fetchGeneratedKeys(type, consumer);
		}

		@Override
		public <K> List<List<K>> fetchGeneratedKeyLists(Class<K> type) throws ConstraintViolationException
		{
			return parameters(Collections.singletonList(Collections.emptyList())).fetchGeneratedKeyLists(type);
		}

		@Override
		public <K> void fetchGeneratedKeyLists(Class<K> type, Consumer<List<K>> consumer) throws ConstraintViolationException
		{
			parameters(Collections.singletonList(Collections.emptyList())).fetchGeneratedKeyLists(type, consumer);
		}

		private class Compiled implements Sentence.Connected.Compiled
		{

			private final List<List<? extends Object>> batch;

			public Compiled(List<List<? extends Object>> batch)
			{
				this.batch = batch;
			}

			@Override
			public int execute() throws ConstraintViolationException
			{
				try (Command command = link.createCommand(sql))
				{
					int count = 0;
					for (List<? extends Object> parameters : batch)
						count += command.setParameters(parameters).execute();
					return count;
				}
			}

			@Override
			public <K> List<K> fetchGeneratedKeys(Class<K> type) throws ConstraintViolationException
			{
				try (Command command = link.createCommand(sql))
				{
					List<K> generatedKeys = new ArrayList<>();
					for (List<? extends Object> parameters : batch)
						command.setParameters(parameters).execute(type).ifPresent(generatedKeys::add);
					return generatedKeys;
				}
			}

			@Override
			public <K> void fetchGeneratedKeys(Class<K> type, Consumer<K> consumer) throws ConstraintViolationException
			{
				try (Command command = link.createCommand(sql))
				{
					for (List<? extends Object> parameters : batch)
						command.setParameters(parameters).execute(type).ifPresent(consumer);
				}
			}

			@Override
			public <K> List<List<K>> fetchGeneratedKeyLists(Class<K> type) throws ConstraintViolationException
			{
				try (Command command = link.createCommand(sql))
				{
					List<List<K>> generatedKeys = new ArrayList<>();
					for (List<? extends Object> parameters : batch)
					{
						command.setParameters(parameters).execute();
						generatedKeys.add(command.getGeneratedKeys(type));
					}
					return generatedKeys;
				}
			}

			@Override
			public <K> void fetchGeneratedKeyLists(Class<K> type, Consumer<List<K>> consumer) throws ConstraintViolationException
			{
				try (Command command = link.createCommand(sql))
				{
					for (List<? extends Object> parameters : batch)
					{
						command.setParameters(parameters).execute();
						consumer.accept(command.getGeneratedKeys(type));
					}
				}
			}

			@Override
			public Sentence.Connected.Compiled observe(Consumer<List<? extends Object>> consumer)
			{
				return consumer != null
					? new Observed(consumer) : this;
			}

			@Override
			public String toString()
			{
				return sql;
			}

			@Override
			public Sentence.Connected.Compiled print()
			{
				System.out.println(toString());
				System.out.println(batch);
				return this;
			}

			private class Observed implements Sentence.Connected.Compiled
			{

				private final Consumer<List<? extends Object>> observer;

				public Observed(Consumer<List<? extends Object>> observer)
				{
					this.observer = observer;
				}

				@Override
				public int execute() throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						int count = 0;
						for (List<? extends Object> parameters : batch)
						{
							count += command.setParameters(parameters).execute();
							observer.accept(parameters);
						}
						return count;
					}
				}

				@Override
				public <T> List<T> fetchGeneratedKeys(Class<T> type) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						List<T> generatedKeys = new ArrayList<>();
						for (List<? extends Object> parameters : batch)
						{
							command.setParameters(parameters).execute(type).ifPresent(generatedKeys::add);
							observer.accept(parameters);
						}
						return generatedKeys;
					}
				}

				@Override
				public <K> void fetchGeneratedKeys(Class<K> type, Consumer<K> consumer) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						for (List<? extends Object> parameters : batch)
						{
							command.setParameters(parameters).execute(type).ifPresent(consumer);
							observer.accept(parameters);
						}
					}
				}

				@Override
				public <K> List<List<K>> fetchGeneratedKeyLists(Class<K> type) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						List<List<K>> generatedKeys = new ArrayList<>();
						for (List<? extends Object> parameters : batch)
						{
							command.setParameters(parameters).execute();
							generatedKeys.add(command.getGeneratedKeys(type));
							observer.accept(parameters);
						}
						return generatedKeys;
					}
				}

				@Override
				public <K> void fetchGeneratedKeyLists(Class<K> type, Consumer<List<K>> consumer) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						for (List<? extends Object> parameters : batch)
						{
							command.setParameters(parameters).execute();
							consumer.accept(command.getGeneratedKeys(type));
							observer.accept(parameters);
						}
					}
				}

				@Override
				public Sentence.Connected.Compiled observe(Consumer<List<? extends Object>> consumer)
				{
					return Compiled.this.observe(consumer);
				}

				@Override
				public String toString()
				{
					return Compiled.this.toString();
				}

				@Override
				public Sentence.Connected.Compiled print()
				{
					return Compiled.this.print();
				}
			}
		}

		private class Prepared<T> implements Sentence.Connected.Prepared<T>
		{

			private final List<T> entities;

			public Prepared(List<T> entities)
			{
				this.entities = entities;
			}

			@Override
			public String toString()
			{
				return sql;
			}

			@Override
			public Compiled parameters(List<Function<T, ?>> extractors)
			{
				return new Compiled(extractors);
			}

			@Override
			public Prepared<T> print()
			{
				System.out.println(toString());
				System.out.println(entities);
				return this;
			}

			public class Compiled implements Sentence.Connected.Prepared.Compiled<T>
			{

				private final List<Function<T, ?>> extractors;

				public Compiled(List<Function<T, ?>> extractors)
				{
					this.extractors = extractors;
				}

				@Override
				public int execute() throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						int count = 0;
						for (T entity : entities)
						{
							extractors.forEach(e -> command.setParameter(e.apply(entity)));
							count += command.execute();
						}
						return count;
					}
				}

				@Override
				public <K> List<K> fetchGeneratedKeys(Class<K> type) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						List<K> generatedKeys = new ArrayList<>();
						for (T entity : entities)
						{
							extractors.forEach(e -> command.setParameter(e.apply(entity)));
							command.execute(type).ifPresent(generatedKeys::add);
						}
						return generatedKeys;
					}
				}

				@Override
				public <K> void fetchGeneratedKeys(Class<K> type, BiConsumer<T, K> consumer) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						for (T entity : entities)
						{
							extractors.forEach(e -> command.setParameter(e.apply(entity)));
							command.execute(type).ifPresent(e -> consumer.accept(entity, e));
						}
					}
				}

				@Override
				public <K> List<List<K>> fetchGeneratedKeyLists(Class<K> type) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						List<List<K>> generatedKeys = new ArrayList<>();
						for (T entity : entities)
						{
							extractors.forEach(e -> command.setParameter(e.apply(entity)));
							command.execute();
							generatedKeys.add(command.getGeneratedKeys(type));
						}
						return generatedKeys;
					}
				}

				@Override
				public <K> void fetchGeneratedKeyLists(Class<K> type, BiConsumer<T, List<K>> consumer) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						for (T entity : entities)
						{
							extractors.forEach(e -> command.setParameter(e.apply(entity)));
							command.execute();
							consumer.accept(entity, command.getGeneratedKeys(type));
						}
					}
				}

				@Override
				public Sentence.Connected.Prepared.Compiled<T> observe(Consumer<T> consumer)
				{
					return consumer != null
						? new Observed(consumer) : this;
				}

				@Override
				public Compiled print()
				{
					System.out.println(toString());
					System.out.println(entities);
					System.out.println(extractors);
					return this;
				}

				public class Observed implements Sentence.Connected.Prepared.Compiled<T>
				{

					private final Consumer<T> observer;

					public Observed(Consumer<T> observer)
					{
						this.observer = observer;
					}

					@Override
					public int execute() throws ConstraintViolationException
					{
						try (Command command = link.createCommand(sql))
						{
							int count = 0;
							for (T entity : entities)
							{
								extractors.forEach(e -> command.setParameter(e.apply(entity)));
								count += command.execute();
								observer.accept(entity);
							}
							return count;
						}
					}

					@Override
					public <K> List<K> fetchGeneratedKeys(Class<K> type) throws ConstraintViolationException
					{
						try (Command command = link.createCommand(sql))
						{
							List<K> generatedKeys = new ArrayList<>();
							for (T entity : entities)
							{
								extractors.forEach(e -> command.setParameter(e.apply(entity)));
								command.execute(type).ifPresent(generatedKeys::add);
								observer.accept(entity);
							}
							return generatedKeys;
						}
					}

					@Override
					public <K> void fetchGeneratedKeys(Class<K> type, BiConsumer<T, K> consumer) throws ConstraintViolationException
					{
						try (Command command = link.createCommand(sql))
						{
							for (T entity : entities)
							{
								extractors.forEach(e -> command.setParameter(e.apply(entity)));
								command.execute(type).ifPresent(e -> consumer.accept(entity, e));
								observer.accept(entity);
							}
						}
					}

					@Override
					public <K> List<List<K>> fetchGeneratedKeyLists(Class<K> type) throws ConstraintViolationException
					{
						try (Command command = link.createCommand(sql))
						{
							List<List<K>> generatedKeys = new ArrayList<>();
							for (T entity : entities)
							{
								extractors.forEach(e -> command.setParameter(e.apply(entity)));
								command.execute();
								generatedKeys.add(command.getGeneratedKeys(type));
								observer.accept(entity);
							}
							return generatedKeys;
						}
					}

					@Override
					public <K> void fetchGeneratedKeyLists(Class<K> type, BiConsumer<T, List<K>> consumer) throws ConstraintViolationException
					{
						try (Command command = link.createCommand(sql))
						{
							for (T entity : entities)
							{
								extractors.forEach(e -> command.setParameter(e.apply(entity)));
								command.execute();
								consumer.accept(entity, command.getGeneratedKeys(type));
								observer.accept(entity);
							}
						}
					}

					@Override
					public Sentence.Connected.Prepared.Compiled<T> observe(Consumer<T> consumer)
					{
						return Compiled.this.observe(consumer);
					}

					@Override
					public String toString()
					{
						return Compiled.this.toString();
					}

					@Override
					public Sentence.Connected.Prepared.Compiled<T> print()
					{
						return Compiled.this.print();
					}
				}
			}
		}

	}

	private class Compiled implements Sentence.Compiled
	{

		private final List<List<Object>> batch;

		public Compiled(List<List<Object>> batch)
		{
			this.batch = batch;
		}

		@Override
		public Sentence.Compiled.Connected connect(Link link)
		{
			return new Connected(link);
		}

		@Override
		public String toString()
		{
			return sql;
		}

		@Override
		public Sentence.Compiled print()
		{
			System.out.println(toString());
			System.out.println(batch);
			return this;
		}

		private class Connected implements Sentence.Compiled.Connected
		{

			private final Link link;

			private Connected(Link link)
			{
				this.link = link;
			}

			@Override
			public int execute() throws ConstraintViolationException
			{
				try (Command command = link.createCommand(sql))
				{
					int count = 0;
					for (List<Object> parameters : batch)
						count += command.setParameters(parameters).execute();
					return count;
				}
			}

			@Override
			public <T> List<T> fetchGeneratedKeys(Class<T> type) throws ConstraintViolationException
			{
				try (Command command = link.createCommand(sql))
				{
					List<T> generatedKeys = new ArrayList<>();
					for (List<Object> parameters : batch)
						command.setParameters(parameters).execute(type)
							.ifPresent(generatedKeys::add);
					return generatedKeys;
				}
			}

			@Override
			public <T> void fetchGeneratedKeys(Class<T> type, Consumer<T> consumer) throws ConstraintViolationException
			{
				try (Command command = link.createCommand(sql))
				{
					for (List<Object> parameters : batch)
						command.setParameters(parameters).execute(type).ifPresent(consumer);
				}
			}

			@Override
			public <T> List<List<T>> fetchGeneratedKeyLists(Class<T> type) throws ConstraintViolationException
			{
				try (Command command = link.createCommand(sql))
				{
					List<List<T>> generatedKeys = new ArrayList<>();
					for (List<Object> parameters : batch)
					{
						command.setParameters(parameters).execute();
						generatedKeys.add(command.getGeneratedKeys(type));
					}

					return generatedKeys;
				}
			}

			@Override
			public <T> void fetchGeneratedKeyLists(Class<T> type, Consumer<List<T>> consumer) throws ConstraintViolationException
			{
				try (Command command = link.createCommand(sql))
				{
					for (List<Object> parameters : batch)
					{
						command.setParameters(parameters).execute();
						consumer.accept(command.getGeneratedKeys(type));
					}
				}
			}

			@Override
			public Sentence.Compiled.Connected observe(Consumer<List<Object>> consumer)
			{
				return consumer != null
					? new Observed(consumer) : this;
			}

			@Override
			public String toString()
			{
				return sql;
			}

			@Override
			public Sentence.Compiled.Connected print()
			{
				System.out.println(toString());
				System.out.println(batch);
				return this;
			}

			private class Observed implements Sentence.Compiled.Connected
			{

				private final Consumer<List<Object>> observer;

				public Observed(Consumer<List<Object>> observer)
				{
					this.observer = observer;
				}

				@Override
				public int execute() throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						int count = 0;
						for (List<Object> parameters : batch)
						{
							count += command.setParameters(parameters).execute();
							observer.accept(parameters);
						}
						return count;
					}
				}

				@Override
				public <T> List<T> fetchGeneratedKeys(Class<T> type) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						List<T> generatedKeys = new ArrayList<>();
						for (List<Object> parameters : batch)
						{
							command.setParameters(parameters).execute(type)
								.ifPresent(generatedKeys::add);
							observer.accept(parameters);
						}
						return generatedKeys;
					}
				}

				@Override
				public <T> void fetchGeneratedKeys(Class<T> type, Consumer<T> consumer) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						for (List<Object> parameters : batch)
						{
							command.setParameters(parameters).execute(type).ifPresent(consumer);
							observer.accept(parameters);
						}
					}
				}

				@Override
				public <T> List<List<T>> fetchGeneratedKeyLists(Class<T> type) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						List<List<T>> generatedKeys = new ArrayList<>();
						for (List<Object> parameters : batch)
						{
							command.setParameters(parameters).execute();
							generatedKeys.add(command.getGeneratedKeys(type));
							observer.accept(parameters);
						}

						return generatedKeys;
					}
				}

				@Override
				public <T> void fetchGeneratedKeyLists(Class<T> type, Consumer<List<T>> consumer) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						for (List<Object> parameters : batch)
						{
							command.setParameters(parameters).execute();
							consumer.accept(command.getGeneratedKeys(type));
							observer.accept(parameters);
						}
					}
				}

				@Override
				public Sentence.Compiled.Connected observe(Consumer<List<Object>> consumer)
				{
					return Connected.this.observe(consumer);
				}

				@Override
				public String toString()
				{
					return Connected.this.toString();
				}

				@Override
				public Sentence.Compiled.Connected print()
				{
					return Connected.this.print();
				}
			}
		}
	}
}
