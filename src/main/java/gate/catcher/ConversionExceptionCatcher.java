package gate.catcher;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConversionExceptionCatcher extends HttpExceptionCatcher
{

	public ConversionExceptionCatcher()
	{
		super(400);
	}

}
