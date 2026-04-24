package gate.error;

import java.io.Serial;

public class TemplateException extends EvaluableException
{

	@Serial private static final long serialVersionUID = 1L;

	public TemplateException(String message)
	{
		super(message);
	}

	public TemplateException(String message, Object... args)
	{
		super(message, args);
	}

	public TemplateException(Throwable cause, String message)
	{
		super(cause, message);
	}

	public TemplateException(Throwable cause, String message, Object... args)
	{
		super(cause, message, args);
	}
}