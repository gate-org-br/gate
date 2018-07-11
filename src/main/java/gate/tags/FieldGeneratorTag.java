package gate.tags;

import gate.type.Attributes;
import gate.type.Field;
import gate.util.Toolkit;

public abstract class FieldGeneratorTag extends DynamicAttributeTag
{

	public String getFieldControl(Field field,
			String property, Attributes attributes)
	{
		if (!Toolkit.isEmpty(field.getName()))
		{
			attributes = new Attributes(attributes);
			if (!attributes.containsKey("data-mask"))
				if (field.getMask() != null)
					attributes.put("data-mask", field.getMask());
			if (!attributes.containsKey("required"))
				if (Boolean.TRUE.equals(field.getRequired()))
					attributes.put("required", "required");
			if (!attributes.containsKey("readonly"))
				if (Boolean.TRUE.equals(field.getReadonly()))
					attributes.put("readonly", "readonly");
			if (!attributes.containsKey("maxlength"))
				if (field.getMaxlength() != null)
					attributes.put("maxlength", field.getMaxlength());
			if (!attributes.containsKey("pattern"))
				if (field.getPattern() != null)
					attributes.put("pattern", field.getPattern());
			if (!attributes.containsKey("title"))
				if (field.getDescription() != null)
					attributes.put("title", field.getDescription());

			if (field.getOptions().isEmpty())
			{
				if (!attributes.containsKey("name"))
					attributes.put("name", property);
				if (Boolean.FALSE.equals(field.getMultiple()))
				{
					attributes.put("type", "text");
					if (field.getValue() != null)
						attributes.put("value", field.getValue());
					return String.format("<label style='width: %s%%'>%s:<span><input %s/></span></label>", field.getSize().getPercentage().toString(), field
							.getName(), attributes.toString());
				} else
					return String.format("<label style='width: %s%%'>%s:<span style='height: 60px;'><textarea %s/>%s</textarea></span></label>", field.getSize()
							.getPercentage().toString(), field.getName(), attributes.toString(), field.getValue() != null ? field.getValue() : "");
			} else
			{
				StringBuilder options = new StringBuilder();
				if (Boolean.FALSE.equals(field.getMultiple()))
				{
					options.append("<option value=''></option>");
					for (String option : field.getOptions())
					{
						Attributes optionAttributes = new Attributes();
						optionAttributes.put("value", option);
						if (field.getValue().contains(option))
							optionAttributes.put("selected", "selected");
						options.append(String.format("<option %s>%s</option>", optionAttributes.toString(), option));
					}

					attributes.put("name", property);
					return String
							.format("<label style='width: %s%%'>%s:<span><select %s>%s</select></span></label>", field.getSize().getPercentage().toString(),
									field.getName(), attributes.toString(), options.toString());
				} else
				{
					attributes.put("type", "checkbox");
					attributes.put("name", String.format("%s[]", property));
					for (String option : field.getOptions())
					{
						attributes.put("value", option);
						if (field.getValue().contains(option))
							attributes.put("checked", "checked");
						else
							attributes.remove("checked");
						options.append(String.format("<label><input %s/>%s</label>", attributes.toString(), option));
					}
					return String.format("<fieldset style='width: %s%%'><legend>%s:</legend>%s</fieldset>",
							field.getSize().getPercentage().toString(), field.getName(), options.toString());
				}
			}
		} else if (Boolean.FALSE.equals(field.getMultiple()))
		{
			return String.format("<label style='width: %s%%'>&nbsp; <span style='background-color: transparent;'>&nbsp;</span></label>", field.getSize()
					.getPercentage().toString());
		} else
		{
			return String.format("<label style='width: %s%%'>&nbsp; <span style='height: 60px; background-color: transparent;'>&nbsp;</span></label>", field
					.getSize().getPercentage().toString());
		}
	}

	public String getFieldControl(Field field)
	{
		if (!Toolkit.isEmpty(field.getName()))
			if (Boolean.FALSE.equals(field.getMultiple()))
				return String.format("<label style='width: %s%%'>%s: <span>%s</span></label>", field.getSize().getPercentage().toString(), field.getName(),
						field.getValue() != null ? field.getValue() : "");
			else
				return String.format("<label style='width: %s%%'>%s: <span style='height: 60px;'>%s</span></label>", field.getSize().getPercentage().toString(),
						field.getName(),
						field.getValue() != null ? field.getValue() : "");
		else if (Boolean.FALSE.equals(field.getMultiple()))
			return String.format("<label style='width: %s%%'>&nbsp; <span style='background-color: transparent;'>&nbsp;</span></label>", field.getSize()
					.getPercentage().toString());
		else
			return String.format("<label style='width: %s%%'>&nbsp; <span style='height: 60px; background-color: transparent;'>&nbsp;</span></label>", field
					.getSize().getPercentage().toString());
	}
}
