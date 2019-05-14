package gate.tags;

import gate.annotation.Color;
import gate.annotation.Description;
import gate.annotation.Name;
import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class LinkTag extends AnchorTag
{

	private String otherwise;

	public void setOtherwise(String otherwise)
	{
		this.otherwise = otherwise;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		PageContext pageContext = (PageContext) getJspContext();

		if (getCondition() && checkAccess())
		{
			if (!getAttributes().containsKey("title") && getJavaMethod().isAnnotationPresent(Description.class))
				getAttributes().put("title", getJavaMethod().getAnnotation(Description.class).value());

			if (!getAttributes().containsKey("title") && getJavaMethod().isAnnotationPresent(Name.class))
				getAttributes().put("title", getJavaMethod().getAnnotation(Name.class).value());

			if (!getAttributes().containsKey("style") && getJavaMethod().isAnnotationPresent(Color.class))
				getAttributes().put("style", String.format("color: %s", getJavaMethod().getAnnotation(Color.class).value()));

			if (getTabindex() != null)
				getAttributes().put("tabindex", getTabindex());

			if ("POST".equalsIgnoreCase(getMethod()))
			{
				getAttributes().put("formaction", getURL());
				if (getTarget() != null)
					getAttributes().put("formtarget", getTarget());

				pageContext.getOut().print("<button " + getAttributes() + ">");

				if (getJspBody() != null)
					getJspBody().invoke(null);
				else
					pageContext.getOut().print(String.format("%s<i>&#X%s;</i>", getName(), getIcon().getCode()));

				pageContext.getOut().print("</button>");
			} else
			{
				getAttributes().put("href", getURL());
				if (getTarget() != null)
					getAttributes().put("target", getTarget());

				pageContext.getOut().print("<a " + getAttributes() + ">");

				if (getJspBody() != null)
					getJspBody().invoke(null);
				else
					pageContext.getOut().print(String.format("%s<i>&#X%s;</i>", getName(), getIcon().getCode()));

				pageContext.getOut().print("</a>");
			}

		} else if (otherwise != null)
			pageContext.getOut().print(otherwise);
	}
}
