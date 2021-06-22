package gate.tags;

import gate.error.ConversionException;
import gate.util.Parameters;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

public class OrdenatorTag extends AttributeTag
{

	private String method;
	private String property;
	private Parameters arguments;

	@Inject
	private HttpServletRequest request;

	@Override
	public void doTag() throws JspException, IOException
	{
		if (method == null)
			method = request
				.getMethod().toUpperCase();

		String orderBy = request.getParameter("orderBy");
		Parameters queryString = Parameters.parse(request.getQueryString());
		if (arguments != null)
			queryString.putAll(arguments);
		queryString.remove("orderBy");

		String desc = "-" + property;

		if ("POST".equalsIgnoreCase(method))
		{
			if (property.equals(orderBy))
			{
				queryString.put("orderBy", desc);
				getAttributes().put("formaction", String.format("Gate?%s", queryString.toString()));
				getJspContext().getOut().write(String.format("<button %s>&uarr;", getAttributes().toString()));
			} else if (desc.equals(orderBy))
			{
				getAttributes().put("formaction", String.format("Gate?%s", queryString.toString()));
				getJspContext().getOut().write(String.format("<button %s>&darr;", getAttributes().toString()));
			} else
			{
				queryString.put("orderBy", property);
				getAttributes().put("formaction", String.format("Gate?%s", queryString.toString()));
				getJspContext().getOut().write(String.format("<button %s>", getAttributes().toString()));
			}
			getJspBody().invoke(null);
			getJspContext().getOut().write("</button>");
		} else
		{
			if (property.equals(orderBy))
			{
				queryString.put("orderBy", desc);
				getAttributes().put("href", String.format("Gate?%s", queryString.toString()));
				getJspContext().getOut().write(String.format("<a %s>&uarr;", getAttributes().toString()));
			} else if (desc.equals(orderBy))
			{
				getAttributes().put("href", String.format("Gate?%s", queryString.toString()));
				getJspContext().getOut().write(String.format("<a %s>&darr;", getAttributes().toString()));
			} else
			{
				queryString.put("orderBy", property);
				getAttributes().put("href", String.format("Gate?%s", queryString.toString()));
				getJspContext().getOut().write(String.format("<a %s>", getAttributes().toString()));
			}
			getJspBody().invoke(null);
			getJspContext().getOut().write("</a>");
		}
	}

	public void setArguments(String arguments) throws ConversionException
	{
		this.arguments = Parameters.parse(arguments);
	}

	public void setProperty(String property)
	{
		this.property = property;
	}

	public void setMethod(String method)
	{
		this.method = method;
	}

}
