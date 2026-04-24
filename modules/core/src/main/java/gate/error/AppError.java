package gate.error;

import java.io.Serial;

public class AppError extends RuntimeException
{

	@Serial private static final long serialVersionUID = 1L;

	public AppError(Throwable cause)
	{
		super(cause);
	}

	public AppError(String message, Throwable cause)
	{
		super(message, cause);
	}
}