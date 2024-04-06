package gate.thymeleaf.processors;

import gate.thymeleaf.ELExpressionFactory;
import gate.thymeleaf.Precedence;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.stream.Stream;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.processor.element.MatchingElementName;
import org.thymeleaf.templatemode.TemplateMode;

@ApplicationScoped
public class ExpressionProcessor implements IElementTagProcessor, Processor
{

	@Inject
	ELExpressionFactory expression;

	public ExpressionProcessor()
	{
	}

	@Override
	public void process(ITemplateContext context,
		IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		Stream.of(element.getAllAttributes()).forEach(e ->
		{
			var value = expression.create().evaluate((e.getValue()));
			handler.setAttribute(e.getAttributeCompleteName(),
				value != null ? value.toString() : "");
		});
	}

	@Override
	public MatchingElementName getMatchingElementName()
	{
		return null;
	}

	@Override
	public MatchingAttributeName getMatchingAttributeName()
	{
		return null;
	}

	@Override
	public TemplateMode getTemplateMode()
	{
		return TemplateMode.HTML;
	}

	@Override
	public int getPrecedence()
	{
		return Precedence.VERY_LOW;
	}
}
