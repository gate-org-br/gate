package gate.tags;

import gate.annotation.Color;
import gate.annotation.Description;
import gate.annotation.Name;
import gate.annotation.Tooltip;
import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class TableRowTag extends AnchorTag
{

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		PageContext pageContext = (PageContext) getJspContext();

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

		if (getCondition() && checkAccess())
		{
			getAttributes().put("data-action", getURL());
			if ("POST".equalsIgnoreCase(getMethod()))
				getAttributes().put("data-method", "post");

			if (getTarget() != null)
				getAttributes().put("data-target", getTarget());
		}

		pageContext.getOut().print("<tr " + getAttributes() + ">");
		getJspBody().invoke(null);
		pageContext.getOut().print("</tr>");

	}
}
