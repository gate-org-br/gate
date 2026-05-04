package gate;

import gate.annotation.Current;
import gate.entity.User;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = {"/Progress", "/progress"}, asyncSupported = true)
public class ProgressServlet extends HttpServlet
{
	@Inject
	@Current
	@RequestScoped
	Instance<User> userInstance;

	@Override
	@SuppressWarnings("resource")
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/event-stream");
		resp.setHeader("Cache-Control", "no-cache");
		resp.setHeader("Connection", "keep-alive");

		var user = userInstance.get();
		String uuid = req.getParameter("uuid");
		if (user != null && user.getId() != null
		    && uuid != null && !uuid.isBlank())
		{
			AsyncContext asyncContext = req.startAsync(req, resp);
			asyncContext.setTimeout(0);
			try
			{
				Progress.attach(user.getId(), uuid, asyncContext);
			} catch (IOException | RuntimeException ex)
			{
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
				asyncContext.complete();
			}
		} else
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
	}
}