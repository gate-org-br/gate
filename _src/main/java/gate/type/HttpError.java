package gate.type;

import gate.annotation.Handler;
import gate.handler.HttpErrorHandler;

@Handler(HttpErrorHandler.class)
public class HttpError
{

	private final Integer code;
	private final String message;

	public HttpError(Integer code, String message)
	{
		super();
		this.code = code;
		this.message = message;
	}

	public Integer getCode()
	{
		return code;
	}

	public String getMessage()
	{
		return message;
	}
}
