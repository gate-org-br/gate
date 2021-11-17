package gate.tags;

import gate.converter.Converter;
import gate.error.ConversionException;
import gate.tags.ParameterTag;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

public class THTag extends ParameterTag
{

	private Object value;
	private String format;
	private String empty;
	private String ordenate;
	private String method;

	@Inject
	private HttpServletRequest request;

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

		if (method == null)
			method = request.getMethod().toUpperCase();
		getJspContext().getOut().print(getAttributes().isEmpty() ? "<th>" : "<th " + getAttributes().toString() + " >");

		if (ordenate != null)
		{
			final String ordenateDesc = "-" + ordenate;

			getParameters().put(request.getQueryString());
			String orderBy = request.getParameter("orderBy");
			getParameters().remove("orderBy");

			getJspContext().getOut().write("POST".equalsIgnoreCase(method) ? "<button formaction='Gate?" : "<a href='Gate?");

			if (ordenate.equals(orderBy))
			{
				getParameters().put("orderBy", ordenateDesc);
				getJspContext().getOut().write(getParameters().toString() + "'>&uarr;");
			} else if (ordenateDesc.equals(orderBy))
			{
				getJspContext().getOut().write(getParameters().toString() + "'>&darr;");
			} else
			{
				getParameters().put("orderBy", ordenate);
				getJspContext().getOut().write(getParameters().toString() + "'>");
			}

		}

		if (getJspBody() == null)
		{
			String string = format != null ? Converter.toText(value, format) : Converter.toText(value);

			if (string.isEmpty() && empty != null)
				string = empty;
			string = string.replaceAll("\\n", "<br/>");
			getJspContext().getOut().print(string);
		} else
			getJspBody().invoke(null);

		if (ordenate != null)
			getJspContext().getOut().write("POST".equalsIgnoreCase(request.getMethod()) ? "</button>" : "</a>");

		getJspContext().getOut().print("</th>");
	}

	public void setMethod(String method)
	{
		this.method = method;
	}

	public void setArguments(String arguments) throws ConversionException
	{
		getParameters().put(arguments);
	}

	public void setFormat(String format)
	{
		this.format = format;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}

	public void setEmpty(String emptyText)
	{
		this.empty = emptyText;
	}

	public void setOrdenate(String ordenate)
	{
		this.ordenate = ordenate;
	}
}
