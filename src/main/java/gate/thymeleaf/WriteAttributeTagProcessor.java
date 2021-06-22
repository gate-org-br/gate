package gate.thymeleaf;

import gate.converter.Converter;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import static org.thymeleaf.standard.processor.StandardInlineHTMLTagProcessor.PRECEDENCE;
import org.thymeleaf.templatemode.TemplateMode;

public class WriteAttributeTagProcessor extends AbstractAttributeTagProcessor
{

	public WriteAttributeTagProcessor()
	{
		super(TemplateMode.HTML,
			"g",
			null,
			false,
			"write",
			true,
			PRECEDENCE,
			true);
	}

	@Override
	protected void doProcess(ITemplateContext context,
		IProcessableElementTag element,
		AttributeName name,
		String value,
		IElementTagStructureHandler tag)
	{
		IEngineConfiguration configuration = context.getConfiguration();
		IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);
		IStandardExpression expression = parser.parseExpression(context, value);
		value = Converter.toString(expression.execute(context));
		tag.setBody(value, true);
	}

}
