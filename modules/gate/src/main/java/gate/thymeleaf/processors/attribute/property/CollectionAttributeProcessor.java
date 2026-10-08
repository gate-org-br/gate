package gate.thymeleaf.processors.attribute.property;

import gate.adapter.converter.Converter;
import gate.lang.property.Property;
import jakarta.enterprise.context.ApplicationScoped;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@ApplicationScoped
public class CollectionAttributeProcessor extends FormControlAttributeProcessor
{

	public CollectionAttributeProcessor()
	{
		super("g-collection");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element,
	                    IElementTagStructureHandler handler, Object screen, Property property, Object value)
	{
		handler.setAttribute("value", serialize(value));
	}

	private String serialize(Object value)
	{
		if (!(value instanceof Collection<?> collection))
			return "";

		return collection.stream()
				.filter(Objects::nonNull)
				.map(Converter::toString)
				.collect(Collectors.joining(";"));
	}
}
