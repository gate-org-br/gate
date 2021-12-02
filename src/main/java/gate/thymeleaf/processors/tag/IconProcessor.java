package gate.thymeleaf.processors.tag;

import gate.annotation.Icon;
import gate.thymeleaf.ELExpression;
import gate.util.Icons;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class IconProcessor extends TagProcessor
{

	@Inject
	ELExpression expression;

	public IconProcessor()
	{
		super("icon");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		var icon = extract(element, handler, "type")
			.map(expression::evaluate)
			.map(e -> Icon.Extractor.extract(e).orElse(Icons.UNKNOWN))
			.orElseThrow(() -> new TemplateProcessingException("Missing required attribute type on g:icon"));
		handler.replaceWith("<i>" + icon + "</i>", false);
	}

}
