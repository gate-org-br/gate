package gate.thymeleaf.processors.attribute;

import gate.thymeleaf.Precedence;
import gate.thymeleaf.processors.Processor;
import gate.type.Attributes;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeNames;
import org.thymeleaf.engine.ElementNames;
import org.thymeleaf.model.IModel;
import org.thymeleaf.processor.element.IElementModelProcessor;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.processor.element.MatchingElementName;
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

	public void add(ITemplateContext context, IModel model,
		IElementModelStructureHandler handler, String text)
	{
		model.add(context.getModelFactory().createText(text));
	}

	public void removeTag(ITemplateContext context,
		IModel model, IElementModelStructureHandler handler)
	{
		model.remove(0);
		model.remove(model.size() - 1);
	}

	public void replaceTag(ITemplateContext context,
		IModel model, IElementModelStructureHandler handler, String tag, Attributes attributes)
	{
		model.replace(0, context.getModelFactory().createText("<" + tag + " " + attributes + ">"));
		model.replace(model.size() - 1, context.getModelFactory().createText("</" + tag + ">"));
	}

	public void replaceWith(ITemplateContext context,
		IModel model, IElementModelStructureHandler handler, String text)
	{
		model.reset();
		model.add(context.getModelFactory().createText(text));
	}
}
