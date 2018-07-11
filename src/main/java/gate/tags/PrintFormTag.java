package gate.tags;

import gate.type.Field;
import gate.type.Form;
import java.io.IOException;

import javax.servlet.jsp.JspException;

public class PrintFormTag extends FieldPrinterTag
{

	private Form value;

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		if (value != null && !value.getFields().isEmpty())
			for (Field field : value.getFields())
				getJspContext().getOut().print(getFieldControl(field));
	}

	public void setValue(Form value)
	{
		this.value = value;
	}
}
