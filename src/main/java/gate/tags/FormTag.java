package gate.tags;

import gate.base.Screen;
import gate.converter.Converter;
import gate.lang.property.Property;
import gate.type.Field;
import gate.type.Form;
import java.io.IOException;
import javax.servlet.jsp.JspException;

public class FormTag extends FieldGeneratorTag
{

	private String property;

	@Override
	public void doTag() throws JspException, IOException
	{
		Screen screen = (Screen) getJspContext().findAttribute("screen");
		Property p = Property.getProperty(screen.getClass(), this.property);
		if (!p.getRawType().equals(Form.class))
			throw new IllegalArgumentException(String.format("Property '%s' is not a %s.",
				this.property, Form.class.getName()));
		Form form = (Form) p.getValue(screen);
		if (form == null)
			throw new IllegalArgumentException(String.format("Property '%s' can not be null.", this.property));

		String value = Converter.toString(form);
		if (value != null)
			value = value.replaceAll("'", "");
		getJspContext().getOut().print(String.format("<input type='hidden' name='%s' value='%s'/>", p.toString(), value));
		int index = 0;
		for (Field field : form.getFields())
			getJspContext().getOut().print(getFieldControl(field, String.format("%s.fields[%d].value", p.toString(), index++), getAttributes()));
	}

	public String getProperty()
	{
		return property;
	}

	public void setProperty(String property)
	{
		this.property = property;
	}
}
