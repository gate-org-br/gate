package gate.thymeleaf.processors.tag;

import gate.type.Attributes;
import gate.util.Parameters;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

@ApplicationScoped
public class OrdenatorProcessor extends TagModelProcessor
{

	public OrdenatorProcessor()
	{
		super("ordenator");
	}

	@Override
	public void process(ITemplateContext context, IModel model, IElementModelStructureHandler handler)
	{
		IWebContext webContext = (IWebContext) context;
		HttpServletRequest request = webContext.getRequest();
		IProcessableElementTag element = (IProcessableElementTag) model.get(0);

		String method = Optional.ofNullable(element.getAttributeValue("method"))
			.map(String::toUpperCase).orElse(request.getMethod().toUpperCase());
		String property = Optional.ofNullable(element.getAttributeValue("property")).orElseThrow(()
			-> new TemplateProcessingException("Missing required attribute property on g:" + getName()));

		String orderBy = request.getParameter("orderBy");
		Parameters queryString = Parameters.parse(request.getQueryString());
		Optional.ofNullable(element.getAttributeValue("arguments")).map(Parameters::parse).ifPresent(queryString::putAll);
		queryString.remove("orderBy");

		String desc = "-" + property;

		Attributes attributes = new Attributes();
		if ("POST".equalsIgnoreCase(method))
		{
			if (property.equals(orderBy))
			{
				queryString.put("orderBy", desc);
				attributes.put("formaction", String.format("Gate?%s", queryString.toString()));
				replaceTag(context, model, handler, "<button " + attributes + ">&uarr;", "</button>");
			} else if (desc.equals(orderBy))
			{
				attributes.put("formaction", String.format("Gate?%s", queryString.toString()));
				replaceTag(context, model, handler, "<button " + attributes + ">&darr;", "</button>");
			} else
			{
				queryString.put("orderBy", property);
				attributes.put("formaction", String.format("Gate?%s", queryString.toString()));
				replaceTag(context, model, handler, "<button " + attributes + ">", "</button>");
			}
		} else
		{
			if (property.equals(orderBy))
			{
				queryString.put("orderBy", desc);
				attributes.put("href", String.format("Gate?%s", queryString.toString()));
				replaceTag(context, model, handler, "<a " + attributes + ">&uarr;", "</a>");
			} else if (desc.equals(orderBy))
			{
				attributes.put("href", String.format("Gate?%s", queryString.toString()));
				replaceTag(context, model, handler, "<a " + attributes + ">&darr;", "</a>");
			} else
			{
				queryString.put("orderBy", property);
				attributes.put("href", String.format("Gate?%s", queryString.toString()));
				replaceTag(context, model, handler, "<a " + attributes + ">", "</a>");
			}
		}
	}
}
