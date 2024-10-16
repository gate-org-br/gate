package gate.stream;

import java.util.function.IntFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class UncheckedStream<T>
{

	private final Stream<T> stream;

	private UncheckedStream(Stream<T> stream)
	{
		this.stream = stream;
	}

	public static <T> UncheckedStream<T> of(Stream<T> stream)
	{
		return new UncheckedStream<>(stream);
	}

	public UncheckedStream<T> filter(CheckedPredicate<? super T> predicate)
	{
		return UncheckedStream.of(stream.filter(e ->
		{
			try
			{
				return predicate.test(e);
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		}));
	}

	public <R> UncheckedStream<R> map(CheckedFunction<? super T, ? extends R> mapper)
	{
		return UncheckedStream.of(stream.map(e ->
		{
			try
			{
				return mapper.apply(e);
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		}));
	}

	public DoubleStream mapToDouble​(CheckedToDoubleFunction<? super T> mapper)
	{
		return stream.mapToDouble(e ->
		{
			try
			{
				return mapper.applyAsDouble​(e);
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		});
	}

	public DoubleStream flatMapToDouble(CheckedFunction<? super T, ? extends DoubleStream> mapper)
	{
		return stream.flatMapToDouble(a ->
		{
			try
			{
				return mapper.apply(a);
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		});
	}

	public IntStream mapToInt(CheckedToIntFunction<? super T> mapper)
	{
		return stream.mapToInt(e ->
		{
			try
			{
				return mapper.applyAsInt(e);
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		});
	}

	public IntStream flatMapToInt(CheckedFunction<? super T, ? extends IntStream> mapper)
	{
		return stream.flatMapToInt(a ->
		{
			try
			{
				return mapper.apply(a);
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		});
	}

	public LongStream mapToLong(CheckedToLongFunction<? super T> mapper)
	{
		return stream.mapToLong(e ->
		{
			try
			{
				return mapper.applyAsLong(e);
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		});
	}

	public LongStream flatMapToLong(CheckedFunction<? super T, ? extends LongStream> mapper)
	{
		return stream.flatMapToLong(a ->
		{
			try
			{
				return mapper.apply(a);
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		});
	}

	public <R> UncheckedStream<R> flatMap(CheckedFunction<? super T, ? extends Stream<? extends R>> mapper)
	{
		return UncheckedStream.of(stream.flatMap(e ->
		{
			try
			{
				return mapper.apply(e);
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		}));
	}

	public UncheckedStream<T> distinct()
	{
		return UncheckedStream.of(stream.distinct());
	}

	public UncheckedStream<T> sorted​()
	{
		return UncheckedStream.of(stream.sorted());
	}

	public UncheckedStream<T> sorted​(CheckedComparator<? super T> comparator)
	{

		return UncheckedStream.of(stream).sorted​((a, b) ->
		{
			try
			{
				return comparator.compare(a, b);
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		});
	}

	public UncheckedStream<T> peek​(CheckedConsumer<? super T> action)
	{
		return UncheckedStream.of(stream.peek(e ->
		{
			try
			{
				action.accept(e);
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		}));
	}

	public UncheckedStream<T> limit(long maxSize)
	{
		return UncheckedStream.of(stream).limit(maxSize);
	}

	public UncheckedStream<T> skip(long n)
	{
		return UncheckedStream.of(stream).skip(n);
	}

	public UncheckedStream<T> takeWhile(CheckedPredicate<? super T> predicate)
	{
		return UncheckedStream.of(stream.takeWhile(e ->
		{
			try
			{
				return predicate.test(e);
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		}));
	}

	public UncheckedStream<T> dropWhile(CheckedPredicate<? super T> predicate)
	{
		return UncheckedStream.of(stream.dropWhile(e ->
		{
			try
			{
				return predicate.test(e);
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		}));
	}

	public void forEach(CheckedConsumer<? super T> action)
	{
		stream.forEach(e ->
		{
			try
			{
				action.accept(e);
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		});
	}

	public void forEachOrdered​(CheckedConsumer<? super T> action)
	{
		stream.forEach(e ->
		{
			try
			{
				action.accept(e);
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		});
	}

	public Object[] toArray()
	{
		return stream.toArray();
	}

	public <A> A[] toArray​(IntFunction<A[]> generator)
	{
		return stream.toArray(generator);
	}

	public T reduce​(T identity, CheckedBinaryOperator<T> accumulator)
	{
		return stream.reduce(identity, (a, b) ->
		{
			try
			{
				return accumulator.apply(a, b);
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		});
	}

	public UncheckedOptional<T> reduce​(CheckedBinaryOperator<T> accumulator)
	{
		return UncheckedOptional.of(stream.reduce((a, b) ->
		{
			try
			{
				return accumulator.apply(a, b);
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		}));
	}

	public <U> U reduce(U identity, CheckedBiFunction<U, ? super T, U> accumulator,
			CheckedBinaryOperator<U> combiner)
	{
		return stream.reduce(identity, (a, b) ->
		{
			try
			{
				return accumulator.apply(a, b);
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		}, (a, b) ->
		{
			try
			{
				return combiner.apply(a, b);
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		});
	}

	public <R> R collect(CheckedSupplier<R> supplier, CheckedBiConsumer<R, ? super T> accumulator,
			CheckedBiConsumer<R, R> combiner)
	{
		return stream.collect(() ->
		{
			try
			{
				return supplier.get();
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		}, (a, b) ->
		{
			try
			{
				accumulator.accept(a, b);
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		}, (a, b) ->
		{
			try
			{
				combiner.accept(a, b);
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		});
	}

	public <R, A> R collect(Collector<? super T, A, R> collector)
	{
		return stream.collect(collector);
	}

	public UncheckedOptional<T> min(CheckedComparator<? super T> comparator)
	{
		return UncheckedOptional.of(stream.min((a, b) ->
		{
			try
			{
				return comparator.compare(a, b);
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		}));
	}

	public UncheckedOptional<T> max​(CheckedComparator<? super T> comparator)
	{
		return UncheckedOptional.of(stream.max((a, b) ->
		{
			try
			{
				return comparator.compare(a, b);
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		}));
	}

	public long count()
	{
		return stream.count();
	}

	public boolean anyMatch​(CheckedPredicate<? super T> predicate)
	{
		return stream.anyMatch(e ->
		{
			try
			{
				return predicate.test(e);
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		});
	}

	public boolean allMatch​(CheckedPredicate<? super T> predicate)
	{
		return stream.allMatch(e ->
		{
			try
			{
				return predicate.test(e);
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		});
	}

	public boolean noneMatch​(CheckedPredicate<? super T> predicate)
	{
		return stream.noneMatch(e ->
		{
			try
			{
				return predicate.test(e);
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		});
	}

	public UncheckedOptional<T> findFirst()
	{
		return UncheckedOptional.of(stream.findFirst());
	}

	public UncheckedOptional<T> findAny()
	{
		return UncheckedOptional.of(stream.findAny());
	}
}
