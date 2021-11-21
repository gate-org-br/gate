package gate.thymeleaf.processors.tag;

import gate.thymeleaf.Model;
import gate.thymeleaf.TextEngine;
import gate.util.Icons;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.model.IModel;

@ApplicationScoped
public class IconsProcessor extends ModelProcessor
{

	@Inject
	TextEngine engine;

	public IconsProcessor()
	{
		super("icons");
	}

	@Override
	protected void doProcess(Model model)
	{
		String name = "icon";
		if (model.has("name"))
			name = model.get("name");

		model.removeTag();
		IModel content = model.cloneModel();
		model.removeAll();

		for (Icons.Icon icon : Icons.getInstance().get())
		{
			model.request().setAttribute(name, icon);
			model.add(engine.process(content, model.getContext()));
			model.request().removeAttribute(name);
		}
	}
}
