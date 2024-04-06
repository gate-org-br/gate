package gate.thymeleaf.processors.tag;

import gate.thymeleaf.ELExpressionFactory;
import gate.thymeleaf.Precedence;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class UseProcessor extends TagProcessor
{

	@Inject
	ELExpressionFactory expression;

	public UseProcessor()
	{
		super("use");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		try
		{
			IWebContext webContext = (IWebContext) context;
			var exchange = webContext.getExchange();

			var name = extract(element, handler, "name")
				.orElseThrow(() -> new TemplateProcessingException("Missing required attribute name on g:use"));

			if (element.hasAttribute("value"))
				exchange.setAttributeValue(name, expression.create().evaluate(element.getAttributeValue("value")));
			else if (element.hasAttribute("type"))
				exchange.setAttributeValue(name, Thread.currentThread().getContextClassLoader().loadClass(element.getAttributeValue("type"))
					.getDeclaredConstructor()
					.newInstance());
			handler.removeTags();
		} catch (ReflectiveOperationException ex)
		{
			throw new TemplateProcessingException(ex.getMessage());
		}
	}

	@Override
	public int getPrecedence()
	{
		return Precedence.MAX;
	}
}
