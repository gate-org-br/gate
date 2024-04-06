package gate.thymeleaf.processors;

import gate.thymeleaf.ELExpressionFactory;
import gate.thymeleaf.Precedence;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IText;
import org.thymeleaf.processor.text.ITextProcessor;
import org.thymeleaf.processor.text.ITextStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

@ApplicationScoped
public class TextProcessor implements ITextProcessor, Processor
{

	@Inject
	ELExpressionFactory expression;

	public TextProcessor()
	{
	}

	@Override
	public void process(ITemplateContext context, IText text, ITextStructureHandler handler)
	{
		var value = expression.create().evaluate(text.getText());
		handler.setText(value != null ? value.toString() : "");
	}

	@Override
	public TemplateMode getTemplateMode()
	{
		return TemplateMode.HTML;
	}

	@Override
	public int getPrecedence()
	{
		return Precedence.MIN;
	}
}
