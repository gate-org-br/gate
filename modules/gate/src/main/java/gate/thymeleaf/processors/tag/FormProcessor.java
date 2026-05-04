package gate.thymeleaf.processors.tag;

import gate.adapter.converter.Converter;
import gate.lang.property.Property;
import gate.thymeleaf.processors.tag.property.PropertyProcessor;
import gate.type.Attributes;
import gate.type.Field;
import gate.type.Form;
import gate.util.Toolkit;
import jakarta.enterprise.context.ApplicationScoped;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

import java.util.StringJoiner;

@ApplicationScoped
public class FormProcessor extends PropertyProcessor
{

	public FormProcessor()
	{
		super("form");
	}

	@Override
	protected void process(ITemplateContext context, IProcessableElementTag element,
	                       IElementTagStructureHandler handler,
	                       Object screen, Property property, Attributes attributes)
	{

		if ("g-form".equals(element.getElementCompleteName()))
			return;

		if (!property.getRawType().equals(Form.class))
			throw new IllegalArgumentException(String.format("Property '%s' is not a %s.",
					property, Form.class.getName()));

		Form form = (Form) property.getValue(screen);
		if (form == null)
			throw new IllegalArgumentException(String.format("Property '%s' can not be null.", property));

		String value = Converter.toString(form);
		if (value != null)
			value = value.replace("'", "");

		StringJoiner string = new StringJoiner(System.lineSeparator());
		string.add(String.format("<input type='hidden' name='%s' value='%s'/>", property, value));
		int index = 0;
		for (Field field : form.getFields())
			string.add(getFieldControl(field, String.format("%s.fields[%d].value", property, index++), attributes));

		handler.replaceWith(string.toString(), false);

	}

	public String getFieldControl(Field field,
	                              String property, Attributes attributes)
	{

		Attributes size = new Attributes();
		if (field.schema().size() != null)
			size.put("data-size", Integer.parseInt(field.schema().size().toString()) * 2);

		if (Toolkit.notEmpty(field.schema().name()))
		{
			attributes = new Attributes(attributes);
			if (!attributes.containsKey("data-mask"))
				if (field.schema().mask() != null)
					attributes.put("data-mask", field.schema().mask());
			if (!attributes.containsKey("required"))
				if (field.schema().required())
					attributes.put("required", "required");
			if (!attributes.containsKey("readonly"))
				if (field.schema().readonly())
					attributes.put("readonly", "readonly");
			if (!attributes.containsKey("maxlength"))
				if (field.schema().maxlength() != null)
					attributes.put("maxlength", field.schema().maxlength());
			if (!attributes.containsKey("pattern"))
				if (field.schema().pattern() != null)
					attributes.put("pattern", field.schema().pattern());
			if (!attributes.containsKey("title"))
				if (field.schema().description() != null)
					attributes.put("title", field.schema().description());

			if (field.schema().options().isEmpty())
			{
				if (!attributes.containsKey("name"))
					attributes.put("name", property);
				if (!field.schema().multiple())
				{
					attributes.put("type", "text");
					if (field.getValue() != null)
						attributes.put("value", field.getValue());
					return String.format("<label %s>%s:<span><input %s/></span></label>", size, field.schema().name(), attributes);
				} else
					return String.format("<label %s>%s:<span style='height: 60px;'><textarea %s/>%s</textarea></span></label>",
							size, field.schema().name(), attributes, field.getValue() != null ? field.getValue() : "");
			} else
			{
				StringBuilder options = new StringBuilder();
				if (!field.schema().multiple())
				{
					options.append("<option value=''></option>");
					for (String option : field.schema().options())
					{
						Attributes optionAttributes = new Attributes();
						optionAttributes.put("value", option);
						if (field.getValue().contains(option))
							optionAttributes.put("selected", "selected");
						options.append(String.format("<option %s>%s</option>", optionAttributes, option));
					}

					attributes.put("name", property);
					return String
							.format("<label %s>%s:<span><select %s>%s</select></span></label>", size,
									field.schema().name(), attributes, options);
				} else
				{
					attributes.put("type", "checkbox");
					attributes.put("name", String.format("%s[]", property));
					for (String option : field.schema().options())
					{
						attributes.put("value", option);
						if (field.getValue().contains(option))
							attributes.put("checked", "checked");
						else
							attributes.remove("checked");
						options.append("<input ").append(attributes).append("/><label>").append(option).append("</label>");
					}
					return String.format("<fieldset %s><legend>%s:</legend><g-selectn>%s</g-selectn></fieldset>",
							size, field.schema().name(), options);
				}
			}
		} else if (!field.schema().multiple())
			return String.format("<label %s>&nbsp; <span style='background-color: transparent;'><label>&nbsp;</label></span></label>", size);
		else
			return String.format("<label %s>&nbsp; <span style='height: 60px; background-color: transparent;'><label>&nbsp;</label></span></label>", size);
	}

}