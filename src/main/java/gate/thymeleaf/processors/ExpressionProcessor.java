package gate.thymeleaf.processors;

import gate.converter.Converter;
import gate.thymeleaf.Expression;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.processor.element.MatchingElementName;
import static org.thymeleaf.standard.processor.StandardInlineHTMLTagProcessor.PRECEDENCE;
import org.thymeleaf.templatemode.TemplateMode;

@ApplicationScoped
public class ExpressionProcessor implements IElementTagProcessor, Processor
{

	public ExpressionProcessor()
	{
	}

	@Override
	public void process(ITemplateContext context,
		IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		Expression expression = Expression.of(context);
		Stream.of(element.getAllAttributes())
			.forEach(e -> handler.setAttribute(e.getAttributeCompleteName(),
			Converter.toString(expression.evaluate(e.getValue()))));
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
		return PRECEDENCE + 1000;
	}
}
