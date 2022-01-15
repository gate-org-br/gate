package gate.thymeleaf.processors.tag;

import gate.annotation.Description;
import gate.thymeleaf.ELExpression;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class DescriptionProcessor extends TagProcessor
{

	@Inject
	ELExpression expression;

	public DescriptionProcessor()
	{
		super("description");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		var description = extract(element, handler, "type")
			.map(expression::evaluate)
			.map(e -> Description.Extractor.extract(e).orElse("undescribed"))
			.orElseThrow(() -> new TemplateProcessingException("Missing required attribute type on g:description"));
		handler.replaceWith(description, false);
	}

}
