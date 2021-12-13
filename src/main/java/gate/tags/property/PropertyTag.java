package gate.tags.property;

import gate.base.Screen;
import gate.converter.Converter;
import gate.lang.property.Property;
import gate.tags.AttributeTag;
import java.io.IOException;
import javax.servlet.jsp.JspException;

abstract class PropertyTag extends AttributeTag
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

	public Class<?> getElementType()
	{
		return elementType;
	}

	public Class<?> getType()
	{
		return type;
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

		Property property = Property.getProperty(screen.getClass(), this.property);

		this.name = property.toString();
		this.color = property.getColor();
		this.type = property.getRawType();
		this.value = property.getValue(screen);
		this.converter = property.getConverter();
		this.elementType = property.getElementRawType();

		if (!getAttributes().containsKey("name"))
			getAttributes().put("name", this.name);

		property.getConstraints().stream()
			.filter(e -> !getAttributes().containsKey(e.getName()))
			.forEachOrdered(e -> getAttributes().put(e.getName(), e.getValue().toString()));

		if (!getAttributes().containsKey("data-mask"))
		{
			String mask = property.getMask();
			if (mask != null && !mask.isEmpty())
				getAttributes().put("data-mask", mask);
		}

		if (!getAttributes().containsKey("title"))
		{
			String description = property.getDescription();
			if (description == null || description.isEmpty())
			{
				String propertyName = property.getDisplayName();
				if (propertyName != null && !propertyName.isEmpty())
					getAttributes().put("title", propertyName);
			} else
				getAttributes().put("title", description);
		}

		if (!getAttributes().containsKey("data-tooltip"))
		{
			String tooltip = property.getTooltip();
			if (tooltip != null && !tooltip.isEmpty())
				getAttributes().put("data-tooltip", tooltip);
		}

		if (!getAttributes().containsKey("placeholder"))
		{
			String placeholder = property.getPlaceholder();
			if (placeholder != null && !placeholder.isEmpty())
				getAttributes().put("placeholder", placeholder);
		}
	}
}
