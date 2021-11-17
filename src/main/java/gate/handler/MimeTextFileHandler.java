package gate.handler;

import gate.error.AppError;
import gate.type.mime.MimeTextFile;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

@ApplicationScoped
public class MimeTextFileHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value) throws AppError
	{
		try
		{
			MimeTextFile mimeTextFile = (MimeTextFile) value;
			response.setCharacterEncoding(mimeTextFile.getCharset());

			response.setContentType(String.format("%s/%s; charset=%s",
				mimeTextFile.getType(),
				mimeTextFile.getSubType(),
				mimeTextFile.getCharset()));

			response.setHeader("Content-Disposition",
				String.format("attachment; filename=\"%s\"",
					mimeTextFile.getName()));

			byte[] bytes = mimeTextFile.getText().getBytes(mimeTextFile.getCharset());
			response.setContentLength(bytes.length);

			try ( OutputStream os = response.getOutputStream())
			{
				os.write(bytes);
				os.flush();
			}
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}
