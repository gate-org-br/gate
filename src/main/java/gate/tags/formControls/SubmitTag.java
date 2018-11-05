package gate.tags.formControls;

import java.io.IOException;

import javax.servlet.jsp.JspException;

public class SubmitTag extends PropertyTag
{

	private String value;

	public void setValue(String value)
	{
		this.value = value;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		getAttributes().put("value", value);
		getAttributes().put("type", "SUBMIT");
		getJspContext().getOut().print(String.format("<input%s/>%n", getAttributes().toString()));
	}
}
