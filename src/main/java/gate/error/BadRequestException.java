package gate.error;

import gate.annotation.Catcher;
import gate.catcher.BatdRequestExceptionCatcher;

@Catcher(BatdRequestExceptionCatcher.class)
public class BadRequestException extends AppException
{

	private static final long serialVersionUID = 1L;

	public BadRequestException()
	{
		super("Bad request");
	}

	public BadRequestException(String message)
	{
		super(message);
	}

	public BadRequestException(String module, String screen, String action)
	{
		super(String.format("Requisição inválida: MODULE=%s, SCREEN=%s, ACTION=%s",
			module, screen, action));
	}

}
