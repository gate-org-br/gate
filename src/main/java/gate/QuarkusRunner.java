package gate;

import gate.base.Screen;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.slf4j.Logger;

public class QuarkusRunner implements Runner
{

	@Inject
	Logger logger;

	@Inject
	ManagedExecutor managedExecutor;

	@Override
	public void execute(Progress progress, HttpServletRequest request, Screen screen, Method method)
	{
		managedExecutor.runAsync(() ->
		{
			Progress.bind(progress);
			try
			{
				Thread.sleep(1000);
				Object url = screen.execute(method);
				if (url != null)
					Progress.redirect(url.toString());
			} catch (InvocationTargetException ex)
			{
				if (Progress.Status.PENDING.equals(Progress.status()))
					Progress.cancel(ex.getCause().getMessage());
				else
					Progress.message(ex.getCause().getMessage());
			} catch (RuntimeException | IllegalAccessException | InterruptedException ex)
			{
				if (Progress.Status.PENDING.equals(Progress.status()))
					Progress.cancel(ex.getMessage());
				else
					Progress.message(ex.getMessage());
				logger.error(ex.getMessage(), ex);
			} finally
			{
				try
				{
					Thread.sleep(5000);
					Progress.dispose();
				} catch (InterruptedException ex)
				{
					Progress.dispose();
				}
			}
		});

	}
}
