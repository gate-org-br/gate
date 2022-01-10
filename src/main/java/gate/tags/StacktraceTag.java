package gate.tags;

import gate.lang.json.JsonArray;
import gate.lang.json.JsonObject;
import gate.lang.json.JsonString;
import java.io.IOException;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StacktraceTag extends AttributeTag
{

	private Throwable exception;

	public void setException(Throwable exception)
	{
		this.exception = exception;
	}

	@Override
	public void doTag() throws IOException
	{
		JsonArray array = new JsonArray();
		for (Throwable error = this.exception; error != null; error = error.getCause())
		{
			JsonObject object = new JsonObject();
			object.setString("message", error.getMessage());
			object.set("stacktrace", Stream.of(error.getStackTrace())
				.map(StackTraceElement::toString)
				.map(JsonString::of)
				.collect(Collectors.toCollection(JsonArray::new)));
			array.add(object);
		}

		StringJoiner string = new StringJoiner(System.lineSeparator());
		string.add("<script type='module'>");
		string.add("import GStacktrace from './gate/script/g-stacktrace.mjs';");
		string.add("GStacktrace.show('Erro de sistema', " + array.toString() + ");");
		string.add("</script>");
	}
}
