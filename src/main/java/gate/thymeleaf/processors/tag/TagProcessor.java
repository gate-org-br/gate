package gate.thymeleaf.processors.tag;

import gate.thymeleaf.Precedence;
import gate.thymeleaf.processors.Processor;
import java.util.Optional;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.ElementNames;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.processor.element.MatchingElementName;
import org.thymeleaf.templatemode.TemplateMode;

public class TagProcessor implements IElementTagProcessor, Processor
{

	private final String element;
	private final MatchingElementName matchingElementName;

	public TagProcessor(String element)
	{
		this.element = element;
		this.matchingElementName = element != null
			? MatchingElementName.forElementName(TemplateMode.HTML,
				ElementNames.forName(TemplateMode.HTML, "g", element)) : null;
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
		return Precedence.DEFAULT;
	}

	public String getElement()
	{
		return element;
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
