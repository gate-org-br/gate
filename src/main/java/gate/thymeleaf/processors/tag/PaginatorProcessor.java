package gate.thymeleaf.processors.tag;

import gate.base.Screen;
import gate.lang.property.Property;
import gate.thymeleaf.ELExpressionFactory;
import gate.type.Attributes;
import gate.util.Page;
import gate.util.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class PaginatorProcessor extends TagProcessor
{

	@Inject
	ELExpressionFactory expression;

	public PaginatorProcessor()
	{
		super("paginator");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		var exchange = ((IWebContext) context).getExchange();
		var request = exchange.getRequest();
		Parameters queryString = Parameters.parse(request.getQueryString());
		queryString.remove("pageSize");
		queryString.remove("pageIndx");

		String url = String.format("Gate?%s", queryString.toString());
		Screen screen = (Screen) exchange.getAttributeValue("screen");

		Page<?> page = element.hasAttribute("page")
			? (Page<?>) expression.create().evaluate(element.getAttributeValue("page"))
			: (Page<?>) Property.getValue(screen, "page");

		Attributes attributes = Stream.of(element.getAllAttributes())
			.filter(e -> !e.getAttributeCompleteName().equals("page"))
			.filter(e -> !e.getAttributeCompleteName().equals("target"))
			.collect(Collectors.toMap(e -> e.getAttributeCompleteName(),
				e -> e.getValue(), (a, b) -> a, Attributes::new));

		var paginator = page.getPaginator();

		final String tag = screen.isPOST() ? "button" : "a";
		final String action = screen.isPOST() ? "formaction" : "href";
		final String target = screen.isPOST() ? "formtarget" : "target";
		final String text = "Página&nbsp;%d&nbsp;de&nbsp;%d".formatted(page.getIndx() + 1, paginator.getSize());
		final String first = "%s&pageSize=%d&pageIndx=%d".formatted(url, page.getPaginator().getPageSize(), page.getPaginator().getFirstPageIndx());
		final String prev = "%s&pageSize=%d&pageIndx=%d".formatted(url, page.getPaginator().getPageSize(), page.getPrevIndx());
		final String expand = "%s&pageSize=%d&pageIndx=%d".formatted(url, Math.min(page.getPaginator().getDataSize(), 1000), page.getPaginator().getFirstPageIndx());
		final String collapse = "%s&pageSize=%d&pageIndx=%d".formatted(url, screen.getDefaultPageSize(), page.getPaginator().getFirstPageIndx());
		final String next = "%s&pageSize=%d&pageIndx=%d".formatted(url, page.getPaginator().getPageSize(), page.getNextIndx());
		final String last = "%s&pageSize=%d&pageIndx=%d".formatted(url, page.getPaginator().getPageSize(), page.getPaginator().getLastPageIndx());
		Attributes parameters = new Attributes();

		parameters.set(target, element.getAttributeValue("target"));

		if (screen.isPOST())
			parameters.set("form", element.getAttributeValue("form"));

		StringJoiner string = new StringJoiner(System.lineSeparator());
		string.add(String.format("<g-paginator %s>", attributes.toString()));

		if (!page.isFirst())
		{
			string.add("<%s %s>&lt;&lt;</%s>&nbsp;&nbsp;".formatted(tag, parameters.set("title", "Primeiro").set(action, first), tag));
			string.add("<%s %s>&lt;</%s>&nbsp;&nbsp;".formatted(tag, parameters.set("title", "Anterior").set(action, prev), tag));
		}

		if (page.getSize() == screen.getDefaultPageSize())
			string.add("<%s %s>[%s]</%s>".formatted(tag, parameters.set("title", "Expandir").set(action, expand), text, tag));
		else
			string.add("<%s %s>[%s]</%s>".formatted(tag, parameters.set("title", "Contrair").set(action, collapse), text, tag));

		if (!page.isLast())
		{
			string.add("<%s %s>&gt;</%s>&nbsp;&nbsp;".formatted(tag, parameters.set("title", "Próximo").set(action, next), tag));

			string.add("<%s %s>&gt;&gt;</%s>&nbsp;&nbsp;".formatted(tag, parameters.set("title", "Último").set(action, last), tag));
		}

		string.add(String.format("</g-paginator>"));

		handler.replaceWith(string.toString(), false);
	}
}
