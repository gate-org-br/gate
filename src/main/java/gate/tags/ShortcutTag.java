package gate.tags;

import gate.annotation.Color;
import gate.annotation.Tooltip;
import gate.util.Icons;
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
				getName().ifPresent(e -> getAttributes().put("title", e));

			if (!getAttributes().containsKey("tooltip"))
				Tooltip.Extractor.extract(getJavaMethod()).ifPresent(e -> getAttributes().put("data-tooltip", e));

			if (!getAttributes().containsKey("style"))
				Color.Extractor.extract(getJavaMethod()).ifPresent(e -> getAttributes().put("style", "color: " + e));

			if ("POST".equalsIgnoreCase(this.getMethod()))
			{
				getAttributes().put("formaction", getURL());

				if (getTarget() != null)
					getAttributes().put("formtarget", getTarget());

				pageContext.getOut().print("<button " + getAttributes() + ">");

				if (getJspBody() != null)
					getJspBody().invoke(null);
				else
					pageContext.getOut().print(String.format("<i>&#X%s;</i>", getIcon().map(Icons.Icon::getCode).orElse("?")));

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
					pageContext.getOut().print(String.format("<i>&#X%s;</i>", getIcon().map(Icons.Icon::getCode).orElse("?")));

				pageContext.getOut().print("</a>");
			}
		}
	}
}
