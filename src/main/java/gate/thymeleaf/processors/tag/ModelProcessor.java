package gate.thymeleaf.processors.tag;

import gate.thymeleaf.Model;
import gate.thymeleaf.Precedence;
import gate.thymeleaf.processors.Processor;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.ElementNames;
import org.thymeleaf.model.IModel;
import org.thymeleaf.processor.element.IElementModelProcessor;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.processor.element.IElementProcessor;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.processor.element.MatchingElementName;
import org.thymeleaf.templatemode.TemplateMode;

public abstract class ModelProcessor implements IElementModelProcessor, IElementProcessor, Processor
{

	private final String name;
	private final MatchingElementName matchingElementName;

	public ModelProcessor(String name)
	{

		this.name = name;
		this.matchingElementName = MatchingElementName.forElementName(TemplateMode.HTML,
			ElementNames.forName(TemplateMode.HTML, "g", name));

	}

	protected abstract void doProcess(Model model);

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

	@Override
	public void process(ITemplateContext context, IModel model, IElementModelStructureHandler handler)
	{
		doProcess(new Model(name, context, model, handler));
	}
}
