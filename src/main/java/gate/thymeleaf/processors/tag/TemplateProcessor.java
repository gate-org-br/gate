package gate.thymeleaf.processors.tag;

import gate.thymeleaf.FileEngine;
import gate.thymeleaf.Model;
import gate.thymeleaf.TextEngine;
import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import org.thymeleaf.exceptions.TemplateProcessingException;

@ApplicationScoped
public class TemplateProcessor extends ModelProcessor
{

	@Inject
	TextEngine textEngine;

	@Inject
	FileEngine fileEngine;

	public TemplateProcessor()
	{
		super("template");

	}

	@Override
	protected void doProcess(Model model)
	{

		try
		{
			if (!model.has("filename"))
				throw new TemplateProcessingException("Missing required attribute filename on g:template");

			model.removeTag();

			model.request().setAttribute("g-template-content", textEngine.process(model.getModel(), model.getContext()));

			if (!model.get("filename").endsWith(".jsp"))
				model.replaceAll(fileEngine.process(model.get("filename"), model.getContext()));
			else
				model.request().getRequestDispatcher(model.get("filename"))
					.forward(model.request(), model.response());

			model.request().removeAttribute("g-template-content");
		} catch (IOException | ServletException ex)
		{
			throw new TemplateProcessingException(ex.getMessage(), ex);
		}
	}

}
