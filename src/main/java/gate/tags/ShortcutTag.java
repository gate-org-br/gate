package gate.tags;

import gate.annotation.Color;
import gate.util.Toolkit;
import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class ShortcutTag extends AnchorTag
{

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		if (!getCondition())
			return;

		PageContext pageContext = (PageContext) getJspContext();

		if (getTabindex() != null)
			getAttributes().put("tabindex", getTabindex());

		if (Toolkit.isEmpty(getModule())
			&& Toolkit.isEmpty(getScreen())
			&& Toolkit.isEmpty(getAction()))
		{
			getAttributes().put("href", "Gate");
			if (!getAttributes().containsKey("title"))
				getAttributes().put("title", "Sair do sistema");

			pageContext.getOut().print("<a " + getAttributes() + ">");

			if (getJspBody() != null)
				getJspBody().invoke(null);
			else
				pageContext.getOut().print("<i>&#X2007;</i>");

			pageContext.getOut().print("</a>");
		} else if (checkAccess())
		{
			if (!getAttributes().containsKey("title"))
				getAttributes().put("title", getName());

			if (!getAttributes().containsKey("style") && getJavaMethod().isAnnotationPresent(Color.class))
				getAttributes().put("style", String.format("color: %s", getJavaMethod().getAnnotation(Color.class).value()));

			if ("POST".equalsIgnoreCase(this.getMethod()))
			{
				getAttributes().put("formaction", getURL());

				if (getTarget() != null)
					getAttributes().put("formtarget", getTarget());

				pageContext.getOut().print("<button " + getAttributes() + ">");

				if (getJspBody() != null)
					getJspBody().invoke(null);
				else
					pageContext.getOut().print(String.format("<i>&#X%s;</i>", getIcon().getCode()));

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
					pageContext.getOut().print(String.format("<i>&#X%s;</i>", getIcon().getCode()));

				pageContext.getOut().print("</a>");
			}
		}
	}
}
