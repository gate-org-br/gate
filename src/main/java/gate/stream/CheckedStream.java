package gate.stream;

import java.util.function.IntFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class CheckedStream<T, E extends Exception>
{

	private final Class<E> type;
	private final Stream<T> stream;

	private CheckedStream(Class<E> type, Stream<T> stream)
	{
		this.type = type;
		this.stream = stream;
	}

	public static <T, E extends Exception> CheckedStream<T, E> of(Class<E> type, Stream<T> stream)
	{
		return new CheckedStream<>(type, stream);
	}

	public static <T> CheckedStream<T, Exception> of(Stream<T> stream)
	{
		return new CheckedStream<>(Exception.class, stream);
	}

	public CheckedStream<T, E> filter(CheckedPredicate<? super T> predicate) throws E
	{
		try
		{
			return CheckedStream.of(type, stream.filter(e ->
			{
				try
				{
					return predicate.test(e);
				} catch (Exception ex)
				{
					throw new CheckedExceptionWrapper(ex);
				}
			}));
		} catch (CheckedExceptionWrapper ex)
		{
			throw type.cast(ex.getCause());
		}
	}

	public <R> CheckedStream<R, E> map(CheckedFunction<? super T, ? extends R> mapper) throws E
	{
		try
		{
			return CheckedStream.of(type, stream.map(e ->
			{
				try
				{
					return mapper.apply(e);
				} catch (Exception ex)
				{
					throw new CheckedExceptionWrapper(ex);
				}
			}));
		} catch (CheckedExceptionWrapper ex)
		{
			throw type.cast(ex.getCause());
		}
	}

	public DoubleStream mapToDouble​(CheckedToDoubleFunction<? super T> mapper) throws E
	{
		try
		{
			return stream.mapToDouble(e ->
			{
				try
				{
					return mapper.applyAsDouble(e);
				} catch (Exception ex)
				{
					throw new CheckedExceptionWrapper(ex);
				}
			});
		} catch (CheckedExceptionWrapper ex)
		{
			throw type.cast(ex.getCause());
		}
	}

	public DoubleStream flatMapToDouble(CheckedFunction<? super T, ? extends DoubleStream> mapper) throws E
	{
		try
		{
			return stream.flatMapToDouble(a ->
			{
				try
				{
					return mapper.apply(a);
				} catch (Exception ex)
				{
					throw new CheckedExceptionWrapper(ex);
				}
			});
		} catch (CheckedExceptionWrapper ex)
		{
			throw type.cast(ex.getCause());
		}
	}

	public IntStream mapToInt(CheckedToIntFunction<? super T> mapper) throws E
	{
		try
		{
			return stream.mapToInt(e ->
			{
				try
				{
					return mapper.applyAsInt(e);
				} catch (Exception ex)
				{
					throw new CheckedExceptionWrapper(ex);
				}
			});
		} catch (CheckedExceptionWrapper ex)
		{
			throw type.cast(ex.getCause());
		}
	}

	public IntStream flatMapToInt(CheckedFunction<? super T, ? extends IntStream> mapper) throws E
	{
		try
		{
			return stream.flatMapToInt(a ->
			{
				try
				{
					return mapper.apply(a);
				} catch (Exception ex)
				{
					throw new CheckedExceptionWrapper(ex);
				}
			});
		} catch (CheckedExceptionWrapper ex)
		{
			throw type.cast(ex.getCause());
		}
	}

	public LongStream mapToLong(CheckedToLongFunction<? super T> mapper) throws E
	{
		try
		{
			return stream.mapToLong(e ->
			{
				try
				{
					return mapper.applyAsLong(e);
				} catch (Exception ex)
				{
					throw new CheckedExceptionWrapper(ex);
				}
			});
		} catch (CheckedExceptionWrapper ex)
		{
			throw type.cast(ex.getCause());
		}
	}

	public LongStream flatMapToLong(CheckedFunction<? super T, ? extends LongStream> mapper) throws E
	{
		try
		{
			return stream.flatMapToLong(a ->
			{
				try
				{
					return mapper.apply(a);
				} catch (Exception ex)
				{
					throw new CheckedExceptionWrapper(ex);
				}
			});
		} catch (CheckedExceptionWrapper ex)
		{
			throw type.cast(ex.getCause());
		}
	}

	public <R> CheckedStream<R, E> flatMap(CheckedFunction<? super T, ? extends Stream<? extends R>> mapper) throws E
	{
		try
		{
			return CheckedStream.of(type, stream.flatMap(e ->
			{
				try
				{
					return mapper.apply(e);
				} catch (Exception ex)
				{
					throw new CheckedExceptionWrapper(ex);
				}
			}));
		} catch (CheckedExceptionWrapper ex)
		{
			throw type.cast(ex.getCause());
		}
	}

	public CheckedStream<T, E> distinct() throws E
	{
		return CheckedStream.of(type, stream.distinct());
	}

	public CheckedStream<T, E> sorted​() throws E
	{
		return CheckedStream.of(type, stream.sorted());
	}

	public CheckedStream<T, E> sorted​(CheckedComparator<? super T> comparator) throws E
	{
		return CheckedStream.of(type, stream).sorted((a, b) ->
		{
			try
			{
				return comparator.compare(a, b);
			} catch (Exception ex)
			{
				throw new CheckedExceptionWrapper(ex);
			}
		});
	}

	public CheckedStream<T, E> peek​(CheckedConsumer<? super T> action) throws E
	{
		try
		{
			return CheckedStream.of(type, stream.peek​(e ->
			{
				try
				{
					action.accept(e);
				} catch (Exception ex)
				{
					throw new CheckedExceptionWrapper(ex);
				}
			}));
		} catch (CheckedExceptionWrapper ex)
		{
			throw type.cast(ex.getCause());
		}
	}

	public CheckedStream<T, E> limit(long maxSize) throws E
	{
		return CheckedStream.of(type, stream).limit(maxSize);
	}

	public CheckedStream<T, E> skip(long n) throws E
	{
		return CheckedStream.of(type, stream).skip(n);
	}

	public CheckedStream<T, E> takeWhile(CheckedPredicate<? super T> predicate) throws E
	{
		try
		{
			return CheckedStream.of(type, stream.takeWhile(e ->
			{
				try
				{
					return predicate.test(e);
				} catch (Exception ex)
				{
					throw new CheckedExceptionWrapper(ex);
				}
			}));
		} catch (CheckedExceptionWrapper ex)
		{
			throw type.cast(ex.getCause());
		}
	}

	public CheckedStream<T, E> dropWhile(CheckedPredicate<? super T> predicate) throws E
	{
		try
		{
			return CheckedStream.of(type, stream.dropWhile(e ->
			{
				try
				{
					return predicate.test(e);
				} catch (Exception ex)
				{
					throw new CheckedExceptionWrapper(ex);
				}
			}));
		} catch (CheckedExceptionWrapper ex)
		{
			throw type.cast(ex.getCause());
		}
	}

	public void forEach(CheckedConsumer<? super T> action) throws E
	{
		try
		{
			stream.forEach(e ->
			{
				try
				{
					action.accept(e);
				} catch (Exception ex)
				{
					throw new CheckedExceptionWrapper(ex);
				}
			});
		} catch (CheckedExceptionWrapper ex)
		{
			throw type.cast(ex.getCause());
		}
	}

	public void forEachOrdered​(CheckedConsumer<? super T> action) throws E
	{
		try
		{
			stream.forEach(e ->
			{
				try
				{
					action.accept(e);
				} catch (Exception ex)
				{
					throw new CheckedExceptionWrapper(ex);
				}
			});
		} catch (CheckedExceptionWrapper ex)
		{
			throw type.cast(ex.getCause());
		}
	}

	public Object[] toArray()
	{
		return stream.toArray();
	}

	public <A> A[] toArray​(IntFunction<A[]> generator)
	{
		return stream.toArray(generator);
	}

	public T reduce​(T identity, CheckedBinaryOperator<T> accumulator) throws E
	{
		try
		{

			return stream.reduce(identity, (a, b) ->
			{
				try
				{
					return accumulator.apply(a, b);
				} catch (Exception ex)
				{
					throw new CheckedExceptionWrapper(ex);
				}
			});
		} catch (CheckedExceptionWrapper ex)
		{
			throw type.cast(ex.getCause());
		}
	}

	public CheckedOptional<T, E> reduce​(CheckedBinaryOperator<T> accumulator) throws E
	{
		try
		{
			return CheckedOptional.of(type, stream.reduce((a, b) ->
			{
				try
				{
					return accumulator.apply(a, b);
				} catch (Exception ex)
				{
					throw new CheckedExceptionWrapper(ex);
				}
			}));
		} catch (CheckedExceptionWrapper ex)
		{
			throw type.cast(ex.getCause());
		}
	}

	public <U> U reduce(U identity, CheckedBiFunction<U, ? super T, U> accumulator, CheckedBinaryOperator<U> combiner) throws E
	{
		try
		{

			return stream.reduce(identity, (a, b) ->
			{
				try
				{
					return accumulator.apply(a, b);
				} catch (Exception ex)
				{
					throw new CheckedExceptionWrapper(ex);
				}
			}, (a, b) ->
			{
				try
				{
					return combiner.apply(a, b);
				} catch (Exception ex)
				{
					throw new CheckedExceptionWrapper(ex);
				}
			});
		} catch (CheckedExceptionWrapper ex)
		{
			throw type.cast(ex.getCause());
		}
	}

	public <R> R collect(CheckedSupplier<R> supplier, CheckedBiConsumer<R, ? super T> accumulator, CheckedBiConsumer<R, R> combiner) throws E
	{
		try
		{
			return stream.collect(() ->
			{
				try
				{
					return supplier.get();
				} catch (Exception ex)
				{
					throw new CheckedExceptionWrapper(ex);
				}
			}, (a, b) ->
			{
				try
				{
					accumulator.accept(a, b);
				} catch (Exception ex)
				{
					throw new CheckedExceptionWrapper(ex);
				}
			}, (a, b) ->
			{
				try
				{
					combiner.accept(a, b);
				} catch (Exception ex)
				{
					throw new CheckedExceptionWrapper(ex);
				}
			});
		} catch (CheckedExceptionWrapper ex)
		{
			throw type.cast(ex.getCause());
		}
	}

	public <R, A> R collect(Collector<? super T, A, R> collector)
	{
		return stream.collect(collector);
	}

	public CheckedOptional<T, E> min(CheckedComparator<? super T> comparator) throws E
	{
		try
		{
			return CheckedOptional.of(type, stream.min((a, b) ->
			{
				try
				{
					return comparator.compare(a, b);
				} catch (Exception ex)
				{
					throw new CheckedExceptionWrapper(ex);
				}
			}));
		} catch (CheckedExceptionWrapper ex)
		{
			throw type.cast(ex.getCause());
		}
	}

	public CheckedOptional<T, E> max​(CheckedComparator<? super T> comparator) throws E
	{
		try
		{
			return CheckedOptional.of(type, stream.max((a, b) ->
			{
				try
				{
					return comparator.compare(a, b);
				} catch (Exception ex)
				{
					throw new CheckedExceptionWrapper(ex);
				}
			}));
		} catch (CheckedExceptionWrapper ex)
		{
			throw type.cast(ex.getCause());
		}
	}

	public long count()
	{
		return stream.count();
	}

	public boolean anyMatch​(CheckedPredicate<? super T> predicate) throws E
	{
		try
		{
			return stream.anyMatch​(e ->
			{
				try
				{
					return predicate.test(e);
				} catch (Exception ex)
				{
					throw new CheckedExceptionWrapper(ex);
				}
			});
		} catch (CheckedExceptionWrapper ex)
		{
			throw type.cast(ex.getCause());
		}
	}

	public boolean allMatch​(CheckedPredicate<? super T> predicate) throws E
	{
		try
		{
			return stream.allMatch​(e ->
			{
				try
				{
					return predicate.test(e);
				} catch (Exception ex)
				{
					throw new CheckedExceptionWrapper(ex);
				}
			});
		} catch (CheckedExceptionWrapper ex)
		{
			throw type.cast(ex.getCause());
		}
	}

	public boolean noneMatch​(CheckedPredicate<? super T> predicate) throws E
	{
		try
		{
			return stream.noneMatch(e ->
			{
				try
				{
					return predicate.test(e);
				} catch (Exception ex)
				{
					throw new CheckedExceptionWrapper(ex);
				}
			});
		} catch (CheckedExceptionWrapper ex)
		{
			throw type.cast(ex.getCause());
		}
	}

	public CheckedOptional<T, E> findFirst() throws E
	{
		return CheckedOptional.of(type, stream.findFirst());
	}

	public CheckedOptional<T, E> findAny() throws E
	{
		return CheckedOptional.of(type, stream.findAny());
	}
}
