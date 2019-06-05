package gate;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

@ApplicationPath("app")
public class App extends Application
{

	@Override
	public Set<Class<?>> getClasses()
	{
		return new HashSet<>(Collections.singletonList(Resources.class));
	}

	@Path("resources")
	public static class Resources
	{

		@GET
		@Path("Gate.css")
		@Produces("text/css")
		public Response getStyles()
		{
			return getFile("Gate.css");
		}

		@GET
		@Path("Gate.js")
		@Produces("text/javascript")
		public Response getScripts()
		{
			return getFile("Gate.js");
		}

		@GET
		@Path("Gate.ttf")
		@Produces("font/ttf")
		public Response getTTFFont()
		{
			return getFile("Gate.ttf");
		}

		@GET
		@Path("Gate.eot")
		@Produces("application/vnd.ms-fontobject")
		public Response getEOTFont()
		{
			return getFile("Gate.eot");
		}

		@GET
		@Path("Gate.woff")
		@Produces("application/font-woff")
		public Response getWoffFont()
		{
			return getFile("Gate.woff");
		}

		@GET
		@Path("Gate.svg")
		@Produces("image/svg+xml")
		public Response getSVGFont()
		{
			return getFile("Gate.svg");
		}

		@GET
		@Path("echarts.min.js")
		@Produces("text/javascript")
		public Response getECharts()
		{
			return getFile("echarts.min.js");
		}

		public Response getFile(String filename)
		{
			return Response.ok((StreamingOutput) (OutputStream o) ->
			{
				try (InputStream i = getClass().getResourceAsStream(filename))
				{
					for (int b = i.read(); b != -1; b = i.read())
						o.write(b);
					o.flush();
				}
			}).header("Content-Disposition", "attachment; filename=" + filename).build();
		}
	}
}
