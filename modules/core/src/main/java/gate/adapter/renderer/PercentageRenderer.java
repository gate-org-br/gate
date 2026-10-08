package gate.adapter.renderer;

import gate.i18n.CurrentLocale;
import gate.type.Percentage;

import java.text.DecimalFormat;

public class PercentageRenderer implements Renderer
{
	@Override
	public String render(Class<?> type, Object object)
	{
		if (object != null)
		{
			DecimalFormat format = CurrentLocale.getDecimalFormat("0.00");
			format.setCurrency(CurrentLocale.getCurrency());
			format.setParseBigDecimal(true);
			return format.format(((Percentage) object).getValue());
		}
		return "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(CurrentLocale.get(), format, render(type, object)) : "";
	}
}
