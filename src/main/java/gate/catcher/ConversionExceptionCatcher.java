package gate.catcher;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConversionExceptionCatcher extends HttpExceptionCatcher
{

	public ConversionExceptionCatcher()
	{
		super(400);
	}

}
