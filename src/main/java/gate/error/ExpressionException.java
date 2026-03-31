package gate.error;

import java.io.Serial;

public class ExpressionException extends EvaluableException
{

	@Serial private static final long serialVersionUID = 1L;

	public ExpressionException(String message)
	{
		super(message);
	}

	public ExpressionException(String message, Object... args)
	{
		super(String.format(message, args));
	}
}