package gate.error;

public class TemplateException extends EvaluableException
{

	private static final long serialVersionUID = 1L;

	public TemplateException(String message)
	{
		super(message);
	}

	public TemplateException(String message, Object... args)
	{
		super(String.format(message, args));
	}
}
