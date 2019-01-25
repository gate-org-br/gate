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
}
