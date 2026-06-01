package gate.adapter.catcher;

import gate.error.UnauthorizedException;
import gate.http.ScreenServletRequest;
import gate.http.ScreenServletResponse;
import gate.lang.contentType.ContentType;
import gate.lang.json.JsonArray;
import gate.lang.json.JsonObject;
import gate.util.SystemProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class ThrowableCatcher implements Catcher
{
	@Inject
	Logger logger;

	@Inject
	UnauthorizedExceptionCatcher unauthorizedExceptionCatcher;

	private static final boolean DEV_MODE = SystemProperty.get("gate.dev-mode")
			.orElse("false")
			.equals("true");

	@Override
	public void catches(ScreenServletRequest request,
	                    ScreenServletResponse response, Throwable exception)
	{
		if (exception == null)
			return;

		for (Throwable current = exception; current != null; current = current.getCause())
			if (current instanceof UnauthorizedException)
			{
				unauthorizedExceptionCatcher.catches(request, response, current);
				return;
			}

		response.setStatus(500);
		response.setHeader(HttpHeaders.CONTENT_TYPE,
				DEV_MODE ? ContentType.APPLICATION_JSON.toString()
						: ContentType.TEXT_PLAIN.toString());

		try (PrintWriter writer = response.getWriter())
		{
			writer.write(DEV_MODE ? new JsonObject()
									.setInt("status", 500)
									.setString("method", request.getMethod())
									.setString("path", request.getRequestURI())
									.setString("queryString", request.getQueryString())
									.setString("timestamp", Instant.now().toString())
									.set("errors", Stream.iterate(exception,
											Objects::nonNull, Throwable::getCause)
												   .map(e -> new JsonObject()
															 .setString("type", e.getClass().getName())
															 .setString("simpleType", e.getClass().getSimpleName())
															 .setString("message", e.getMessage())
															 .set("stackTrace", Stream.of(e.getStackTrace())
																				.map(element -> new JsonObject()
																								.setString("className",
					                                                                                    element.getClassName())
																								.setString("methodName",
																										element.getMethodName())
																								.setString("fileName",
																										element.getFileName())
																								.setInt("lineNumber",
																										element.getLineNumber())
																								.setBoolean(
																										"nativeMethod",
																										element.isNativeMethod())
																								.setString("text",
																										element.toString()))
																				.collect(JsonArray::new,
																						JsonArray::add,
																						JsonArray::addAll)))
												   .collect(Collectors.toCollection(JsonArray::new))).toString()
					: "Erro de sistema: procure o suporte para informar o ocorrido"
			);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		} finally
		{
			logger.error(exception.getMessage(), exception);
		}
	}
}