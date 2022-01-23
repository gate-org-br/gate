package gate.thymeleaf.processors.attribute;

import gate.thymeleaf.ELExpression;
import gate.thymeleaf.Precedence;
import gate.thymeleaf.TextEngine;
import java.util.List;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class EnumerateAttributeProcessor extends AttributeProcessor
{

	@Inject
	TextEngine engine;

	@Inject
	ELExpression expression;

	public EnumerateAttributeProcessor()
	{
		super(null, "enumerate");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{

		try
		{
			var source = element.getAttributeValue("g:enumerate");
			if (source == null || source.isBlank())
				throw new TemplateProcessingException("Missing required enum class name on g:enumerate attribute");
			var type = Thread.currentThread().getContextClassLoader().loadClass(source);

			var status = Optional.ofNullable(element.getAttributeValue("g:status")).orElse("status");
			var target = Optional.ofNullable(element.getAttributeValue("g:target")).orElse("target");

			handler.iterateElement(target, status, List.of(type.getEnumConstants()));

		} catch (ClassNotFoundException ex)
		{
			throw new TemplateProcessingException("Error trying to get enum values: " + ex.getMessage());
		}

	}

	@Override
	public int getPrecedence()
	{
		return Precedence.MAX;
	}
}
