package gate.stream;

import java.util.Optional;
import java.util.function.Supplier;

public class UncheckedOptional<T>
{

	private final Optional<T> optional;

	private UncheckedOptional(Optional<T> optional)
	{
		this.optional = optional;
	}

	public static <T> UncheckedOptional<T> of(Optional<T> optional)
	{
		return new UncheckedOptional<>(optional);
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

	public void ifPresent​(CheckedConsumer<? super T> action) throws UncheckedException
	{
		optional.ifPresent(e ->
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

	public void ifPresentOrElse​(CheckedConsumer<? super T> action, CheckedRunnable emptyAction)
	{
		optional.ifPresentOrElse(e ->
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
		}, () ->
		{
			try
			{
				emptyAction.run();
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		});
	}

	public UncheckedOptional<T> filter​(CheckedPredicate<? super T> predicate)
	{
		return UncheckedOptional.of(optional.filter(e ->
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

	public <U> UncheckedOptional<U> map(CheckedFunction<? super T, ? extends U> mapper)
	{
		return UncheckedOptional.of(optional.map(e ->
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

	public <U> UncheckedOptional<U> flatMap(CheckedFunction<? super T, Optional<U>> mapper)
	{
		return UncheckedOptional.of(optional.flatMap(e ->
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

	public UncheckedOptional<T> or​(CheckedSupplier<? extends Optional<? extends T>> supplier)
	{
		return UncheckedOptional.of(optional.or(() ->
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
		}));
	}

	public UncheckedStream<T> stream()
	{
		return UncheckedStream.of(optional.stream());
	}

	public T orElse(T other)
	{
		return optional.orElse(other);
	}

	public T orElseGet(CheckedSupplier<? extends T> other)
	{
		return optional.orElseGet(() ->
		{
			try
			{
				return other.get();
			} catch (RuntimeException ex)
			{
				throw ex;
			} catch (Exception ex)
			{
				throw new UncheckedException(ex);
			}
		});
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
		return obj instanceof UncheckedOptional<?>
				&& ((UncheckedOptional<?>) obj).optional.equals(optional);
	}

	@Override
	public String toString()
	{
		return optional.toString();
	}

}
