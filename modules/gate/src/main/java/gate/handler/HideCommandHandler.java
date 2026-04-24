package gate.handler;

import gate.command.HideCommand;
import gate.function.TryConsumer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;

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
			writer.write("                import GDialog from './gate/g-dialog.js';");
			signal.getMessages().stream()
					.map(e -> e.replace("'", "\""))
					.map(e -> "alert('" + e + "');")
					.forEach(TryConsumer.wrap(writer::write));
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