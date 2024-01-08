package gate.thymeleaf.processors.tag;

import gate.annotation.Name;
import gate.thymeleaf.ELExpressionFactory;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class NameProcessor extends TagProcessor
{

	@Inject
	ELExpressionFactory expression;

	public NameProcessor()
	{
		super("name");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		var name = extract(element, handler, "type")
			.map(expression.create()::evaluate)
			.map(e -> Name.Extractor.extract(e).orElse("unamed"))
			.orElseThrow(() -> new TemplateProcessingException("Missing required attribute type on g:name"));
		handler.replaceWith(name, false);
	}

}
