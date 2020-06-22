package gate.error;

public class ExpressionException extends EvaluableException
{

	private static final long serialVersionUID = 1L;

	public ExpressionException(String message)
	{
		super(message);
	}

	public ExpressionException(String message, Object... args)
	{
		super(String.format(message, args));
	}
}
