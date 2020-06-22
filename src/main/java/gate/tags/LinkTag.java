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

	private String type;
	private String otherwise;

	public void setOtherwise(String otherwise)
	{
		this.otherwise = otherwise;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
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

			switch (type != null ? type.trim().toLowerCase() : "full")
			{
				case "text":
					pageContext.getOut().print("Sair");
					break;
				case "icon":
					pageContext.getOut().print("<i>&#X2007;</i>");
					break;
				case "full":
					pageContext.getOut().print("Sair<i>&#X2007;</i>");
					break;
			}
			pageContext.getOut().print("</a>");
		} else if (getCondition() && checkAccess())
		{
			if (!getAttributes().containsKey("title"))
				Description.Extractor.extract(getJavaMethod()).ifPresent(e -> getAttributes().put("title", e));

			if (!getAttributes().containsKey("title"))
				Name.Extractor.extract(getJavaMethod()).ifPresent(e -> getAttributes().put("title", e));

			if (!getAttributes().containsKey("data-tooltip"))
				Tooltip.Extractor.extract(getJavaMethod()).ifPresent(e -> getAttributes().put("data-tooltip", e));

			if (!getAttributes().containsKey("data-tooltip"))
				Tooltip.Extractor.extract(getJavaClass()).ifPresent(e -> getAttributes().put("data-tooltip", e));

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
					switch (type != null ? type.trim().toLowerCase() : "full")
					{
						case "text":
							pageContext.getOut().print(getName());
							break;
						case "icon":
							pageContext.getOut().print("<i>&#X" + getIcon().getCode() + ";</i>");
							break;
						case "full":
							pageContext.getOut().print(getName() + "<i>&#X" + getIcon().getCode() + ";</i>");
							break;
					}

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
					switch (type != null ? type.trim().toLowerCase() : "full")
					{
						case "text":
							pageContext.getOut().print(getName());
							break;
						case "icon":
							pageContext.getOut().print("<i>&#X" + getIcon().getCode() + ";</i>");
							break;
						case "full":
							pageContext.getOut().print(getName() + "<i>&#X" + getIcon().getCode() + ";</i>");
							break;
					}

				pageContext.getOut().print("</a>");
			}

		} else if (otherwise != null)
			pageContext.getOut().print(otherwise);
	}
}
