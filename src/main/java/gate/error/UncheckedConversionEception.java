package gate.error;

import java.io.UncheckedIOException;

public class UncheckedConversionEception extends UncheckedIOException
{

	private static final long serialVersionUID = 1L;

	public UncheckedConversionEception(ConversionException cause)
	{
		super(cause);
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
			throw new UncheckedConversionEception(ex);
		}
	}

	public static <T> T execute(UncheckedConversionEceptionSupplier<T> executor)
	{
		try
		{
			return executor.get();
		} catch (ConversionException ex)
		{
			throw new UncheckedConversionEception(ex);
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
