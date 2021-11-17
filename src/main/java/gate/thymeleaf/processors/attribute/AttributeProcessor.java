package gate.thymeleaf.processors.attribute;

import gate.thymeleaf.processors.Processor;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeNames;
import org.thymeleaf.engine.ElementNames;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.processor.element.MatchingElementName;
import static org.thymeleaf.standard.processor.StandardInlineHTMLTagProcessor.PRECEDENCE;
import org.thymeleaf.templatemode.TemplateMode;

public class AttributeProcessor implements IElementTagProcessor, Processor
{

	private final MatchingElementName matchingElementName;
	private final MatchingAttributeName matchingAttributeName;

	public AttributeProcessor(String attribute)
	{
		this(null, attribute);
	}

	public AttributeProcessor(String element, String attribute)
	{
		this.matchingElementName = element != null
			? MatchingElementName.forElementName(TemplateMode.HTML,
				ElementNames.forName(TemplateMode.HTML, null, element)) : null;

		this.matchingAttributeName = MatchingAttributeName.forAttributeName(TemplateMode.HTML,
			AttributeNames.forName(TemplateMode.HTML, "g", attribute));
	}

	@Override
	public void process(ITemplateContext context,
		IProcessableElementTag element, IElementTagStructureHandler handler)
	{

	}

	@Override
	public MatchingElementName getMatchingElementName()
	{
		return matchingElementName;
	}

	@Override
	public MatchingAttributeName getMatchingAttributeName()
	{
		return matchingAttributeName;
	}

	@Override
	public TemplateMode getTemplateMode()
	{
		return TemplateMode.HTML;
	}

	@Override
	public int getPrecedence()
	{
		return PRECEDENCE;
	}

}
