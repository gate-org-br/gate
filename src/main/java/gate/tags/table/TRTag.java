package gate.tags.table;

import gate.annotation.Color;
import gate.annotation.Description;
import gate.annotation.Name;
import gate.annotation.Tooltip;
import gate.tags.*;
import java.io.IOException;
import javax.servlet.jsp.JspException;

public class TRTag extends AnchorTag
{

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

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

		if (getModule() != null
			|| getScreen() != null
			|| getAction() != null)
			if (getCondition() && checkAccess())
			{
				getAttributes().put("data-action", getURL());
				if ("POST".equalsIgnoreCase(getMethod()))
					getAttributes().put("data-method", "post");

				if (getTarget() != null)
					getAttributes().put("data-target", getTarget());
			}

		getJspContext().getOut().print(getAttributes().isEmpty()
			? "<tr>"
			: "<tr " + getAttributes().toString() + " >");
		getJspBody().invoke(null);
		getJspContext().getOut().print("</tr>");

	}

}
