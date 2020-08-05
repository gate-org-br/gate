package gate.stream;

public class CheckedExceptionWrapper extends RuntimeException
{
	
	public CheckedExceptionWrapper(Exception cause)
	{
		super(cause);
	}

	@Override
	public synchronized Exception getCause()
	{
		return (Exception) super.getCause();
	}
	
}
