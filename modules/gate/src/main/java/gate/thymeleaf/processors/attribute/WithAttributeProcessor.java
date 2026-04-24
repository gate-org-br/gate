package gate.thymeleaf.processors.attribute;

import gate.thymeleaf.ELExpressionFactory;
import gate.thymeleaf.Precedence;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class WithAttributeProcessor extends AttributeProcessor
{

	@Inject
	ELExpressionFactory expression;

	public WithAttributeProcessor()
	{
		super(null, "with");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		try
		{
			var name = extract(element, handler, "g:with").orElseThrow();
			var value = extract(element, handler, "g:value").orElse(null);
			var type = extract(element, handler, "g:type").orElse(null);

			if (value != null)
				handler.setLocalVariable(name, expression.create().evaluate(value));
			else if (type != null)
				handler.setLocalVariable(name, Thread.currentThread().getContextClassLoader().loadClass(type)
					.getDeclaredConstructor().newInstance());

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
