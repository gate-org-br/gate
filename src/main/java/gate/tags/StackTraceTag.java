package gate.tags;

import gate.util.Toolkit;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StackTraceTag extends AttributeTag
{

	private Throwable exception;

	public void setException(Throwable exception)
	{
		this.exception = exception;
	}

	@Override
	public void doTag() throws IOException
	{
		getJspContext().getOut().print("<div title='Erro de sistema' data-popup " + getAttributes().toString() + ">");
		getJspContext().getOut().print("<table><tbody>");
		for (Throwable error = this.exception; error != null; error = error.getCause())
		{
			getJspContext().getOut().print("<tr><td style='font-weight: bold'>");
			getJspContext().getOut().print(Toolkit.escapeHTML(error.toString()));
			getJspContext().getOut().print("</td></tr>");
			getJspContext().getOut().print("<tr><td>");
			getJspContext().getOut().print(Stream.of(error.getStackTrace()).map(StackTraceElement::toString)
				.collect(Collectors.joining("<br/>")));
			getJspContext().getOut().print("</td></tr>");
		}
		getJspContext().getOut().print("</tbody></table>");
		getJspContext().getOut().print("</div>");
	}
}
