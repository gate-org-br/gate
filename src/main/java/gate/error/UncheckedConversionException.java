package gate.error;

import java.io.UncheckedIOException;

public class UncheckedConversionException extends UncheckedIOException
{

	private static final long serialVersionUID = 1L;

	public UncheckedConversionException(ConversionException cause)
	{
		super(cause);
	}

	public UncheckedConversionException(String message)
	{
		super(new ConversionException(message));
	}

	@Override
	public ConversionException getCause()
	{
		return (ConversionException) super.getCause();
	}

	public static void execute(UncheckedConversionEceptionExecutor executor)
	{
		try
		{
			executor.execute();
		} catch (ConversionException ex)
		{
			throw new UncheckedConversionException(ex);
		}
	}

	public static <T> T execute(UncheckedConversionEceptionSupplier<T> executor)
	{
		try
		{
			return executor.get();
		} catch (ConversionException ex)
		{
			throw new UncheckedConversionException(ex);
		}
	}

	public interface UncheckedConversionEceptionExecutor
	{

		void execute() throws ConversionException;
	}

	public interface UncheckedConversionEceptionSupplier<T>
	{

		T get() throws ConversionException;
	}
}
