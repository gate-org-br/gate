package gate.thymeleaf.processors.attribute;

import gate.thymeleaf.ELExpressionFactory;
import gate.thymeleaf.HTMLFileEngine;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

import java.nio.file.Path;

@ApplicationScoped
public class IncludeAttributeProcessor extends AttributeProcessor
{
	@Inject
	HTMLFileEngine fileEngine;

	@Inject
	ELExpressionFactory expression;

	public IncludeAttributeProcessor()
	{
		super(null, "include");
	}

	@Override
	public void process(ITemplateContext context,
	                    IProcessableElementTag element,
	                    IElementTagStructureHandler handler)
	{
		var filename = (String) expression.create()
				.evaluate(extract(element, handler, "g:include")
						.orElseThrow(() -> new TemplateProcessingException("Missing required attribute g:include")));
		if (filename == null)
			throw new TemplateProcessingException("Missing required attribute g:include");

		var parent = Path.of(context.getTemplateData().getTemplate()).getParent();
		if (parent != null && !filename.startsWith("/"))
			filename = parent.resolve(filename)
					.normalize()
					.toString()
					.replace('\\', '/');

		var content = fileEngine.process(filename, context);
		handler.setBody(content, false);
	}
}