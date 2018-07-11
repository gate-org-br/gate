package gate.tags.formControls;

import gate.base.Screen;
import gate.lang.property.Property;
import gate.converter.Converter;
import gate.tags.DynamicAttributeTag;
import gate.util.Toolkit;

import java.io.IOException;

import javax.servlet.jsp.JspException;

abstract class PropertyTag extends DynamicAttributeTag
{

	private String name;
	private Object value;
	private Class<?> type;
	private String property;
	private Converter converter;
	private Class<?> elementType;

	public void setProperty(String property)
	{
		this.property = property;
	}

	public String getProperty()
	{
		return property;
	}

	public Object getValue()
	{
		return value;
	}

	public Class<?> getType()
	{
		return type;
	}

	public Class<?> getElementType()
	{
		return elementType;
	}

	public String getName()
	{
		return name;
	}

	public Converter getConverter()
	{
		return converter;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		Screen screen = (Screen) getJspContext().findAttribute("screen");
		Property p = Property.getProperty(screen.getClass(), this.property);

		this.name = p.toString();
		this.type = p.getRawType();
		this.value = p.getValue(screen);
		this.elementType = p.getElementRawType();

		if (!getAttributes().containsKey("name"))
			getAttributes().put("name", p.toString());

		if (p.getMask() != null && !getAttributes().containsKey("data-mask"))
			getAttributes().put("data-mask", p.getMask());
		p.getConstraints()
				.stream()
				.filter(e -> !getAttributes().containsKey(e.getName()))
				.forEachOrdered(e -> getAttributes().put(e.getName(), e.getValue().toString()));

		converter = p.getConverter();
		if (converter.getMask() != null && !getAttributes().containsKey("data-mask"))
			getAttributes().put("data-mask", converter.getMask());

		converter.getConstraints()
				.stream()
				.filter(e -> !getAttributes().containsKey(e.getName()))
				.forEachOrdered(e -> getAttributes().put(e.getName(), e.getValue().toString()));

		if (!getAttributes().containsKey("title"))
		{
			if (!Toolkit.isEmpty(p.getDescription())
					&& !Toolkit.isEmpty(converter.getDescription()))
				getAttributes().put("title", String.format(p.getDescription(), String.format(converter.getDescription())));
			else if (!Toolkit.isEmpty(p.getDescription()))
				getAttributes().put("title", String.format(p.getDescription()));
			else if (!Toolkit.isEmpty(converter.getDescription()))
				getAttributes().put("title", String.format(converter.getDescription()));
		}
	}
}
