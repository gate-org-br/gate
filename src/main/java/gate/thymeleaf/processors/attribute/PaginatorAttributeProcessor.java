package gate.thymeleaf.processors.attribute;

import gate.base.Screen;
import gate.lang.property.Property;
import gate.thymeleaf.ELExpression;
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
	ELExpression expression;

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
			? (Page<?>) expression.evaluate(value)
			: (Page<?>) Property.getValue(screen, "page");

		StringJoiner string = new StringJoiner(System.lineSeparator());
		string.add("<g-paginator>");
		if (screen.isPOST())
		{
			if (!page.isFrst())
				string.add(String.format("<button formaction='%s&pageSize=%d&pageIndx=%d' title='Primeiro'>&lt;&lt;</button>&nbsp;&nbsp;",
					url, page.getPaginator().getPageSize(), page.getPaginator().getFrstPageIndx()))
					.add(String.format("<button formaction='%s&pageSize=%d&pageIndx=%d' title='Anterior'>&lt;</button>&nbsp;&nbsp;",
						url, page.getPaginator().getPageSize(), page.getPrevIndx()));

			if (page.getSize() == screen.getDefaultPageSize())
				string.add(String.format("<button formaction='%s&pageSize=%d&pageIndx=%d' title='Expandir'>[P&aacute;gina&nbsp;%d&nbsp;de&nbsp;%d]</button>",
					url, Math.min(page.getPaginator().getDataSize(), 1000), page.getPaginator().getFrstPageIndx(), page.getIndx() + 1, page.getPaginator().getSize()));
			else
				string.add(String.format("<button formaction='%s&pageSize=%d&pageIndx=%d' title='Contrair'>[P&aacute;gina&nbsp;%d&nbsp;de&nbsp;%d]</button>",
					url, screen.getDefaultPageSize(), page.getPaginator().getFrstPageIndx(), page.getIndx() + 1, page.getPaginator().getSize()));

			if (!page.isLast())
				string.add(String.format("&nbsp;&nbsp;<button formaction='%s&pageSize=%d&pageIndx=%d' title='Pr&oacute;ximo'>&gt;</button>",
					url, page.getPaginator().getPageSize(), page.getNextIndx()))
					.add(String.format("&nbsp;&nbsp;<button formaction='%s&pageSize=%d&pageIndx=%d' title='&Uacute;ltimo'>&gt;&gt;</button>",
						url, page.getPaginator().getPageSize(), page.getPaginator().getLastPageIndx()));
		} else if (screen.isGET())
		{
			if (!page.isFrst())
				string.add(String.format("<a href='%s&pageSize=%d&pageIndx=%d' title='Primeiro'>&lt;&lt;</a>&nbsp;&nbsp;",
					url, page.getPaginator().getPageSize(), page.getPaginator().getFrstPageIndx()))
					.add(String.format("<a href='%s&pageSize=%d&pageIndx=%d' title='Anterior'>&lt;</a>&nbsp;&nbsp;",
						url, page.getPaginator().getPageSize(), page.getPrevIndx()));

			if (page.getSize() == screen.getDefaultPageSize())
				string.add(String.format("<a href='%s&pageSize=%d&pageIndx=%d' title='Expandir'>[P&aacute;gina&nbsp;%d&nbsp;de&nbsp;%d]</a>",
					url, Math.min(page.getPaginator().getDataSize(), 1000), page.getPaginator().getFrstPageIndx(), page.getIndx() + 1, page.getPaginator().getSize()));
			else
				string.add(String.format("<a href='%s&pageSize=%d&pageIndx=%d' title='Contrair'>[P&aacute;gina&nbsp;%d&nbsp;de&nbsp;%d]</a>",
					url, screen.getDefaultPageSize(), page.getPaginator().getFrstPageIndx(), page.getIndx() + 1, page.getPaginator().getSize()));

			if (!page.isLast())
				string.add(String.format("&nbsp;&nbsp;<a href='%s&pageSize=%d&pageIndx=%d' title='Pr&oacute;ximo'>&gt;</a>",
					url, page.getPaginator().getPageSize(), page.getNextIndx()))
					.add(String.format("&nbsp;&nbsp;<a href='%s&pageSize=%d&pageIndx=%d' title='&Uacute;ltimo'>&gt;&gt;</a>",
						url, page.getPaginator().getPageSize(), page.getPaginator().getLastPageIndx()));
		}
		string.add("</g-paginator>");
		handler.setBody(string.toString(), false);
	}
}
