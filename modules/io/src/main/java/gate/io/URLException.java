package gate.io;

public class URLException extends RuntimeException
{

	private final int status;

	public URLException(int status, String message)
	{
		super(message);
		this.status = status;
	}

	public URLException(int status, String message, Throwable cause)
	{
		super(message, cause);
		this.status = status;
	}

	public URLException(int status, Throwable cause)
	{
		super(cause);
		this.status = status;
	}

	public int getStatus()
	{
		return status;
	}
}
