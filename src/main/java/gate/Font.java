package gate;

import static gate.Service.getFile;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/font")
public class Font
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
