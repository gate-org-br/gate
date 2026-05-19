package gate;

import gate.annotation.Current;
import gate.entity.User;
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
	Instance<User> userInstance;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/event-stream");
		resp.setHeader("Cache-Control", "no-cache");
		resp.setHeader("Connection", "keep-alive");

		var user = userInstance.get();
		String uuid = req.getParameter("uuid");
		AsyncContext asyncContext = req.startAsync(req, resp);
		asyncContext.setTimeout(0);
		Progress.attach(asyncContext, user, uuid);
	}
}