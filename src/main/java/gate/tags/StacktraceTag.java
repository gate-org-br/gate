package gate.tags;

import java.io.IOException;
import java.util.StringJoiner;
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
		StringJoiner string = new StringJoiner(System.lineSeparator());
		string.add("<ul class='TreeView'>");
		for (Throwable error = exception; error != null; error = error.getCause())
		{
			string.add("<li>");
			string.add(error.getMessage());
			string.add("<ul>");
			Stream.of(error.getStackTrace())
				.map(StackTraceElement::toString)
				.forEach(e -> string.add("<li>").add(e).add("</li>"));
			string.add("</ul>");
			string.add("</li>");
		}
		string.add("</ul>");
		getJspContext().getOut().println(string);
	}
}
