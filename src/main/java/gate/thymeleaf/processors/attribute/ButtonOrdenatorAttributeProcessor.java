package gate.thymeleaf.processors.attribute;

import gate.util.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.web.IWebRequest;

import java.util.Optional;

@ApplicationScoped
public class ButtonOrdenatorAttributeProcessor extends AttributeProcessor
{

	public ButtonOrdenatorAttributeProcessor()
	{
		super("button", "ordenator");
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
		Optional.ofNullable(element.getAttributeValue("arguments"))
				.map(Parameters::parse).ifPresent(queryString::put);
		queryString.remove("orderBy");

		String desc = "-" + property;

		if (property.equals(orderBy))
		{
			queryString.put("orderBy", desc);
			handler.setAttribute("formaction", String.format("Gate?%s", queryString));
		} else if (desc.equals(orderBy))
		{
			handler.setAttribute("formaction", String.format("Gate?%s", queryString));
		} else
		{
			queryString.put("orderBy", property);
			handler.setAttribute("formaction", String.format("Gate?%s", queryString));
		}
	}
}