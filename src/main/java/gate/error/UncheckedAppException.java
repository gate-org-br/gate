package gate.error;

public class UncheckedAppException extends RuntimeException
{

	public UncheckedAppException(AppException cause)
	{
		super(cause);
	}

	@Override
	public AppException getCause()
	{
		return (AppException) super.getCause();
	}

	public static void execute(UncheckedAppExceptionExecutor executor)
	{
		try
		{
			executor.execute();
		} catch (AppException ex)
		{
			throw new UncheckedAppException(ex);
		}
	}

	public static <T> T execute(UncheckedAppExceptionSupplier<T> executor)
	{
		try
		{
			return executor.execute();
		} catch (AppException ex)
		{
			throw new UncheckedAppException(ex);
		}
	}

	public interface UncheckedAppExceptionExecutor
	{

		void execute() throws AppException;
	}

	public interface UncheckedAppExceptionSupplier<T>
	{

		T execute() throws AppException;
	}
}
