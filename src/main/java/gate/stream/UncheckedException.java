package gate.stream;

public class UncheckedException extends RuntimeException
{
	
	public UncheckedException(Exception cause)
	{
		super(cause);
	}

	@Override
	public synchronized Exception getCause()
	{
		return (Exception) super.getCause();
	}
	
}
