package gate.stream;

import java.util.Optional;
import java.util.function.Supplier;

public class CheckedOptional<T, E extends Exception>
{

	private final Class<E> type;
	private final Optional<T> optional;

	private CheckedOptional(Class<E> type, Optional<T> optional)
	{
		this.type = type;
		this.optional = optional;
	}

	public static <T, E extends Exception> CheckedOptional<T, E> of(Class<E> type, Optional<T> optional)
	{
		return new CheckedOptional<>(type, optional);
	}

	public T get()
	{
		return optional.get();
	}

	public boolean isPresent()
	{
		return optional.isPresent();
	}

	public boolean isEmpty()
	{
		return optional.isEmpty();
	}

	public void ifPresent​(CheckedConsumer<? super T> action) throws E
	{
		try
		{
			optional.ifPresent(e ->
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

	public void ifPresentOrElse​(CheckedConsumer<? super T> action, CheckedRunnable emptyAction) throws E
	{
		try
		{
			optional.ifPresentOrElse​(e ->
			{
				try
				{
					action.accept(e);
				} catch (Exception ex)
				{
					throw new CheckedExceptionWrapper(ex);
				}
			}, () ->
			{
				try
				{
					emptyAction.run();
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

	public Optional<T> filter​(CheckedPredicate<? super T> predicate) throws E
	{
		try
		{
			return optional.filter​(e ->
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

	public <U> Optional<U> map(CheckedFunction<? super T, ? extends U> mapper) throws E
	{
		try
		{
			return optional.map(e ->
			{
				try
				{
					return mapper.apply(e);
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

	public <U> Optional<U> flatMap(CheckedFunction<? super T, Optional<U>> mapper) throws E
	{
		try
		{
			return optional.flatMap(e ->
			{
				try
				{
					return mapper.apply(e);
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

	public Optional<T> or​(CheckedSupplier<? extends Optional<? extends T>> supplier) throws E
	{
		try
		{
			return optional.or​(() ->
			{
				try
				{
					return supplier.get();
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

	public CheckedStream<T, E> stream()
	{
		return CheckedStream.of(type, optional.stream());
	}

	public T orElse(T other)
	{
		return optional.orElse(other);
	}

	public T orElseGet(CheckedSupplier<? extends T> other) throws E
	{
		try
		{
			return optional.orElseGet(() ->
			{
				try
				{
					return other.get();
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

	public T orElseThrow()
	{
		return optional.orElseThrow();
	}

	public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X
	{
		return optional.orElseThrow(exceptionSupplier);
	}

	@Override
	public int hashCode()
	{
		return optional.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof CheckedOptional
			&& ((CheckedOptional) obj).type.equals(type)
			&& ((CheckedOptional) obj).optional.equals(optional);
	}

	@Override
	public String toString()
	{
		return optional.toString();
	}

}
