package gate.thymeleaf.processors.attribute;

import gate.base.Screen;
import gate.lang.property.Property;
import gate.thymeleaf.ELExpressionFactory;
import gate.type.Attributes;
import gate.util.Page;
import gate.util.Parameters;
import java.util.StringJoiner;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class PaginatorAttributeProcessor extends AttributeProcessor
{

	@Inject
	ELExpressionFactory expression;

	public PaginatorAttributeProcessor()
	{
		super(null, "paginator");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		HttpServletRequest request = (HttpServletRequest) ((IWebContext) context).getRequest();
		Parameters queryString = Parameters.parse(request.getQueryString());
		queryString.remove("pageSize");
		queryString.remove("pageIndx");

		String url = String.format("Gate?%s", queryString.toString());
		Screen screen = (Screen) request.getAttribute("screen");

		var value = element.getAttributeValue("g:paginator");
		handler.removeAttribute("g:paginator");
		Page<?> page = value != null && !value.isBlank()
			? (Page<?>) expression.create().evaluate(value)
			: (Page<?>) Property.getValue(screen, "page");

		final String tag = screen.isPOST() ? "button" : "a";
		final String action = screen.isPOST() ? "formaction" : "href";
		final String target = screen.isPOST() ? "formtarget" : "target";
		final String text = "Página&nbsp;%d&nbsp;de&nbsp;%d".formatted(page.getIndx() + 1, page.getPaginator().getSize());
		final String first = "%s&pageSize=%d&pageIndx=%d".formatted(url, page.getPaginator().getPageSize(), page.getPaginator().getFirstPageIndx());
		final String prev = "%s&pageSize=%d&pageIndx=%d".formatted(url, page.getPaginator().getPageSize(), page.getPrevIndx());
		final String expand = "%s&pageSize=%d&pageIndx=%d".formatted(url, Math.min(page.getPaginator().getDataSize(), 1000), page.getPaginator().getFirstPageIndx());
		final String collapse = "%s&pageSize=%d&pageIndx=%d".formatted(url, screen.getDefaultPageSize(), page.getPaginator().getFirstPageIndx());
		final String next = "%s&pageSize=%d&pageIndx=%d".formatted(url, page.getPaginator().getPageSize(), page.getNextIndx());
		final String last = "%s&pageSize=%d&pageIndx=%d".formatted(url, page.getPaginator().getPageSize(), page.getPaginator().getLastPageIndx());
		Attributes parameters = new Attributes();

		parameters.set(target, element.getAttributeValue("g:target"));
		handler.removeAttribute("g:target");

		StringJoiner string = new StringJoiner(System.lineSeparator());
		string.add("<g-paginator>");
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
		string.add("</g-paginator>");

		handler.setBody(string.toString(), false);
	}
}
