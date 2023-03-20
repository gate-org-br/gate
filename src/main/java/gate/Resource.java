package gate;

import gate.util.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(value = "/gate/*")
public class Resource extends HttpServlet
{

	@Override
	public void service(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException
	{
		List<String> path
			= Toolkit.parsePath(request.getPathInfo());
		if (!path.isEmpty())
		{
			String filename = path.get(0);

			response.setHeader("Content-Type", getContentType(filename));
			response.setHeader("Cache-Control", "public, max-age=86400");
			response.setHeader("Content-Disposition", "attachment; filename=" + filename);
			response.setHeader("Access-Control-Max-Age", "3600");
			response.setHeader("Access-Control-Allow-Credentials", "true");
			response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
			response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
			response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me");

			try (OutputStream o = response.getOutputStream();
				InputStream i = Resource.class.getResourceAsStream(filename))
			{
				for (int b = i.read(); b != -1; b = i.read())
					o.write(b);
				o.flush();
			}
		}
	}

	private String getContentType(String filename) throws IOException
	{
		if (filename.endsWith(".js"))
			return "text/javascript; charset=utf-8";
		if (filename.endsWith(".mjs"))
			return "text/javascript; charset=utf-8";
		if (filename.endsWith(".css"))
			return "text/css; charset=utf-8";
		if (filename.endsWith(".eot"))
			return "application/octet-stream";
		if (filename.endsWith(".svg"))
			return "application/octet-stream";
		if (filename.endsWith(".ttf"))
			return "application/octet-stream";
		if (filename.endsWith(".woff"))
			return "application/octet-stream";
		throw new IOException("Invalid file type");
	}
}