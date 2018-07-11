package gate.handler;

import gate.error.AppError;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FileHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request,
					   HttpServletResponse response, Object value) throws AppError
	{
		try
		{
			java.io.File file = (java.io.File) value;
			response.setContentLength((int) file.length());
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", String.format(
					"attachment; filename=\"%s\"", file.getName()));
			try (BufferedInputStream is = new BufferedInputStream(
					new FileInputStream(file)))
			{
				try (BufferedOutputStream os = new BufferedOutputStream(
						response.getOutputStream()))
				{
					for (int data = is.read(); data != -1; data = is.read())
						os.write(data);
				}
			}
		} catch (Exception e)
		{
			throw new AppError(e);
		}
	}
}
