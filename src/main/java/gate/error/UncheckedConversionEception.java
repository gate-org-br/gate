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

	public static <T> T wrap(UncheckedConversionEceptionWrapper<T> supplier)
	{
		try
		{
			return supplier.get();
		} catch (ConversionException ex)
		{
			throw new UncheckedConversionEception(ex);
		}
	}

	public interface UncheckedConversionEceptionWrapper<T>
	{

		T get() throws ConversionException;
	}

}
