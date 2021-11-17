package gate.handler;

import gate.type.DataFile;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ApplicationScoped
public class DataFileHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{
		DataFile dataFile = (DataFile) value;

		response.setContentType("application/octet-stream");
		response.setContentLength(dataFile.getData().length);
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", dataFile.getName()));

		try ( OutputStream os = response.getOutputStream())
		{
			os.write(dataFile.getData());
			os.flush();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}
