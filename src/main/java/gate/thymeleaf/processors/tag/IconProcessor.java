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
public class IconProcessor extends TagAttributeProcessor
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
		var type = extract(element, handler, "type")
			.orElseThrow(() -> new TemplateProcessingException("Missing required attribute type on g:icon"));
		var value = expression.evaluate(type);
		var icon = Icon.Extractor.extract(value).orElse(Icons.UNKNOWN);
		handler.replaceWith("<i>" + icon + "</i>", false);
	}

}
