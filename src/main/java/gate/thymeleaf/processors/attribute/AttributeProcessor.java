package gate.thymeleaf.processors.attribute;

import gate.thymeleaf.Precedence;
import gate.thymeleaf.processors.Processor;
import java.util.Optional;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeNames;
import org.thymeleaf.engine.ElementNames;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.processor.element.MatchingElementName;
import org.thymeleaf.templatemode.TemplateMode;

public abstract class AttributeProcessor implements IElementTagProcessor, Processor
{

	private final String element;
	private final String attribute;
	private final MatchingElementName matchingElementName;
	private final MatchingAttributeName matchingAttributeName;

	public AttributeProcessor(String element, String attribute)
	{
		this.element = element;
		this.attribute = attribute;
		this.matchingElementName = element != null
			? MatchingElementName.forElementName(TemplateMode.HTML,
				ElementNames.forName(TemplateMode.HTML, null, element)) : null;

		this.matchingAttributeName = attribute != null ? MatchingAttributeName.forAttributeName(TemplateMode.HTML,
			AttributeNames.forName(TemplateMode.HTML, "g", attribute)) : null;
	}

	@Override
	public abstract void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler);

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
		return Precedence.DEFAULT;
	}

	public String getElement()
	{
		return element;
	}

	public String getAttribute()
	{
		return attribute;
	}

	public Optional<String> extract(IProcessableElementTag element, IElementTagStructureHandler handler, String attribute)
	{
		if (!element.hasAttribute(attribute))
			return Optional.empty();
		String result = element.getAttributeValue(attribute);
		handler.removeAttribute(attribute);
		return Optional.ofNullable(result);
	}
}
