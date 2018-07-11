package gate.tags;

import gate.converter.Converter;
import gate.report.Field;
import gate.report.Form;
import gate.report.FormElement;
import gate.report.Report;
import gate.report.ReportElement;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.servlet.jsp.JspException;

public class ReportTag extends FieldGeneratorTag
{

	private Report source;

	@Override
	public void doTag() throws JspException, IOException
	{
		for (ReportElement reportElement : source.getElements())
		{
			if (reportElement instanceof Form)
			{
				int i = 0;
				Form form = (Form) reportElement;
				getJspContext().getOut().print("<fieldset>");
				if (form.getCaption() != null)
					getJspContext().getOut().print(String.format("<legend>%s</legend>", form.getCaption()));

				for (FormElement formElement : form.getElements())
				{
					if (formElement instanceof Field)
					{
						Field field = (Field) formElement;
						getJspContext().getOut().print(String.format("<label style='width: %s%%'>",
								new BigDecimal(100).divide(new BigDecimal(form.getColumns()), 2, RoundingMode.HALF_EVEN)
										.multiply(new BigDecimal(field.getColspan())).toString()));
						getJspContext().getOut().print(String.format("%s:", field.getName()));
						getJspContext().getOut().print(String.format("<span>%s</span>", Converter.toText(field.getValue())));
						getJspContext().getOut().print("</label>");
					}
				}

				getJspContext().getOut().print("</fieldset>");
			}
		}
	}

	public void setSource(Report source)
	{
		this.source = source;
	}

}
