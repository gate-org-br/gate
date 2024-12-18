package gate.error;

import gate.annotation.Catcher;
import gate.catcher.BadRequestExceptionCatcher;
import jakarta.servlet.http.HttpServletResponse;

@Catcher(BadRequestExceptionCatcher.class)
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

	public BadRequestException(String module, String screen, String action)
	{
		super(String.format("Requisição inválida: MODULE=%s, SCREEN=%s, ACTION=%s",
			module, screen, action));
	}

	@Override
	public int getStatusCode()
	{
		return HttpServletResponse.SC_BAD_REQUEST;
	}

}
