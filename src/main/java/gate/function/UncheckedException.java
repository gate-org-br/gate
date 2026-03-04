package gate.function;

/**
 * Runtime wrapper used to rethrow checked exceptions from Try* wrappers.
 */
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
