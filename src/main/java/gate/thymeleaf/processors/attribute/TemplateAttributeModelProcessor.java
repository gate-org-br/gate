package gate.thymeleaf.processors.attribute;

import gate.thymeleaf.FileEngine;
import gate.thymeleaf.Model;
import gate.thymeleaf.TextEngine;
import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import org.thymeleaf.exceptions.TemplateProcessingException;

@ApplicationScoped
public class TemplateAttributeModelProcessor extends AttributeModelProcessor
{

	@Inject
	TextEngine textEngine;

	@Inject
	FileEngine fileEngine;

	public TemplateAttributeModelProcessor()
	{
		super("template");

	}

	@Override
	protected void process(Model model)
	{

		try
		{
			var template = model.get("g:template");

			model.removeTag();

			model.request().setAttribute("g-template-content", textEngine.process(model.getModel(), model.getContext()));

			if (!template.endsWith(".jsp"))
				model.replaceAll(fileEngine.process(template, model.getContext()));
			else
				model.request().getRequestDispatcher(template).forward(model.request(), model.response());

			model.request().removeAttribute("g-template-content");
		} catch (IOException | ServletException ex)
		{
			throw new TemplateProcessingException(ex.getMessage(), ex);
		}
	}

}
