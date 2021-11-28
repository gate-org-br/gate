package gate;

import java.io.IOException;
import java.io.Writer;
import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Version")
public class Version extends HttpServlet
{

	private static final long serialVersionUID = 1L;

	@Inject
	private gate.type.Version version;

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		try ( Writer writer = response.getWriter())
		{
			writer.write(version.toString());
			writer.flush();
		}
	}

}
