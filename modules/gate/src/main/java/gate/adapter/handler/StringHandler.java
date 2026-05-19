package gate.adapter.handler;

import gate.Progress;
import gate.error.ConversionException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class StringHandler implements Handler
{
	@Inject
	private HTMLCommandHandler htmlHandler;

	@Inject
	private TextHandler textHandler;

	@Inject
	private JavaScriptHandler jsHandler;

	@Override
	public Object ofPart(Class<?> type, Part part) throws ConversionException
	{
		try (var is = part.getInputStream())
		{
			return new String(is.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{
		String string = value.toString();
		if (string.endsWith(".html"))
			htmlHandler.handle(request, response, string);
		else if (string.endsWith(".js")
		         || string.endsWith(".mjs"))
			jsHandler.handle(request, response, string);
		else
			textHandler.handle(request, response, string);

	}

	@Override
	public void handle(HttpServletRequest request,
	                   Progress progress, Object value)
	{
		String string = value.toString();
		if (string.endsWith(".html"))
			htmlHandler.handle(request, progress, string);
		else if (string.endsWith(".js")
		         || string.endsWith(".mjs"))
			jsHandler.handle(request, progress, string);
		else
			textHandler.handle(request, progress, string);
	}
}