package gate.thymeleaf.processors.attribute;

import gate.annotation.Emoji;
import gate.thymeleaf.ELExpression;
import gate.thymeleaf.Precedence;
import gate.util.Emojis;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class EmojiTagAttributeProcessor extends AttributeProcessor
{

	@Inject
	ELExpression expression;

	public EmojiTagAttributeProcessor()
	{
		super("e", "emoji");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		var type = element.getAttributeValue("g:emoji");
		handler.removeAttribute("g:emoji");
		var value = expression.evaluate(type);
		var emoji = Emoji.Extractor.extract(value).orElse(Emojis.UNKNOWN);
		handler.setBody(emoji.toString(), false);
	}

	@Override
	public int getPrecedence()
	{
		return Precedence.HIGH;
	}
}
