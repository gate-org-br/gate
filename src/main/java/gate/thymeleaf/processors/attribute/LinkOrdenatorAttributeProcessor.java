package gate.thymeleaf.processors.attribute;

import gate.util.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.web.IWebRequest;

@ApplicationScoped
public class LinkOrdenatorAttributeProcessor extends AttributeProcessor
{

	public LinkOrdenatorAttributeProcessor()
	{
		super("a", "ordenator");
	}

	@Override
	public void process(ITemplateContext context,
		IProcessableElementTag element,
		IElementTagStructureHandler handler)
	{
		String property = Optional.ofNullable(element.getAttributeValue("g:ordenator")).orElseThrow(()
			-> new TemplateProcessingException("Missing required property on g:ordenator"));

		IWebContext webContext = (IWebContext) context;
		IWebRequest request = webContext.getExchange().getRequest();
		String orderBy = request.getParameterValue("orderBy");
		Parameters queryString = Parameters.parse(request.getQueryString());
		Optional.ofNullable(element.getAttributeValue("arguments")).map(Parameters::parse).ifPresent(queryString::putAll);
		queryString.remove("orderBy");

		String desc = "-" + property;

		if (property.equals(orderBy))
		{
			queryString.put("orderBy", desc);
			handler.setAttribute("href", String.format("Gate?%s", queryString.toString()));
		} else if (desc.equals(orderBy))
		{
			handler.setAttribute("href", String.format("Gate?%s", queryString.toString()));
		} else
		{
			queryString.put("orderBy", property);
			handler.setAttribute("href", String.format("Gate?%s", queryString.toString()));
		}
	}
}
