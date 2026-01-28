package gate.error;

import gate.annotation.Catcher;
import gate.catcher.HttpExceptionCatcher;
import gate.type.RequestCommand;
import jakarta.servlet.http.HttpServletResponse;

@Catcher(HttpExceptionCatcher.class)
public class BadRequestException extends HttpException
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

	public BadRequestException(Throwable cause)
	{
		super(cause);
	}

	public BadRequestException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public BadRequestException(RequestCommand requestCommand)
	{
		super(String.format("Requisição inválida: MODULE=%s, SCREEN=%s, ACTION=%s",
			requestCommand.module(), requestCommand.screen(), requestCommand.action()));
	}

	@Override
	public int getStatusCode()
	{
		return HttpServletResponse.SC_BAD_REQUEST;
	}

}
