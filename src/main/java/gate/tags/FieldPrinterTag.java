package gate.tags;

import gate.type.Field;
import gate.util.Toolkit;

public abstract class FieldPrinterTag extends DynamicAttributeTag
{

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
