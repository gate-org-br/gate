package gate.tags.formControls;

import gate.base.Screen;
import gate.converter.Converter;
import gate.lang.property.Property;
import gate.tags.DynamicAttributeTag;
import java.io.IOException;
import javax.servlet.jsp.JspException;

abstract class PropertyTag extends DynamicAttributeTag
{

	private String color;
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

	public String getColor()
	{
		return color;
	}

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();

		Screen screen = (Screen) getJspContext().findAttribute("screen");

		Property p = Property.getProperty(screen.getClass(), this.property);

		this.name = p.toString();
		this.color = p.getColor();
		this.type = p.getRawType();
		this.value = p.getValue(screen);
		this.converter = p.getConverter();
		this.elementType = p.getElementRawType();

		if (!getAttributes().containsKey("name"))
			getAttributes().put("name", p.toString());

		p.getConstraints().stream()
			.filter(e -> !getAttributes().containsKey(e.getName()))
			.forEachOrdered(e -> getAttributes().put(e.getName(), e.getValue().toString()));

		if (!getAttributes().containsKey("data-mask"))
		{
			String mask = p.getMask();
			if (mask != null && !mask.isEmpty())
				getAttributes().put("data-mask", mask);
		}

		if (!getAttributes().containsKey("title"))
		{
			String description = p.getDescription();
			if (description == null || description.isEmpty())
			{
				String propertyName = p.getDisplayName();
				if (propertyName != null && !propertyName.isEmpty())
					getAttributes().put("title", propertyName);
			} else
				getAttributes().put("title", description);
		}

		if (!getAttributes().containsKey("placeholder"))
		{
			String placeholder = p.getPlaceholder();
			if (placeholder != null && !placeholder.isEmpty())
				getAttributes().put("placeholder", placeholder);
		}
	}
}
