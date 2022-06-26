package gate;

import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

@ApplicationPath("/gate")
public class Service extends Application
{

	public static Response getFile(HttpServletRequest request, String filename)
	{
		return Response.ok((StreamingOutput) (OutputStream o) ->
		{
			try ( InputStream i = Service.class.getResourceAsStream(filename))
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
