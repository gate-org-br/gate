package gate.tags.formControls;

import gate.tags.formControls.SelectorTag;
import gate.error.AppError;
import gate.error.ConversionException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;

public class SelectNTag extends SelectorTag
{

	@Override
	public void doTag() throws JspException, IOException
	{
		try
		{
			super.doTag();
			getAttributes().put("type", "checkbox");
			for (Map.Entry<String, List<Option>> entry : getGroups().entrySet())
				for (Option option : entry.getValue())
				{
					getAttributes().remove("checked");
					if (option.getSelected())
						getAttributes().put("checked", "checked");
					getAttributes().put("value", option.getValue());

					if (getJspBody() != null)
					{
						getJspContext().getOut().print(String.format("<label><input %s/> ", getAttributes().toString()));
						getJspContext().setAttribute("option", option.getValue());
						getJspBody().invoke(getJspContext().getOut());
						getJspContext().removeAttribute("option");
						getJspContext().getOut().print("</label>");
					} else
						getJspContext().getOut().print(String.format("<label><input %s/> %s</label>", getAttributes().toString(), option.getLabel()));

				}
		} catch (ConversionException e)
		{
			throw new AppError(e);
		}
	}
}
