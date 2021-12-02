package gate.thymeleaf.processors.tag;

import gate.converter.Converter;
import gate.thymeleaf.ELExpression;
import gate.type.Attributes;
import gate.util.Parameters;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

@ApplicationScoped
public class THProcessor extends TagModelProcessor
{

	@Inject
	ELExpression expression;

	public THProcessor()
	{
		super("th");
	}

	@Override
	public void process(ITemplateContext context, IModel model, IElementModelStructureHandler handler)
	{
		IProcessableElementTag element = (IProcessableElementTag) model.get(0);
		Attributes attributes = Stream.of(element.getAllAttributes())
			.filter(e -> !"value".equals(e.getAttributeCompleteName()))
			.filter(e -> !"ordenate".equals(e.getAttributeCompleteName()))
			.filter(e -> !"format".equals(e.getAttributeCompleteName()))
			.filter(e -> !"empty".equals(e.getAttributeCompleteName()))
			.collect(Collectors.toMap(e -> e.getAttributeCompleteName(),
				e -> e.getValue(), (a, b) -> a, Attributes::new));

		if (element instanceof IStandaloneElementTag)
			standalone(context, model, handler, element, attributes);
		else
			nonStandalone(context, model, handler, element, attributes);
	}

	private void standalone(ITemplateContext context, IModel model, IElementModelStructureHandler handler, IProcessableElementTag element, Attributes attributes)
	{

		HttpServletRequest request = ((IWebContext) context).getRequest();

		var value = expression.evaluate(element.getAttributeValue("value"));
		String string = Converter.toText(value, element.getAttributeValue("format"));
		if (string.isBlank() && element.hasAttribute("empty"))
			string = Converter.toText(expression.evaluate(element.getAttributeValue("empty")));
		String body = string.replaceAll("\\n", "<br/>");

		model.reset();
		add(context, model, handler, "<th " + attributes + ">");

		if (element.hasAttribute("ordenate"))
		{
			var ordenate = element.getAttributeValue("ordenate");
			final String ordenateDesc = "-" + ordenate;

			Parameters parameters = new Parameters();
			parameters.put(request.getQueryString());
			String orderBy = request.getParameter("orderBy");
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

			if ("post".equalsIgnoreCase(element.getAttributeValue("method")))
				add(context, model, handler, "<button formaction='Gate?" + parameters + "'>" + arrow + body + "</button>");
			else
				add(context, model, handler, "<a href='Gate?" + parameters + "'>" + arrow + body + "</a>");
		} else
			add(context, model, handler, body);

		add(context, model, handler, "</th>");
	}

	private void nonStandalone(ITemplateContext context, IModel model, IElementModelStructureHandler handler, IProcessableElementTag element, Attributes attributes)
	{
		if (element.hasAttribute("ordenate"))
		{
			HttpServletRequest request = ((IWebContext) context).getRequest();

			var ordenate = element.getAttributeValue("ordenate");
			final String ordenateDesc = "-" + ordenate;

			Parameters parameters = new Parameters();
			parameters.put(request.getQueryString());
			String orderBy = request.getParameter("orderBy");
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

			if ("post".equalsIgnoreCase(element.getAttributeValue("method")))
				replaceTag(context, model, handler, "<th " + attributes + "><button formaction='Gate?" + parameters + "'>" + arrow, "</button></th>");
			else
				replaceTag(context, model, handler, "<th " + attributes + "><a href='Gate?" + parameters + "'>" + arrow, "</a></th>");
		} else
			replaceTag(context, model, handler, "th", attributes);
	}
}
