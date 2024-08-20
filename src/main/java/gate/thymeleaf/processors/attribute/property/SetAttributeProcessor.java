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
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.processor.element.IElementProcessor;
import org.thymeleaf.processor.element.IElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.processor.element.MatchingElementName;
import org.thymeleaf.templatemode.TemplateMode;

@ApplicationScoped
public class SetAttributeProcessor extends AbstractProcessorDialect
{

	@Inject
	ELExpressionFactory expression;

	public SetAttributeProcessor()
	{
		super("Set", "set", Precedence.DEFAULT);
	}

	@Override
	public Set<IProcessor> getProcessors(final String dialectPrefix)
	{
		final Set<IProcessor> processors = new HashSet<>();
		processors.add(new SetAttributeProcessorHandler());
		return processors;
	}

	@Vetoed
	private class SetAttributeProcessorHandler implements IElementProcessor, IElementTagProcessor, IProcessor
	{

		@Override
		public MatchingElementName getMatchingElementName()
		{
			return MatchingElementName.forAllElements(TemplateMode.HTML);
		}

		@Override
		public MatchingAttributeName getMatchingAttributeName()
		{
			return MatchingAttributeName.forAllAttributesWithPrefix(TemplateMode.HTML, "set");
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
				.filter(attribute -> attribute.getAttributeCompleteName().startsWith("set:"))
				.forEach(attribute ->
				{
					handler.removeAttribute(attribute.getAttributeCompleteName());
					String name = attribute.getAttributeCompleteName().substring(4);
					Boolean condition = (Boolean) expression.create().evaluate(attribute.getValue());

					if (Boolean.TRUE.equals(condition)
						&& Stream.of(tag.getAllAttributes())
							.map(IAttribute::getAttributeCompleteName).noneMatch(name::equals))
						handler.setAttribute(name, null);
					else
						handler.removeAttribute(name);
				});
		}
	}
}
