package gate.thymeleaf.processors.attribute.property;

import gate.thymeleaf.ELExpressionFactory;
import gate.thymeleaf.Precedence;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Vetoed;
import jakarta.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.processor.element.IElementProcessor;
import org.thymeleaf.processor.element.IElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.processor.element.MatchingElementName;
import org.thymeleaf.templatemode.TemplateMode;

@ApplicationScoped
public class NotAttributeProcessor extends AbstractProcessorDialect
{

	@Inject
	ELExpressionFactory expression;

	public NotAttributeProcessor()
	{
		super("Not", "not", Precedence.DEFAULT);
	}

	@Override
	public Set<IProcessor> getProcessors(final String dialectPrefix)
	{
		final Set<IProcessor> processors = new HashSet<>();
		processors.add(new NotAttributeProcessorHandler());
		return processors;
	}

	@Vetoed
	private class NotAttributeProcessorHandler implements IElementProcessor, IElementTagProcessor, IProcessor
	{

		@Override
		public MatchingElementName getMatchingElementName()
		{
			return MatchingElementName.forAllElements(TemplateMode.HTML);
		}

		@Override
		public MatchingAttributeName getMatchingAttributeName()
		{
			return MatchingAttributeName.forAllAttributesWithPrefix(TemplateMode.HTML, "not");
		}

		@Override
		public TemplateMode getTemplateMode()
		{
			return TemplateMode.HTML;
		}

		@Override
		public int getPrecedence()
		{
			return Precedence.VERY_LOW;
		}

		@Override
		public void process(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler handler)
		{
			Stream.of(tag.getAllAttributes())
				.filter(attribute -> attribute.getAttributeCompleteName().startsWith("not:"))
				.forEach(attribute ->
				{
					handler.removeAttribute(attribute.getAttributeCompleteName());
					String name = attribute.getAttributeCompleteName().substring(4);
					handler.removeAttribute(name);
				});
		}
	}
}
