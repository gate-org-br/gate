package gate.catcher;

import gate.error.AppException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Catcher
{

	public void execute(HttpServletRequest request,
		HttpServletResponse response,
		AppException exception);
}
