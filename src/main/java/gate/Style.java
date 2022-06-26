package gate;

import static gate.Service.getFile;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/style")
public class Style
{

	@Inject
	private HttpServletRequest request;

	@GET
	@Path("/{file}")
	@Produces("text/css")
	public Response get(@PathParam("file") String file)
	{
		return getFile(request, file);
	}
}
