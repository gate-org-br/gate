package gate.thymeleaf.processors.attribute;

import gate.thymeleaf.Model;
import gate.thymeleaf.processors.Processor;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeNames;
import org.thymeleaf.engine.ElementNames;
import org.thymeleaf.model.IModel;
import org.thymeleaf.processor.element.IElementModelProcessor;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.processor.element.MatchingElementName;
import static org.thymeleaf.standard.processor.StandardInlineHTMLTagProcessor.PRECEDENCE;
import org.thymeleaf.templatemode.TemplateMode;

public abstract class AttributeModelProcessor implements IElementModelProcessor, Processor
{

	private final String name;
	private final MatchingElementName matchingElementName;
	private final MatchingAttributeName matchingAttributeName;

	public AttributeModelProcessor(String attribute)
	{
		this(null, attribute);
	}

	protected abstract void process(Model model);

	public AttributeModelProcessor(String element, String attribute)
	{
		this.name = attribute;
		this.matchingElementName = element != null
			? MatchingElementName.forElementName(TemplateMode.HTML,
				ElementNames.forName(TemplateMode.HTML, null, element)) : null;

		this.matchingAttributeName = MatchingAttributeName.forAttributeName(TemplateMode.HTML,
			AttributeNames.forName(TemplateMode.HTML, "g", attribute));
	}

	@Override
	public void process(ITemplateContext context, IModel model, IElementModelStructureHandler handler)
	{
		process(new Model(name, context, model, handler));
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
