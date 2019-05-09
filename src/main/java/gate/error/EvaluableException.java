package gate.error;

public class EvaluableException extends RuntimeException
{

	private static final long serialVersionUID = 1L;

	EvaluableException(String message)
	{
		super(message);
	}

	EvaluableException(String message, Object... args)
	{
		super(String.format(message, args));
	}

	EvaluableException(Throwable cause, String message)
	{
		super(message, cause);
	}

	EvaluableException(Throwable cause, String message, Object... args)
	{
		super(String.format(message, args), cause);
	}

}
