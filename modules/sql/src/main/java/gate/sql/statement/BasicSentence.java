package gate.sql.statement;

import gate.error.ConstraintViolationException;
import gate.sql.Command;
import gate.sql.Link;
import org.slf4j.Logger;

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
		return new Compiled(parameters);
	}

	@Override
	public String toString() {return sql;}

	@Override
	public Sentence print()
	{
		System.out.println(this);
		return this;
	}

	@Override
	public Sentence print(Logger logger)
	{
		logger.debug("{}", this);
		return this;
	}

	@Override
	public <T> Sentence.Extractor<T> from(Class<T> type) {return new Extractor<>();}

	private class Extractor<T> implements Sentence.Extractor<T>
	{

		@Override
		public String toString() {return sql;}

		@Override
		public Sentence.Extractor.Compiled<T> parameters(List<Function<T, ?>> extractors) {return new Compiled(extractors);}

		@Override
		public Sentence.Extractor<T> print()
		{
			BasicSentence.this.print();
			return this;
		}

		@Override
		public Sentence.Extractor<T> print(Logger logger)
		{
			BasicSentence.this.print(logger);
			return this;
		}

		private class Compiled implements Sentence.Extractor.Compiled<T>
		{

			private final List<Function<T, ?>> extractors;

			public Compiled(List<Function<T, ?>> extractors) {this.extractors = extractors;}

			@Override
			public Sentence.Extractor.Compiled.Connected<T> connect(Link link) {return new Connected(link);}

			@Override
			public Sentence.Extractor.Compiled<T> print(Logger logger)
			{
				logger.debug("{}", this);
				logger.debug("{}", extractors);
				return this;
			}

			@Override
			public Sentence.Extractor.Compiled<T> print()
			{
				System.out.println(this);
				System.out.println(extractors);
				return this;
			}

			@Override
			public String toString() {return sql;}

			private class Connected implements Sentence.Extractor.Compiled.Connected<T>
			{

				private final Link link;

				private Connected(Link link) {this.link = link;}

				@Override
				public Command createCommand() {return link.createCommand(sql);}

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
							extractors.forEach(e -> command.setParameter(e.apply(value)));
							command.execute();
						});
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
							extractors.forEach(e -> command.setParameter(e.apply(value)));
							command.execute(type).ifPresent(e -> consumer.accept(value, e));
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
				public <K> void fetchGeneratedKeys(Stream<? extends T> values, Class<K> type, BiConsumer<T, List<K>> consumer) throws
				                                                                                                               ConstraintViolationException
				{

					try (Command command = link.createCommand(sql))
					{
						values.forEach(value ->
						{
							extractors.forEach(e -> command.setParameter(e.apply(value)));
							command.execute(type);
							consumer.accept(value, command.getGeneratedKeys(type));
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
				public Sentence.Extractor.Compiled.Connected<T> print()
				{
					BasicSentence.Extractor.Compiled.this.print();
					return this;
				}

				@Override
				public Sentence.Extractor.Compiled.Connected<T> print(Logger logger)
				{
					BasicSentence.Extractor.Compiled.this.print(logger);
					return this;
				}

				@Override
				public String toString() {return sql;}

				public class Observed implements Sentence.Extractor.Compiled.Connected<T>
				{
					private final Consumer<T> observer;

					public Observed(Consumer<T> observer) {this.observer = observer;}

					@Override
					public Command createCommand() {return link.createCommand(sql);}

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
								extractors.forEach(e -> command.setParameter(e.apply(value)));
								command.execute();
								observer.accept(value);
							});
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
								extractors.forEach(e -> command.setParameter(e.apply(value)));
								command.execute(type).ifPresent(e -> consumer.accept(value, e));
								observer.accept(value);
							});
						}
					}

					@Override
					public <K> void fetchGeneratedKeys(List<? extends T> values, Class<K> type, BiConsumer<T, List<K>> consumer) throws
					                                                                                                             ConstraintViolationException
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
					public <K> void fetchGeneratedKeys(Stream<? extends T> values, Class<K> type, BiConsumer<T, List<K>> consumer)
							throws ConstraintViolationException
					{
						try (Command command = link.createCommand(sql))
						{
							values.forEach(value ->
							{
								extractors.forEach(e -> command.setParameter(e.apply(value)));
								command.execute(type);
								consumer.accept(value, command.getGeneratedKeys(type));
								observer.accept(value);
							});
						}
					}

					@Override
					public Sentence.Extractor.Compiled.Connected<T> observe(Consumer<T> consumer)
					{
						return Extractor.Compiled.Connected.this.observe(consumer);
					}

					@Override
					public String toString() {return sql;}

					@Override
					public Sentence.Extractor.Compiled.Connected<T> print()
					{
						BasicSentence.Extractor.Compiled.this.print();
						return this;
					}

					@Override
					public Sentence.Extractor.Compiled.Connected<T> print(Logger logger)
					{
						BasicSentence.Extractor.Compiled.this.print(logger);
						return this;
					}
				}
			}
		}
	}

	private class Connected implements Sentence.Connected
	{

		private final Link link;

		private Connected(Link link) {this.link = link;}

		@Override
		public Sentence.Connected.Compiled parameters(List<Object> parameters)
		{
			return new Compiled(parameters);
		}


		@Override
		public String toString()
		{
			return sql;
		}

		@Override
		public Sentence.Connected print()
		{
			BasicSentence.this.print();
			return this;
		}

		@Override
		public Sentence.Connected print(Logger logger)
		{
			BasicSentence.this.print(logger);
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
			try (Command command = link.createCommand(sql))
			{
				return command.execute();
			}
		}

		@Override
		public <K> List<K> fetchGeneratedKeys(Class<K> type) throws ConstraintViolationException
		{
			try (Command command = link.createCommand(sql))
			{
				command.execute();
				return command.getGeneratedKeys(type);
			}
		}

		@Override
		public <K> Optional<K> fetchGeneratedKey(Class<K> type) throws ConstraintViolationException
		{
			try (Command command = link.createCommand(sql))
			{
				return command.execute(type);
			}
		}

		private class Compiled implements Sentence.Connected.Compiled
		{

			private final List<Object> parameters;

			public Compiled(List<Object> parameters)
			{
				this.parameters = parameters;
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
					return command.setParameters(parameters).execute();
				}
			}

			@Override
			public <K> List<K> fetchGeneratedKeys(Class<K> type) throws ConstraintViolationException
			{
				try (Command command = link.createCommand(sql))
				{
					command.setParameters(parameters).execute();
					return command.getGeneratedKeys(type);
				}
			}

			@Override
			public <K> Optional<K> fetchGeneratedKey(Class<K> type) throws ConstraintViolationException
			{
				try (Command command = link.createCommand(sql))
				{
					return command.setParameters(parameters).execute(type);
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
				System.out.println(this);
				System.out.println(parameters);
				return this;
			}

			@Override
			public Sentence.Connected.Compiled print(Logger logger)
			{
				logger.debug("{}", this);
				logger.debug("{}", parameters);
				return this;
			}

			@Override public List<Object> getParameters() {return parameters;}

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
						int count = command.setParameters(parameters).execute();
						observer.accept(parameters);
						return count;
					}
				}

				@Override
				public <T> Optional<T> fetchGeneratedKey(Class<T> type) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
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
						command.setParameters(parameters).execute();
						observer.accept(parameters);
						return command.getGeneratedKeys(type);
					}
				}

				@Override
				public Sentence.Connected.Compiled observe(Consumer<List<?>> consumer)
				{
					return Connected.Compiled.this.observe(consumer);
				}

				@Override
				public String toString() {return sql;}

				@Override
				public Sentence.Connected.Compiled print()
				{
					Connected.Compiled.this.print();
					return this;
				}

				@Override
				public Sentence.Connected.Compiled print(Logger logger)
				{
					Connected.Compiled.this.print(logger);
					return this;
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
				BasicSentence.this.print();
				return this;
			}

			@Override
			public Extractor<T> print(Logger logger)
			{
				BasicSentence.this.print(logger);
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
							extractors.stream()
									.map(e -> e.apply(value))
									.forEach(command::setParameter);
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
							extractors.forEach(e -> command.setParameter(e.apply(value)));
							command.execute();
						});
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
							extractors.forEach(e -> command.setParameter(e.apply(value)));
							command.execute(type).ifPresent(e -> consumer.accept(value, e));
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
				public <K> void fetchGeneratedKeys(Stream<? extends T> values, Class<K> type, BiConsumer<T, List<K>> consumer) throws
				                                                                                                               ConstraintViolationException
				{

					try (Command command = link.createCommand(sql))
					{
						values.forEach(value ->
						{
							extractors.forEach(e -> command.setParameter(e.apply(value)));
							command.execute(type);
							consumer.accept(value, command.getGeneratedKeys(type));
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
				public Sentence.Connected.Extractor.Compiled<T> print()
				{
					System.out.println(this);
					System.out.println(extractors);
					return this;
				}

				@Override
				public Sentence.Connected.Extractor.Compiled<T> print(Logger logger)
				{
					logger.debug("{}", this);
					logger.debug("{}", extractors);
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
								extractors.forEach(e -> command.setParameter(e.apply(value)));
								command.execute();
								observer.accept(value);
							});
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
								extractors.forEach(e -> command.setParameter(e.apply(value)));
								command.execute(type).ifPresent(e -> consumer.accept(value, e));
								observer.accept(value);
							});
						}
					}

					@Override
					public <K> void fetchGeneratedKeys(List<? extends T> values, Class<K> type, BiConsumer<T, List<K>> consumer) throws
					                                                                                                             ConstraintViolationException
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
					public <K> void fetchGeneratedKeys(Stream<? extends T> values, Class<K> type, BiConsumer<T, List<K>> consumer)
							throws ConstraintViolationException
					{
						try (Command command = link.createCommand(sql))
						{
							values.forEach(value ->
							{
								extractors.forEach(e -> command.setParameter(e.apply(value)));
								command.execute(type);
								consumer.accept(value, command.getGeneratedKeys(type));
								observer.accept(value);
							});
						}
					}

					@Override
					public Sentence.Connected.Extractor.Compiled<T> observe(Consumer<T> consumer)
					{
						return Connected.Extractor.Compiled.this.observe(consumer);
					}

					@Override
					public String toString() {return sql;}

					@Override
					public Sentence.Connected.Extractor.Compiled<T> print()
					{
						Connected.Extractor.Compiled.this.print();
						return this;
					}

					@Override
					public Sentence.Connected.Extractor.Compiled<T> print(Logger logger)
					{
						Connected.Extractor.Compiled.this.print(logger);
						return this;
					}
				}
			}
		}

	}

	private class Compiled implements Sentence.Compiled
	{

		private final List<Object> parameters;

		public Compiled(List<Object> parameters)
		{
			this.parameters = parameters;
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
			System.out.println(this);
			System.out.println(parameters);
			return this;
		}

		@Override
		public Sentence.Compiled print(Logger logger)
		{
			logger.debug("{}", this);
			logger.debug("{}", parameters);
			return this;
		}

		@Override public List<Object> getParameters() {return parameters;}

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
					return command.setParameters(parameters).execute();
				}
			}

			@Override
			public <T> Optional<T> fetchGeneratedKey(Class<T> type) throws ConstraintViolationException
			{
				try (Command command = link.createCommand(sql))
				{
					return command.setParameters(parameters).execute(type);
				}
			}

			@Override
			public <T> List<T> fetchGeneratedKeys(Class<T> type) throws ConstraintViolationException
			{
				try (Command command = link.createCommand(sql))
				{
					command.setParameters(parameters).execute();
					return command.getGeneratedKeys(type);
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
				System.out.println(this);
				System.out.println(parameters);
				return this;
			}

			@Override
			public Sentence.Compiled.Connected print(Logger logger)
			{
				logger.debug("{}", this);
				logger.debug("{}", parameters);
				return this;
			}

			@Override public List<Object> getParameters() {return parameters;}

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
						int count = command.setParameters(parameters).execute();
						observer.accept(parameters);
						return count;
					}
				}

				@Override
				public <T> Optional<T> fetchGeneratedKey(Class<T> type) throws ConstraintViolationException
				{
					try (Command command = link.createCommand(sql))
					{
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
						command.setParameters(parameters).execute();
						observer.accept(parameters);
						return command.getGeneratedKeys(type);
					}
				}

				@Override
				public Sentence.Compiled.Connected observe(Consumer<List<Object>> consumer)
				{
					return Compiled.Connected.this.observe(consumer);
				}

				@Override
				public String toString() {return sql;}

				public Sentence.Compiled.Connected print()
				{
					Compiled.Connected.this.print();
					return this;
				}

				@Override
				public Sentence.Compiled.Connected print(Logger logger)
				{
					Compiled.Connected.this.print(logger);
					return this;
				}
			}
		}
	}
}