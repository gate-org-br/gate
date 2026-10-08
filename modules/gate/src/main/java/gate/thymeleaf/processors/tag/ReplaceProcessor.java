package gate.thymeleaf.processors.tag;

import gate.thymeleaf.ELExpressionFactory;
import gate.thymeleaf.HTMLFileEngine;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

import java.nio.file.Path;

@ApplicationScoped
public class ReplaceProcessor extends TagModelProcessor
{
	@Inject
	HTMLFileEngine fileEngine;

	@Inject
	ELExpressionFactory expression;

	public ReplaceProcessor() {super("replace");}

	@Override
	public void process(ITemplateContext context, IModel model,
	                    IElementModelStructureHandler handler)
	{
		IProcessableElementTag element = (IProcessableElementTag) model.get(0);
		if (!element.hasAttribute("filename"))
			throw new TemplateProcessingException("Missing required attribute filename on g:replace");
		var filename = (String) expression.create()
				.evaluate(element.getAttributeValue("filename"));
		if (filename == null)
			throw new TemplateProcessingException("Missing required attribute filename on g:replace");

		var parent = Path.of(context.getTemplateData().getTemplate()).getParent();
		if (parent != null && !filename.startsWith("/"))
			filename = parent.resolve(filename)
					.normalize()
					.toString()
					.replace('\\', '/');

		var content = fileEngine.process(filename, context);
		replaceWith(context, model, handler, content);
	}
}