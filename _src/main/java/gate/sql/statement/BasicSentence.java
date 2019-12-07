package gate.sql.statement;

import gate.error.ConstraintViolationException;
import gate.error.UncheckedConstraintViolationException;
import gate.sql.Command;
import gate.sql.Link;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

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
		return new Compiled(Collections.singletonList(parameters));
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
	public <T> Extractor<T> from(Class<T> type)
	{
		return new Extractor<>();
	}

	private class Extractor<T> implements Sentence.Extractor<T>
	{

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
		public Extractor<T> print()
		{
			System.out.println(toString());
			return this;
		}

		private class Compiled implements Sentence.Extractor.Compiled<T>
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
				System.out.println(extractors);
				return this;
			}

			private class Connected implements Sentence.Extractor.Compiled.Connected<T>
			{

				private final Link link;

				private Connected(Link link)
				{
					this.link = link;
				}

				@Override
				public Command createCommand()
				{
					return link.createCommand(sql);
				}

				@Override
				public int execute(List<? extends T> values) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						int count = 0;
						for (T value : values)
						{
							extractors.forEach(e -> command.setParameter(e.apply(value)));
							count += command.execute();
						}
						return count;
					}
				}

				@Override
				public void execute(Stream<? extends T> values) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						values.forEach(value ->
						{
							try
							{
								extractors.forEach(e -> command.setParameter(e.apply(value)));
								command.execute();
							} catch (ConstraintViolationException ex)
							{
								throw new UncheckedConstraintViolationException(ex);
							}
						});
					} catch (UncheckedConstraintViolationException ex)
					{
						throw ex.getCause();
					}
				}

				@Override
				public <K> void fetchGeneratedKey(List<? extends T> values, Class<K> type, BiConsumer<T, K> consumer) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						for (T value : values)
						{
							extractors.forEach(e -> command.setParameter(e.apply(value)));
							command.execute(type).ifPresent(e -> consumer.accept(value, e));
						}
					}
				}

				@Override
				public <K> void fetchGeneratedKey(Stream<? extends T> values, Class<K> type, BiConsumer<T, K> consumer) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						values.forEach(value ->
						{
							try
							{
								extractors.forEach(e -> command.setParameter(e.apply(value)));
								command.execute(type).ifPresent(e -> consumer.accept(value, e));
							} catch (ConstraintViolationException ex)
							{
								throw new UncheckedConstraintViolationException(ex);
							}
						});
					}
				}

				@Override
				public <K> void fetchGeneratedKeys(List<? extends T> values, Class<K> type, BiConsumer<T, List<K>> consumer) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						for (T value : values)
						{
							extractors.forEach(e -> command.setParameter(e.apply(value)));
							command.execute(type);
							consumer.accept(value, command.getGeneratedKeys(type));
						}
					}
				}

				@Override
				public <K> void fetchGeneratedKeys(Stream<? extends T> values, Class<K> type, BiConsumer<T, List<K>> consumer) throws ConstraintViolationException
				{

					try (Command command = link.createCommand(sql))
					{
						values.forEach(value ->
						{
							try
							{
								extractors.forEach(e -> command.setParameter(e.apply(value)));
								command.execute(type);
								consumer.accept(value, command.getGeneratedKeys(type));
							} catch (ConstraintViolationException ex)
							{
								throw new UncheckedConstraintViolationException(ex);
							}
						});
					}
				}

				@Override
				public Sentence.Extractor.Compiled.Connected<T> observe(Consumer<T> consumer)
				{
					return consumer != null
						? new Observed(consumer) : this;
				}

				@Override
				public Connected print()
				{
					System.out.println(toString());
					System.out.println(extractors);
					return this;
				}

				public class Observed implements Sentence.Extractor.Compiled.Connected<T>
				{

					private final Consumer<T> observer;

					public Observed(Consumer<T> observer)
					{
						this.observer = observer;
					}

					@Override
					public Command createCommand()
					{
						return link.createCommand(sql);
					}

					@Override
					public int execute(List<? extends T> values) throws ConstraintViolationException
					{
						try (Command command = link.createCommand(sql))
						{
							int count = 0;
							for (T value : values)
							{
								extractors.forEach(e -> command.setParameter(e.apply(value)));
								count += command.execute();
								observer.accept(value);
							}
							return count;
						}
					}

					@Override
					public void execute(Stream<? extends T> values) throws ConstraintViolationException
					{
						try (Command command = link.createCommand(sql))
						{
							values.forEach(value ->
							{
								try
								{
									extractors.forEach(e -> command.setParameter(e.apply(value)));
									command.execute();
									observer.accept(value);
								} catch (ConstraintViolationException ex)
								{
									throw new UncheckedConstraintViolationException(ex);
								}
							});
						} catch (UncheckedConstraintViolationException ex)
						{
							throw ex.getCause();
						}
					}

					@Override
					public <K> void fetchGeneratedKey(List<? extends T> values, Class<K> type, BiConsumer<T, K> consumer) throws ConstraintViolationException
					{
						try (Command command = link.createCommand(sql))
						{
							for (T value : values)
							{
								extractors.forEach(e -> command.setParameter(e.apply(value)));
								command.execute(type).ifPresent(e -> consumer.accept(value, e));
								observer.accept(value);
							}
						}
					}

					@Override
					public <K> void fetchGeneratedKey(Stream<? extends T> values, Class<K> type, BiConsumer<T, K> consumer) throws ConstraintViolationException
					{
						try (Command command = link.createCommand(sql))
						{
							values.forEach(value ->
							{
								try
								{
									extractors.forEach(e -> command.setParameter(e.apply(value)));
									command.execute(type).ifPresent(e -> consumer.accept(value, e));
									observer.accept(value);
								} catch (ConstraintViolationException ex)
								{
									throw new UncheckedConstraintViolationException(ex);
								}
							});
						}
					}

					@Override
					public <K> void fetchGeneratedKeys(List<? extends T> values, Class<K> type, BiConsumer<T, List<K>> consumer) throws ConstraintViolationException
					{
						try (Command command = link.createCommand(sql))
						{
							for (T value : values)
							{
								extractors.forEach(e -> command.setParameter(e.apply(value)));
								command.execute(type);
								consumer.accept(value, command.getGeneratedKeys(type));
								observer.accept(value);
							}
						}
					}

					@Override
					public <K> void fetchGeneratedKeys(Stream<? extends T> values, Class<K> type, BiConsumer<T, List<K>> consumer) throws ConstraintViolationException
					{

						try (Command command = link.createCommand(sql))
						{
							values.forEach(value ->
							{
								try
								{
									extractors.forEach(e -> command.setParameter(e.apply(value)));
									command.execute(type);
									consumer.accept(value, command.getGeneratedKeys(type));
									observer.accept(value);
								} catch (ConstraintViolationException ex)
								{
									throw new UncheckedConstraintViolationException(ex);
								}
							});
						}
					}

					@Override
					public Sentence.Extractor.Compiled.Connected<T> observe(Consumer<T> consumer)
					{
						return Connected.this.observe(consumer);
					}

					@Override
					public String toString()
					{
						return Connected.this.toString();
					}

					@Override
					public Sentence.Extractor.Compiled.Connected<T> print()
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
			return new Compiled(Collections.singletonList(parameters));
		}

		@Override
		public Sentence.Connected.Compiled batch(List<List<?>> batch)
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
		public <T> Extractor<T> from(Class<T> type)
		{
			return new Extractor<>();
		}

		@Override
		public Command createCommand()
		{
			return link.createCommand(sql);
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
		public <K> Optional<K> fetchGeneratedKey(Class<K> type) throws ConstraintViolationException
		{
			return parameters(Collections.singletonList(Collections.emptyList())).fetchGeneratedKey(type);
		}

		@Override
		public <K> void fetchGeneratedKey(Class<K> type, BiConsumer<List<?>, K> consumer) throws ConstraintViolationException
		{
			parameters(Collections.singletonList(Collections.emptyList())).fetchGeneratedKey(type, consumer);
		}

		@Override
		public <K> void fetchGeneratedKeys(Class<K> type, BiConsumer<List<?>, List<K>> consumer) throws ConstraintViolationException
		{
			parameters(Collections.singletonList(Collections.emptyList())).fetchGeneratedKeys(type, consumer);
		}

		private class Compiled implements Sentence.Connected.Compiled
		{

			private final List<List<?>> batch;

			public Compiled(List<List<?>> batch)
			{
				this.batch = batch;
			}

			@Override
			public Command createCommand()
			{
				return link.createCommand(sql);
			}

			@Override
			public int execute() throws ConstraintViolationException
			{
				try (Command command = link.createCommand(sql))
				{
					int count = 0;
					for (List<?> parameters : batch)
						count += command.setParameters(parameters).execute();
					return count;
				}
			}

			@Override
			public <K> List<K> fetchGeneratedKeys(Class<K> type) throws ConstraintViolationException
			{
				try (Command command = link.createCommand(sql))
				{
					if (batch.isEmpty())
						return Collections.emptyList();
					command.setParameters(batch.get(0)).execute();
					return command.getGeneratedKeys(type);
				}
			}

			@Override
			public <K> Optional<K> fetchGeneratedKey(Class<K> type) throws ConstraintViolationException
			{
				try (Command command = link.createCommand(sql))
				{
					return batch.isEmpty() ? Optional.empty()
						: command.setParameters(batch.get(0)).execute(type);
				}
			}

			@Override
			public <K> void fetchGeneratedKey(Class<K> type, BiConsumer<List<?>, K> consumer) throws ConstraintViolationException
			{
				try (Command command = link.createCommand(sql))
				{
					for (List<?> parameters : batch)
						command.setParameters(parameters).execute(type).ifPresent(e -> consumer.accept(parameters, e));
				}
			}

			@Override
			public <K> void fetchGeneratedKeys(Class<K> type, BiConsumer<List<?>, List<K>> consumer) throws ConstraintViolationException
			{
				try (Command command = link.createCommand(sql))
				{
					for (List<?> parameters : batch)
					{
						command.setParameters(parameters).execute();
						consumer.accept(parameters, command.getGeneratedKeys(type));
					}
				}
			}

			@Override
			public Sentence.Connected.Compiled observe(Consumer<List<?>> consumer)
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

				private final Consumer<List<?>> observer;

				public Observed(Consumer<List<?>> observer)
				{
					this.observer = observer;
				}

				@Override
				public Command createCommand()
				{
					return link.createCommand(sql);
				}

				@Override
				public int execute() throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						int count = 0;
						for (List<?> parameters : batch)
						{
							count += command.setParameters(parameters).execute();
							observer.accept(parameters);
						}
						return count;
					}
				}

				@Override
				public <T> Optional<T> fetchGeneratedKey(Class<T> type) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						if (batch.isEmpty())
							return Optional.empty();

						List<?> parameters = batch.get(0);
						Optional<T> key = command.setParameters(parameters).execute(type);
						observer.accept(parameters);
						return key;
					}
				}

				@Override
				public <T> List<T> fetchGeneratedKeys(Class<T> type) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						if (batch.isEmpty())
							return Collections.emptyList();
						command.setParameters(batch.get(0)).execute();
						return command.getGeneratedKeys(type);
					}
				}

				@Override
				public <K> void fetchGeneratedKey(Class<K> type, BiConsumer<List<?>, K> consumer) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						for (List<?> parameters : batch)
						{
							command.setParameters(parameters).execute(type).ifPresent(e -> consumer.accept(parameters, e));
							observer.accept(parameters);
						}
					}
				}

				@Override
				public <K> void fetchGeneratedKeys(Class<K> type, BiConsumer<List<?>, List<K>> consumer) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						for (List<?> parameters : batch)
						{
							command.setParameters(parameters).execute();
							consumer.accept(parameters, command.getGeneratedKeys(type));
							observer.accept(parameters);
						}
					}
				}

				@Override
				public Sentence.Connected.Compiled observe(Consumer<List<?>> consumer)
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

		private class Extractor<T> implements Sentence.Connected.Extractor<T>
		{

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
			public Extractor<T> print()
			{
				System.out.println(toString());
				return this;
			}

			public class Compiled implements Sentence.Connected.Extractor.Compiled<T>
			{

				private final List<Function<T, ?>> extractors;

				public Compiled(List<Function<T, ?>> extractors)
				{
					this.extractors = extractors;
				}

				@Override
				public Command createCommand()
				{
					return link.createCommand(sql);
				}

				@Override
				public int execute(List<? extends T> values) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						int count = 0;
						for (T value : values)
						{
							extractors.forEach(e -> command.setParameter(e.apply(value)));
							count += command.execute();
						}
						return count;
					}
				}

				@Override
				public void execute(Stream<? extends T> values) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						values.forEach(value ->
						{
							try
							{
								extractors.forEach(e -> command.setParameter(e.apply(value)));
								command.execute();
							} catch (ConstraintViolationException ex)
							{
								throw new UncheckedConstraintViolationException(ex);
							}
						});
					} catch (UncheckedConstraintViolationException ex)
					{
						throw ex.getCause();
					}
				}

				@Override
				public <K> void fetchGeneratedKey(List<? extends T> values, Class<K> type, BiConsumer<T, K> consumer) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						for (T value : values)
						{
							extractors.forEach(e -> command.setParameter(e.apply(value)));
							command.execute(type).ifPresent(e -> consumer.accept(value, e));
						}
					}
				}

				@Override
				public <K> void fetchGeneratedKey(Stream<? extends T> values, Class<K> type, BiConsumer<T, K> consumer) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						values.forEach(value ->
						{
							try
							{
								extractors.forEach(e -> command.setParameter(e.apply(value)));
								command.execute(type).ifPresent(e -> consumer.accept(value, e));
							} catch (ConstraintViolationException ex)
							{
								throw new UncheckedConstraintViolationException(ex);
							}
						});
					}
				}

				@Override
				public <K> void fetchGeneratedKeys(List<? extends T> values, Class<K> type, BiConsumer<T, List<K>> consumer) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						for (T value : values)
						{
							extractors.forEach(e -> command.setParameter(e.apply(value)));
							command.execute(type);
							consumer.accept(value, command.getGeneratedKeys(type));
						}
					}
				}

				@Override
				public <K> void fetchGeneratedKeys(Stream<? extends T> values, Class<K> type, BiConsumer<T, List<K>> consumer) throws ConstraintViolationException
				{

					try (Command command = link.createCommand(sql))
					{
						values.forEach(value ->
						{
							try
							{
								extractors.forEach(e -> command.setParameter(e.apply(value)));
								command.execute(type);
								consumer.accept(value, command.getGeneratedKeys(type));
							} catch (ConstraintViolationException ex)
							{
								throw new UncheckedConstraintViolationException(ex);
							}
						});
					}
				}

				@Override
				public Sentence.Connected.Extractor.Compiled<T> observe(Consumer<T> consumer)
				{
					return consumer != null
						? new Observed(consumer) : this;
				}

				@Override
				public Compiled print()
				{
					System.out.println(toString());
					System.out.println(extractors);
					return this;
				}

				public class Observed implements Sentence.Connected.Extractor.Compiled<T>
				{

					private final Consumer<T> observer;

					public Observed(Consumer<T> observer)
					{
						this.observer = observer;
					}

					@Override
					public Command createCommand()
					{
						return link.createCommand(sql);
					}

					@Override
					public int execute(List<? extends T> values) throws ConstraintViolationException
					{
						try (Command command = link.createCommand(sql))
						{
							int count = 0;
							for (T value : values)
							{
								extractors.forEach(e -> command.setParameter(e.apply(value)));
								count += command.execute();
								observer.accept(value);
							}
							return count;
						}
					}

					@Override
					public void execute(Stream<? extends T> values) throws ConstraintViolationException
					{
						try (Command command = link.createCommand(sql))
						{
							values.forEach(value ->
							{
								try
								{
									extractors.forEach(e -> command.setParameter(e.apply(value)));
									command.execute();
									observer.accept(value);
								} catch (ConstraintViolationException ex)
								{
									throw new UncheckedConstraintViolationException(ex);
								}
							});
						} catch (UncheckedConstraintViolationException ex)
						{
							throw ex.getCause();
						}
					}

					@Override
					public <K> void fetchGeneratedKey(List<? extends T> values, Class<K> type, BiConsumer<T, K> consumer) throws ConstraintViolationException
					{
						try (Command command = link.createCommand(sql))
						{
							for (T value : values)
							{
								extractors.forEach(e -> command.setParameter(e.apply(value)));
								command.execute(type).ifPresent(e -> consumer.accept(value, e));
								observer.accept(value);
							}
						}
					}

					@Override
					public <K> void fetchGeneratedKey(Stream<? extends T> values, Class<K> type, BiConsumer<T, K> consumer) throws ConstraintViolationException
					{
						try (Command command = link.createCommand(sql))
						{
							values.forEach(value ->
							{
								try
								{
									extractors.forEach(e -> command.setParameter(e.apply(value)));
									command.execute(type).ifPresent(e -> consumer.accept(value, e));
									observer.accept(value);
								} catch (ConstraintViolationException ex)
								{
									throw new UncheckedConstraintViolationException(ex);
								}
							});
						}
					}

					@Override
					public <K> void fetchGeneratedKeys(List<? extends T> values, Class<K> type, BiConsumer<T, List<K>> consumer) throws ConstraintViolationException
					{
						try (Command command = link.createCommand(sql))
						{
							for (T value : values)
							{
								extractors.forEach(e -> command.setParameter(e.apply(value)));
								command.execute(type);
								consumer.accept(value, command.getGeneratedKeys(type));
								observer.accept(value);
							}
						}
					}

					@Override
					public <K> void fetchGeneratedKeys(Stream<? extends T> values, Class<K> type, BiConsumer<T, List<K>> consumer) throws ConstraintViolationException
					{

						try (Command command = link.createCommand(sql))
						{
							values.forEach(value ->
							{
								try
								{
									extractors.forEach(e -> command.setParameter(e.apply(value)));
									command.execute(type);
									consumer.accept(value, command.getGeneratedKeys(type));
									observer.accept(value);
								} catch (ConstraintViolationException ex)
								{
									throw new UncheckedConstraintViolationException(ex);
								}
							});
						}
					}

					@Override
					public Sentence.Connected.Extractor.Compiled<T> observe(Consumer<T> consumer)
					{
						return Compiled.this.observe(consumer);
					}

					@Override
					public String toString()
					{
						return Compiled.this.toString();
					}

					@Override
					public Sentence.Connected.Extractor.Compiled<T> print()
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
			public Command createCommand()
			{
				return link.createCommand(sql);
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
			public <T> Optional<T> fetchGeneratedKey(Class<T> type) throws ConstraintViolationException
			{
				try (Command command = link.createCommand(sql))
				{
					return batch.isEmpty() ? Optional.empty()
						: command.setParameters(batch.get(0)).execute(type);
				}
			}

			@Override
			public <T> List<T> fetchGeneratedKeys(Class<T> type) throws ConstraintViolationException
			{
				try (Command command = link.createCommand(sql))
				{
					if (batch.isEmpty())
						return Collections.emptyList();
					command.setParameters(batch.get(0)).execute();
					return command.getGeneratedKeys(type);
				}
			}

			@Override
			public <T> void fetchGeneratedKey(Class<T> type, BiConsumer<List<?>, T> consumer) throws ConstraintViolationException
			{
				try (Command command = link.createCommand(sql))
				{
					for (List<Object> parameters : batch)
						command.setParameters(parameters).execute(type).ifPresent(e -> consumer.accept(parameters, e));
				}
			}

			@Override
			public <T> void fetchGeneratedKeys(Class<T> type, BiConsumer<List<?>, List<T>> consumer) throws ConstraintViolationException
			{
				try (Command command = link.createCommand(sql))
				{
					for (List<Object> parameters : batch)
					{
						command.setParameters(parameters).execute();
						consumer.accept(parameters, command.getGeneratedKeys(type));
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
				public Command createCommand()
				{
					return link.createCommand(sql);
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
				public <T> Optional<T> fetchGeneratedKey(Class<T> type) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						if (batch.isEmpty())
							return Optional.empty();

						List<Object> parameters = batch.get(0);
						Optional<T> key = command.setParameters(parameters).execute(type);
						observer.accept(parameters);
						return key;
					}
				}

				@Override
				public <T> List<T> fetchGeneratedKeys(Class<T> type) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						if (batch.isEmpty())
							return Collections.emptyList();
						List<Object> parameters = batch.get(0);
						command.setParameters(batch.get(0)).execute();
						observer.accept(parameters);
						return command.getGeneratedKeys(type);
					}
				}

				@Override
				public <T> void fetchGeneratedKey(Class<T> type, BiConsumer<List<?>, T> consumer) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						for (List<Object> parameters : batch)
						{
							command.setParameters(parameters).execute(type).ifPresent(e -> consumer.accept(parameters, e));
							observer.accept(parameters);
						}
					}
				}

				@Override
				public <T> void fetchGeneratedKeys(Class<T> type, BiConsumer<List<?>, List<T>> consumer) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
						for (List<Object> parameters : batch)
						{
							command.setParameters(parameters).execute();
							consumer.accept(parameters, command.getGeneratedKeys(type));
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
