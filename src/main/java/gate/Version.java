package gate;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

@WebServlet("/Version")
public class Version extends HttpServlet
{

	private static final long serialVersionUID = 1L;

	@Inject
	private gate.type.Version version;

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		try (Writer writer = response.getWriter())
		{
			writer.write(version.toString());
			writer.flush();
		}
	}

}
