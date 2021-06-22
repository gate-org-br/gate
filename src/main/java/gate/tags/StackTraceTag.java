package gate.tags;

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
		for (Throwable error = this.exception;
			error != null;
			error = error.getCause())
		{
			getJspContext().getOut().print("<tr><td style='font-weight: bold'>");
			getJspContext().getOut().print(escapeHTML(error.toString()));
			getJspContext().getOut().print("</td></tr>");
			getJspContext().getOut().print("<tr><td>");
			getJspContext().getOut().print(Stream.of(error.getStackTrace())
				.map(StackTraceElement::toString).collect(Collectors.joining("<br/>")));
			getJspContext().getOut().print("</td></tr>");
		}
		getJspContext().getOut().print("</tbody></table>");
		getJspContext().getOut().print("</div>");
	}

	public static String escapeHTML(String s)
	{
		StringBuilder out = new StringBuilder(Math.max(16, s.length()));
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			if (c > 127
				|| c == '"'
				|| c == '\''
				|| c == '<'
				|| c == '>'
				|| c == '&')
				out.append("&#")
					.append((int) c)
					.append(';');
			else
				out.append(c);
		}
		return out.toString();
	}
}
