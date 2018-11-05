package gate.tags.formControls;

import gate.tags.formControls.PropertyTag;
import gate.converter.Converter;
import gate.util.Toolkit;

import java.io.IOException;

import javax.servlet.jsp.JspException;

public class RadioTag extends PropertyTag
{

	private String value;
	private Boolean checked;

	public void setValue(String value)
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
		getAttributes().put("value", value);
		getAttributes().put("type", "radio");
		if (Boolean.TRUE.equals(checked)
				|| (checked == null
				&& Toolkit.collection(getValue()).contains(Converter
						.getConverter(getType())
						.ofString(getType(), value))))
			getAttributes().put("checked", "checked");
		getJspContext().getOut().print(String.format("<input %s/>", getAttributes().toString()));
	}
}
