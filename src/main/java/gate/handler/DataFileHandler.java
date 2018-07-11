package gate.handler;

import gate.error.AppError;
import gate.type.DataFile;
import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DataFileHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request,
			   HttpServletResponse response, Object value) throws AppError
	{
		DataFile dataFile = (DataFile) value;

		response.setContentType("application/octet-stream");
		response.setContentLength(dataFile.getData().length);
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", dataFile.getName()));

		try (OutputStream os = response.getOutputStream())
		{
			os.write(dataFile.getData());
			os.flush();
		} catch (Exception e)
		{
			throw new AppError(e);
		}
	}
}
