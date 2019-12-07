package gate.error;

public class UncheckedConstraintViolationException extends RuntimeException
{

	private static final long serialVersionUID = 1L;

	public UncheckedConstraintViolationException(ConstraintViolationException cause)
	{
		super(cause);
	}

	@Override
	public ConstraintViolationException getCause()
	{
		return (ConstraintViolationException) super.getCause();
	}

	public static void execute(UncheckedConstraintViolationExceptionExecutor executor)
	{
		try
		{
			executor.execute();
		} catch (ConstraintViolationException ex)
		{
			throw new UncheckedConstraintViolationException(ex);
		}
	}

	public static <T> T execute(UncheckedConstraintViolationExceptionSupplier<T> executor)
	{
		try
		{
			return executor.execute();
		} catch (ConstraintViolationException ex)
		{
			throw new UncheckedConstraintViolationException(ex);
		}
	}

	public interface UncheckedConstraintViolationExceptionExecutor
	{

		void execute() throws ConstraintViolationException;
	}

	public interface UncheckedConstraintViolationExceptionSupplier<T>
	{

		T execute() throws ConstraintViolationException;
	}

}
