package gate.tags.property;

import gate.converter.Converter;
import gate.util.Toolkit;
import java.io.IOException;
import javax.servlet.jsp.JspException;

public class CheckboxTag extends PropertyTag
{

	private Object value;
	private Boolean checked;

	public void setValue(Object value)
	{
		this.value = value;
	}

	public void setChecked(Boolean checked)
	{
		this.checked = checked;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		getAttributes().put("type", "checkbox");
		getAttributes().put("value", Converter.toString(value));

		if (Boolean.TRUE.equals(checked) || (checked == null && Toolkit.collection(getValue()).contains(value)))
			getAttributes().put("checked", "checked");
		getJspContext().getOut().print(String.format("<input %s/>", getAttributes().toString()));
	}
}
