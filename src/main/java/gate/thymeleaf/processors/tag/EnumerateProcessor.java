package gate.thymeleaf.processors.tag;

import gate.thymeleaf.TextEngine;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

@ApplicationScoped
public class EnumerateProcessor extends TagModelProcessor
{

	@Inject
	TextEngine engine;

	public EnumerateProcessor()
	{
		super("enumerate");
	}

	@Override
	public void process(ITemplateContext context, IModel model, IElementModelStructureHandler handler)
	{
		try
		{
			IProcessableElementTag element = (IProcessableElementTag) model.get(0);

			String source = element.getAttributeValue("source");
			if (source == null || source.isBlank())
				throw new TemplateProcessingException("Missing required enum class name on source attribute");

			var type = Thread.currentThread().getContextClassLoader().loadClass(source);
			var target = Optional.ofNullable(element.getAttributeValue("target")).orElse("target");

			removeTag(context, model, handler);
			IModel content = model.cloneModel();
			model.reset();

			var exchange = ((IWebContext) context).getExchange();

			for (var e : List.of(type.getEnumConstants()))
			{
				exchange.setAttributeValue(target, e);
				add(context, model, handler, engine.process(content, context));
				exchange.removeAttribute(target);
			}
		} catch (ClassNotFoundException ex)
		{
			throw new TemplateProcessingException("Error trying to get enum values: " + ex.getMessage());
		}
	}
}
