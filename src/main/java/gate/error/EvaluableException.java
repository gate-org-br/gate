package gate.error;

public class EvaluableException extends Exception
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
}
