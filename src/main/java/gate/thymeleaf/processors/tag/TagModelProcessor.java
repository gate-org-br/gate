package gate.thymeleaf.processors.tag;

import gate.thymeleaf.Precedence;
import gate.thymeleaf.processors.Processor;
import gate.type.Attributes;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.ElementNames;
import org.thymeleaf.model.IModel;
import org.thymeleaf.processor.element.IElementModelProcessor;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.processor.element.IElementProcessor;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.processor.element.MatchingElementName;
import org.thymeleaf.templatemode.TemplateMode;

public abstract class TagModelProcessor implements IElementModelProcessor, IElementProcessor, Processor
{

	private final String name;
	private final MatchingElementName matchingElementName;

	public TagModelProcessor(String name)
	{

		this.name = name;
		this.matchingElementName = MatchingElementName.forElementName(TemplateMode.HTML,
			ElementNames.forName(TemplateMode.HTML, "g", name));

	}

	@Override
	public TemplateMode getTemplateMode()
	{
		return TemplateMode.HTML;
	}

	@Override
	public MatchingAttributeName getMatchingAttributeName()
	{
		return null;
	}

	@Override
	public MatchingElementName getMatchingElementName()
	{
		return matchingElementName;
	}

	@Override
	public int getPrecedence()
	{
		return Precedence.DEFAULT;
	}

	public String getName()
	{
		return name;
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

	public void replaceWith(ITemplateContext context,
		IModel model, IElementModelStructureHandler handler, String text)
	{
		model.reset();
		model.add(context.getModelFactory().createText(text));
	}

	public void replaceTag(ITemplateContext context,
		IModel model, IElementModelStructureHandler handler, String tag, Attributes attributes)
	{
		model.replace(0, context.getModelFactory().createText("<" + tag + " " + attributes + ">"));
		model.replace(model.size() - 1, context.getModelFactory().createText("</" + tag + ">"));
	}

	public void replaceTag(ITemplateContext context,
		IModel model, IElementModelStructureHandler handler, String open, String close)
	{
		model.replace(0, context.getModelFactory().createText(open));
		model.replace(model.size() - 1, context.getModelFactory().createText(close));
	}
}
