package gate.handler;

import gate.converter.Converter;
import gate.error.AppError;
import gate.lang.property.Entity;
import gate.lang.property.Property;
import gate.util.Toolkit;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ApplicationScoped
public class OptionHandler implements Handler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value) throws AppError {
		Objects.requireNonNull(value);

		String string = Toolkit.stream(value).map(this::javaToJson).collect(Collectors.joining(",", "[", "]"));
		byte[] bytes = string.getBytes(StandardCharsets.UTF_8);

		response.setCharacterEncoding("UTF-8");
		response.setContentLength(bytes.length);
		response.setContentType("application/json");

		try (OutputStream os = response.getOutputStream()) {
			os.write(bytes);
			os.flush();
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	private String javaToJson(Object object) {
		Objects.requireNonNull(object);
		Class<?> type = object.getClass();
		Property property = Property.getProperty(type, Entity.getId(type));
		Object id = property.getValue(object);
		String label = object.toString();
		String value = Converter.toString(id);
		return String.format("{\"label\":\"%s\",\"value\":\"%s\"}", label, value);
	}
}
