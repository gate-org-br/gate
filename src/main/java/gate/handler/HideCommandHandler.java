package gate.handler;

import gate.command.HideCommand;
import gate.stream.CheckedStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ApplicationScoped
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
			writer.write("    </head>");
			writer.write("    <body>");
			writer.write("        <script type='module'>");
			writer.write("                import GDialog from './gate/script/g-dialog.mjs';");

			CheckedStream.of(IOException.class,
				signal.getMessages().stream())
				.map(e -> e.replace("'", "\""))
				.map(e -> "alert('" + e + "');")
				.forEach(writer::write);

			writer.write("                GDialog.hide();");
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
