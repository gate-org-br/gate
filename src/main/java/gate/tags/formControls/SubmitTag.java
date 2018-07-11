package gate.tags.formControls;

import gate.error.AppError;

import java.io.IOException;

import javax.servlet.jsp.JspException;

public class SubmitTag extends PropertyTag
{

	private String value;

	public void setValue(String value)
	{
		this.value = value;
	}

	public void doTag() throws JspException, IOException
	{
		try
		{
			super.doTag();
			getAttributes().put("value", value);
			getAttributes().put("type", "SUBMIT");
			getJspContext().getOut().print(String.format("<input%s/>%n", getAttributes().toString()));
		} catch (IOException | JspException e)
		{
			throw new AppError(e);
		}
	}
}
