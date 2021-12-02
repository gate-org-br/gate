package gate;

import gate.base.Screen;
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;

public interface Runner
{

	public void execute(Progress progress, HttpServletRequest request, Screen screen, Method method);
}
