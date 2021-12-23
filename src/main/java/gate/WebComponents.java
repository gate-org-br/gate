package gate;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

@ApplicationPath("gate")
public class WebComponents extends Application
{

	@Override
	public Set<Class<?>> getClasses()
	{
		return Set.of(Script.class, Style.class, Font.class);
	}

	@Path("script")
	public static class Script
	{

		@Inject
		private HttpServletRequest request;

		@GET
		@Path("/{file}")
		@Produces("text/javascript")
		public Response js(@PathParam("file") String file)
		{
			return getFile(request, file);
		}
	}

	@Path("style")
	public static class Style
	{

		@Inject
		private HttpServletRequest request;

		@GET
		@Path("/{file}")
		@Produces("text/css")
		public Response js(@PathParam("file") String file)
		{
			return getFile(request, file);
		}
	}

	@Path("font")
	public static class Font
	{

		@Inject
		private HttpServletRequest request;

		@GET
		@Path("Gate.ttf")
		@Produces("font/ttf")
		public Response getTTFFont()
		{
			return getFile(request, "Gate.ttf");
		}

		@GET
		@Path("Gate.eot")
		@Produces("application/vnd.ms-fontobject")
		public Response getEOTFont()
		{
			return getFile(request, "Gate.eot");
		}

		@GET
		@Path("Gate.woff")
		@Produces("application/font-woff")
		public Response getWoffFont()
		{
			return getFile(request, "Gate.woff");
		}

		@GET
		@Path("Gate.svg")
		@Produces("image/svg+xml")
		public Response getSVGFont()
		{
			return getFile(request, "Gate.svg");
		}

	}

	public static Response getFile(HttpServletRequest request, String filename)
	{
		return Response.ok((StreamingOutput) (OutputStream o) ->
		{
			try ( InputStream i = WebComponents.class.getResourceAsStream(filename))
			{
				for (int b = i.read(); b != -1; b = i.read())
					o.write(b);
				o.flush();
			}
		}).header("Content-Disposition", "attachment; filename=" + filename)
			.header("Access-Control-Allow-Origin", request.getHeader("Origin"))
			.header("Access-Control-Allow-Credentials", "true")
			.header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE")
			.header("Access-Control-Max-Age", "3600")
			.header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me")
			.build();
	}
}
