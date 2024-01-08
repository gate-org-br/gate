package gate.thymeleaf.processors.attribute;

import gate.annotation.Emoji;
import gate.icon.Emojis;
import gate.thymeleaf.ELExpressionFactory;
import gate.thymeleaf.Precedence;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class EmojiTagAttributeProcessor extends AttributeProcessor
{

	@Inject
	ELExpressionFactory expression;

	public EmojiTagAttributeProcessor()
	{
		super("e", "emoji");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		var type = element.getAttributeValue("g:emoji");
		handler.removeAttribute("g:emoji");
		var value = expression.create().evaluate(type);
		var emoji = Emoji.Extractor.extract(value).orElse(Emojis.UNKNOWN);
		handler.setBody(emoji.toString(), false);
	}

	@Override
	public int getPrecedence()
	{
		return Precedence.HIGH;
	}
}
