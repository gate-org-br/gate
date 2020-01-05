package gate.tags;

import gate.annotation.Color;
import gate.annotation.Description;
import gate.annotation.Name;
import gate.annotation.Tooltip;
import gate.type.Attributes;
import gate.util.Toolkit;
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

		if (Toolkit.isEmpty(getModule())
			&& Toolkit.isEmpty(getScreen())
			&& Toolkit.isEmpty(getAction()))
		{

			Attributes atrributes = new Attributes();
			atrributes.put("href", "Gate");
			pageContext.getOut().print("<a " + atrributes + ">");
			pageContext.getOut().print("Sair<i>&#X2007;</i>");
			pageContext.getOut().print("</a>");
		} else if (getCondition() && checkAccess())
		{
			if (!getAttributes().containsKey("title"))
				Description.Extractor.extract(getJavaMethod()).ifPresent(e -> getAttributes().put("title", e));

			if (!getAttributes().containsKey("title"))
				Name.Extractor.extract(getJavaMethod()).ifPresent(e -> getAttributes().put("title", e));

			if (!getAttributes().containsKey("tooltip"))
				Tooltip.Extractor.extract(getJavaMethod()).ifPresent(e -> getAttributes().put("data-tooltip", e));

			if (!getAttributes().containsKey("style"))
				Color.Extractor.extract(getJavaMethod()).ifPresent(e -> getAttributes().put("style", "color: " + e));

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
