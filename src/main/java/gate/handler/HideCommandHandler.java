package gate.handler;

import gate.stream.CheckedStream;
import gate.type.HideCommand;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HideCommandHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{
		HideCommand signal = (HideCommand) value;

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

			CheckedStream.of(IOException.class,
				signal.getMessages().stream())
				.map(e -> e.replace("'", "\""))
				.map(e -> "alert('" + e + "');")
				.forEach(writer::write);

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
