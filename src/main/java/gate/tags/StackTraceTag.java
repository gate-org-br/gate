package gate.tags;

import java.io.IOException;
import javax.servlet.jsp.JspException;

public class StackTraceTag extends DynamicAttributeTag
{

	private Throwable exception;

	public void setException(Throwable exception)
	{
		this.exception = exception;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		getJspContext().getOut().print("<div " + getAttributes().toString() + ">");
		getJspContext().getOut().print("    <div class='TEXT'>");
		getJspContext().getOut().print("        <h1>");
		getJspContext().getOut().print(exception.toString());
		getJspContext().getOut().print("        </h1>");
		getJspContext().getOut().print("    </div>");
		getJspContext().getOut().print("    <table>");
		getJspContext().getOut().print("        <caption>");
		getJspContext().getOut().print("            STACKTRACE");
		getJspContext().getOut().print("        </caption>");
		getJspContext().getOut().print("        <tbody>");
		print(0, exception);
		getJspContext().getOut().print("        </tbody>");
		getJspContext().getOut().print("    </table>");
		getJspContext().getOut().print("</div>");
	}

	public void print(int depth, Throwable exception) throws IOException
	{
		for (StackTraceElement stackTraceElement : exception.getStackTrace())
		{
			getJspContext().getOut().print("        <tr>");
			getJspContext().getOut().print(String.format("            <td style='padding-left: %dpx'>", depth * 40));
			getJspContext().getOut().print(stackTraceElement.toString());
			getJspContext().getOut().print("            </td>");
			getJspContext().getOut().print("        </tr>");
		}

		if (exception.getCause() != null)
			print(depth + 1, exception.getCause());
	}
}
