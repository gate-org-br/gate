package gate.handler;

import gate.error.AppError;
import gate.type.mime.MimeDataFile;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ApplicationScoped
public class MimeDataFileHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value) throws AppError
	{
		MimeDataFile mimeDataFile = (MimeDataFile) value;

		response.setContentLength(mimeDataFile.getData().length);

		response.setContentType(String.format("%s/%s",
			mimeDataFile.getType(),
			mimeDataFile.getSubType()));

		response.setHeader("Content-Disposition",
			String.format("attachment; filename=\"%s\"",
				mimeDataFile.getName()));

		try ( OutputStream os = response.getOutputStream())
		{
			os.write(mimeDataFile.getData());
			os.flush();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}
