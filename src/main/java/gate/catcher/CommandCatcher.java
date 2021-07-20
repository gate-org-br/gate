package gate.catcher;

import gate.error.AppException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CommandCatcher implements Catcher
{

	@Override
	public void execute(HttpServletRequest request,
		HttpServletResponse response,
		AppException exception)
	{

		response.setContentType("text/html");
		try (Writer writer = response.getWriter())
		{

			writer.write("<!DOCTYPE HTML>");
			writer.write("<html>");
			writer.write("    <head>");
			writer.write("        <meta charset='UTF-8'>");
			writer.write("        <title>Hide Signal</title>");
			writer.write("        <script src='app/resources/Gate.js'></script>");
			writer.write("    </head>");
			writer.write("    <body>");
			writer.write("        <script>");
			writer.write("            window.addEventListener('load', () =>");
			writer.write("            {");

			writer.write("alert('" + exception.getMessage().replace("'", "\"") + "');");

			writer.write("                GDialog.hide();");
			writer.write("            });");
			writer.write("        </script>");
			writer.write("    </body>");
			writer.write("</html>");
			writer.flush();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

}
