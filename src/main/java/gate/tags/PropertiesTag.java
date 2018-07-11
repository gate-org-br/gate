package gate.tags;

import gate.constraint.Maxlength;
import gate.constraint.Required;
import gate.lang.property.Property;

import java.io.IOException;
import java.util.List;
import javax.servlet.jsp.JspException;

public class PropertiesTag extends DynamicAttributeTag
{

	private String caption;
	private List<Property> source;

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		getJspContext().getOut().print(String.format("<table %s>", getAttributes().toString()));
		if (caption != null)
			getJspContext().getOut().print(String.format("<caption>%s</caption>", caption));

		getJspContext().getOut().print("<colgroup>");
		getJspContext().getOut().print("<col style='width: 40px'/>");
		getJspContext().getOut().print("<col style='width: 160px'/>");
		getJspContext().getOut().print("<col style='width: 80px'/>");
		getJspContext().getOut().print("<col style='width: 80px'/>");
		getJspContext().getOut().print("<col/>");
		getJspContext().getOut().print("</colgroup>");

		getJspContext().getOut().print("<thead>");
		getJspContext().getOut().print("<tr>");
		getJspContext().getOut().print("<th style='text-align: center'>#</th>");
		getJspContext().getOut().print("<th>Nome</th>");
		getJspContext().getOut().print("<th style='text-align: center'>Requerido</th>");
		getJspContext().getOut().print("<th style='text-align: center'>Tam Max</th>");
		getJspContext().getOut().print("<th>Descrição</th>");
		getJspContext().getOut().print("</tr>");
		getJspContext().getOut().print("</thead>");

		getJspContext().getOut().print("<tbody>");
		int i = 0;
		for (Property property : source)
		{
			getJspContext().getOut().print("<tr>");
			getJspContext().getOut().print(String.format("<td style='text-align: center'>%02d</td>", ++i));
			getJspContext().getOut().print(String.format("<td>%s</td>", property.getName().orElse(property.toString())));
			getJspContext().getOut().print(String.format("<td style='text-align: center'>%s</td>", property.getConstraints().stream().anyMatch(
				e -> e instanceof Required.Implementation) ? "Sim" : "Não"));
			getJspContext().getOut().print(String.format("<td style='text-align: center'>%s</td>", property.getConstraints().stream().filter(e -> e instanceof Maxlength.Implementation).map(e -> e.getValue().toString()).findFirst().orElse("N/A")));
			getJspContext().getOut().print(String.format("<td>%s</td>", property.getDescription()));
			getJspContext().getOut().print("</tr>");
		}
		getJspContext().getOut().print("</tbody>");

		getJspContext().getOut().print("</table>");
	}

	public void setSource(List<Property> source)
	{
		this.source = source;
	}

	public void setCaption(String caption)
	{
		this.caption = caption;
	}
}
