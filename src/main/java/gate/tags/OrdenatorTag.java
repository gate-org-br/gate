package gate.tags;

import gate.util.QueryString;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class OrdenatorTag extends DynamicAttributeTag
{

	private String method;
	private String property;
	private QueryString arguments;

	@Override
	public void doTag() throws JspException, IOException
	{
		HttpServletRequest request = (HttpServletRequest) ((PageContext) getJspContext()).getRequest();
		String orderBy = request.getParameter("orderBy");
		QueryString qs = new QueryString(request.getQueryString());
		if (arguments != null)
			qs.putAll(arguments);
		qs.remove("orderBy");

		if ("POST".equalsIgnoreCase(method))
		{
			if (property.equals(orderBy))
			{
				qs.put("orderBy", "-".concat(property));
				getAttributes().put("formaction", String.format("Gate?%s", qs.toString()));
				getJspContext().getOut().write(String.format("<button %s>&uarr;", getAttributes().toString()));
			} else if ("-".concat(property).equals(orderBy))
			{
				getAttributes().put("formaction", String.format("Gate?%s", qs.toString()));
				getJspContext().getOut().write(String.format("<button %s>&darr;", getAttributes().toString()));
			} else
			{
				qs.put("orderBy", property);
				getAttributes().put("formaction", String.format("Gate?%s", qs.toString()));
				getJspContext().getOut().write(String.format("<button %s>", getAttributes().toString()));
			}
			getJspBody().invoke(null);
			getJspContext().getOut().write("</button>");
		} else
		{
			if (property.equals(orderBy))
			{
				qs.put("orderBy", "-".concat(property));
				getAttributes().put("href", String.format("Gate?%s", qs.toString()));
				getJspContext().getOut().write(String.format("<a %s>&uarr;", getAttributes().toString()));
			} else if ("-".concat(property).equals(orderBy))
			{
				getAttributes().put("href", String.format("Gate?%s", qs.toString()));
				getJspContext().getOut().write(String.format("<a %s>&darr;", getAttributes().toString()));
			} else
			{
				qs.put("orderBy", property);
				getAttributes().put("href", String.format("Gate?%s", qs.toString()));
				getJspContext().getOut().write(String.format("<a %s>", getAttributes().toString()));
			}
			getJspBody().invoke(null);
			getJspContext().getOut().write("</a>");
		}
	}

	public void setArguments(String arguments)
	{
		this.arguments = new QueryString(arguments);
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
