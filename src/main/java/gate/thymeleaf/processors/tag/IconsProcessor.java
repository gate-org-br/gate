package gate.thymeleaf.processors.tag;

import gate.thymeleaf.TextEngine;
import gate.util.Icons;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

@ApplicationScoped
public class IconsProcessor extends TagModelProcessor
{

	@Inject
	TextEngine engine;

	public IconsProcessor()
	{
		super("icons");
	}

	@Override
	public void process(ITemplateContext context, IModel model, IElementModelStructureHandler handler)
	{
		IProcessableElementTag element = (IProcessableElementTag) model.get(0);

		String name = "icon";
		if (element.hasAttribute("name"))
			name = element.getAttributeValue("name");

		removeTag(context, model, handler);
		IModel content = model.cloneModel();
		model.reset();

		var request = ((IWebContext) context).getRequest();

		for (Icons.Icon icon : Icons.getInstance().get())
		{
			request.setAttribute(name, icon);
			add(context, model, handler, engine.process(content, context));
			request.removeAttribute(name);
		}
	}
}
