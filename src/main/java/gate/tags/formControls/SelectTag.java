package gate.tags.formControls;

import gate.tags.formControls.SelectorTag;
import gate.error.AppError;
import gate.type.Attributes;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;

public class SelectTag extends SelectorTag
{

	private String empty;

	public void setEmpty(String empty)
	{
		this.empty = empty;
	}

	public void doTag() throws JspException, IOException
	{
		try
		{
			super.doTag();

			getJspContext().getOut().println(String.format("<select %s>", getAttributes().toString()));

			if (!getName().endsWith("[]"))
				getJspContext().getOut().println(String.format("<option value=''>%s</option>", this.empty == null ? "" : empty));

			for (Map.Entry<String, List<Option>> entry : getGroups().entrySet())
			{
				if (entry.getKey() != null)
					getJspContext().getOut().print(String.format("<optgroup label='%s'>", entry.getKey()));

				for (Option option : entry.getValue())
				{
					Attributes attributes = new Attributes();
					if (option.getSelected())
						attributes.put("selected", "selected");
					attributes.put("value", option.getValue());
					if (getJspBody() != null)
					{
						getJspContext().getOut().println(String.format("<option %s>", attributes.toString()));
						getJspContext().setAttribute("option", option.getValue());
						getJspBody().invoke(getJspContext().getOut());
						getJspContext().removeAttribute("option");
						getJspContext().getOut().println("</option>");
					} else
						getJspContext().getOut().println(String.format("<option %s>%s</option>", attributes.toString(), option.getLabel()));
				}

				if (entry.getKey() != null)
					getJspContext().getOut().print(String.format("</optgroup>", entry.getKey()));
			}
			getJspContext().getOut().println(String.format("</select>"));
		} catch (Exception e)
		{
			throw new AppError(e);
		}
	}
}
