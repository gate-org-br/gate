package gate.catcher;

import gate.error.AppException;
import gate.error.FKViolationException;
import gate.error.NotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ObjectCatcher implements Catcher
{

	@Override
	public void execute(HttpServletRequest request,
		HttpServletResponse response,
		AppException exception)
	{

		if (exception instanceof NotFoundException)
			new HideCommandCatcher().execute(request, response, exception);

		if (exception instanceof FKViolationException)
			new ReloadCommandCatcher().execute(request, response, exception);

		new JSPCommandCatcher().execute(request, response, exception);

	}

}
