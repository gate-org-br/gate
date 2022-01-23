package gate.thymeleaf.processors.attribute;

import gate.annotation.Emoji;
import gate.thymeleaf.ELExpression;
import gate.util.Emojis;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class EmojiAttributeProcessor extends AttributeProcessor
{

	@Inject
	ELExpression expression;

	public EmojiAttributeProcessor()
	{
		super(null, "emoji");
	}

	@Override
	public void process(ITemplateContext context,
		IProcessableElementTag element,
		IElementTagStructureHandler handler)
	{
		handler.setAttribute("data-emoji",
			extract(element, handler, "g:emoji")
				.map(expression::evaluate)
				.flatMap(Emoji.Extractor::extract)
				.orElse(Emojis.UNKNOWN).getCode());
	}
}
