package gate.thymeleaf.processors.tag;

import gate.converter.Converter;
import gate.thymeleaf.Expression;
import gate.thymeleaf.Model;
import gate.type.Attributes;
import gate.util.Parameters;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class THProcessor extends ModelProcessor
{

	public THProcessor()
	{
		super("th");
	}

	@Override
	protected void doProcess(Model model)
	{
		Attributes attributes = new Attributes();
		model.stream()
			.filter(e -> e.getValue() != null)
			.filter(e -> !"value".equals(e.getAttributeCompleteName()))
			.filter(e -> !"ordenate".equals(e.getAttributeCompleteName()))
			.forEach(e -> attributes.put(e.getAttributeCompleteName(), e.getValue()));

		if (model.isStandalone())
			standalone(model, attributes);
		else
			nonStandalone(model, attributes);
	}

	private void standalone(Model model, Attributes attributes)
	{
		Expression expression = Expression.of(model.getContext());

		var value = expression.evaluate(model.get("value"));
		String string = Converter.toText(value, model.get("format"));
		if (string.isBlank() && model.has("empty"))
			string = Converter.toText(expression.evaluate(model.get("empty")));
		String body = string.replaceAll("\\n", "<br/>");

		model.removeAll();
		model.add("<th " + attributes + ">");

		if (model.has("ordenate"))
		{
			var ordenate = model.get("ordenate");
			final String ordenateDesc = "-" + ordenate;

			Parameters parameters = new Parameters();
			parameters.put(model.request().getQueryString());
			String orderBy = model.request().getParameter("orderBy");
			parameters.remove("orderBy");

			String arrow = "";
			if (ordenate.equals(orderBy))
				arrow = "&uarr;";
			else if (ordenateDesc.equals(orderBy))
				arrow = "&darr;";

			if (ordenate.equals(orderBy))
				parameters.put("orderBy", ordenateDesc);
			else if (!ordenateDesc.equals(orderBy))
				parameters.put("orderBy", ordenate);

			if ("post".equalsIgnoreCase(model.get("method")))
				model.add("<button formaction='Gate?" + parameters + "'>" + arrow + body + "</button>");
			else
				model.add("<a href='Gate?" + parameters + "'>" + arrow + body + "</a>");
		} else
			model.add(body);

		model.add("</th>");
	}

	private void nonStandalone(Model model, Attributes attributes)
	{
		if (model.has("ordenate"))
		{
			var ordenate = model.get("ordenate");
			final String ordenateDesc = "-" + ordenate;

			Parameters parameters = new Parameters();
			parameters.put(model.request().getQueryString());
			String orderBy = model.request().getParameter("orderBy");
			parameters.remove("orderBy");

			String arrow = "";
			if (ordenate.equals(orderBy))
				arrow = "&uarr;";
			else if (ordenateDesc.equals(orderBy))
				arrow = "&darr;";

			if (ordenate.equals(orderBy))
				parameters.put("orderBy", ordenateDesc);
			else if (!ordenateDesc.equals(orderBy))
				parameters.put("orderBy", ordenate);

			if ("post".equalsIgnoreCase(model.get("method")))
				model.replaceTag("<th " + attributes + "><button formaction='Gate?" + parameters + "'>" + arrow, "</button></th>");
			else
				model.replaceTag("<th " + attributes + "><a href='Gate?" + parameters + "'>" + arrow, "</a></th>");
		} else
			model.replaceTag("th", attributes);
	}
}
