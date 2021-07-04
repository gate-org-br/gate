package gate.tags;

import gate.base.Screen;
import gate.lang.property.Property;
import gate.util.Page;
import gate.util.Parameters;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

public class PaginatorTag extends AttributeTag
{

	private Page<?> page;

	@Override
	public void doTag() throws IOException
	{
		HttpServletRequest request = (HttpServletRequest) ((PageContext) getJspContext()).getRequest();

		Parameters queryString = Parameters.parse(request.getQueryString());
		queryString.remove("pageSize");
		queryString.remove("pageIndx");

		String url = String.format("Gate?%s", queryString.toString());
		Screen screen = (Screen) getJspContext().findAttribute("screen");

		if (page == null)
			setPage((Page<?>) Property.getValue(screen, "page"));

		getJspContext().getOut().write(String.format("<g-paginator %s>", getAttributes().toString()));

		if (screen.isPOST())
		{
			if (!page.isFrst())
			{
				getJspContext().getOut().write(String.format("<button formaction='%s&pageSize=%d&pageIndx=%d' title='Primeiro'>&lt;&lt;</button>&nbsp;&nbsp;", url, page.getPaginator().getPageSize(), page.getPaginator().getFrstPageIndx()));
				getJspContext().getOut().write(String.format("<button formaction='%s&pageSize=%d&pageIndx=%d' title='Anterior'>&lt;</button>&nbsp;&nbsp;", url, page.getPaginator().getPageSize(), page.getPrevIndx()));
			}

			if (page.getSize() == screen.getDefaultPageSize())
				getJspContext().getOut().write(String.format("<button formaction='%s&pageSize=%d&pageIndx=%d' title='Expandir'>[P&aacute;gina&nbsp;%d&nbsp;de&nbsp;%d]</button>", url, Math.min(page.getPaginator().getDataSize(), 1000), page.getPaginator().getFrstPageIndx(), page.getIndx() + 1, page.getPaginator().getSize()));
			else
				getJspContext().getOut().write(String.format("<button formaction='%s&pageSize=%d&pageIndx=%d' title='Contrair'>[P&aacute;gina&nbsp;%d&nbsp;de&nbsp;%d]</button>", url, screen.getDefaultPageSize(), page.getPaginator().getFrstPageIndx(), page.getIndx() + 1, page.getPaginator().getSize()));

			if (!page.isLast())
			{
				getJspContext().getOut().write(String.format("&nbsp;&nbsp;<button formaction='%s&pageSize=%d&pageIndx=%d' title='Pr&oacute;ximo'>&gt;</button>", url, page.getPaginator().getPageSize(), page.getNextIndx()));
				getJspContext().getOut().write(String.format("&nbsp;&nbsp;<button formaction='%s&pageSize=%d&pageIndx=%d' title='&Uacute;ltimo'>&gt;&gt;</button>", url, page.getPaginator().getPageSize(), page.getPaginator().getLastPageIndx()));
			}
		} else if (screen.isGET())
		{
			if (!page.isFrst())
			{
				getJspContext().getOut().write(String.format("<a href='%s&pageSize=%d&pageIndx=%d' title='Primeiro'>&lt;&lt;</a>&nbsp;&nbsp;", url, page.getPaginator().getPageSize(), page.getPaginator().getFrstPageIndx()));
				getJspContext().getOut().write(String.format("<a href='%s&pageSize=%d&pageIndx=%d' title='Anterior'>&lt;</a>&nbsp;&nbsp;", url, page.getPaginator().getPageSize(), page.getPrevIndx()));
			}

			if (page.getSize() == screen.getDefaultPageSize())
				getJspContext().getOut().write(String.format("<a href='%s&pageSize=%d&pageIndx=%d' title='Expandir'>[P&aacute;gina&nbsp;%d&nbsp;de&nbsp;%d]</a>", url, Math.min(page.getPaginator().getDataSize(), 1000), page.getPaginator().getFrstPageIndx(), page.getIndx() + 1, page.getPaginator().getSize()));
			else
				getJspContext().getOut().write(String.format("<a href='%s&pageSize=%d&pageIndx=%d' title='Contrair'>[P&aacute;gina&nbsp;%d&nbsp;de&nbsp;%d]</a>", url, screen.getDefaultPageSize(), page.getPaginator().getFrstPageIndx(), page.getIndx() + 1, page.getPaginator().getSize()));

			if (!page.isLast())
			{
				getJspContext().getOut().write(String.format("&nbsp;&nbsp;<a href='%s&pageSize=%d&pageIndx=%d' title='Pr&oacute;ximo'>&gt;</a>", url, page.getPaginator().getPageSize(), page.getNextIndx()));
				getJspContext().getOut().write(String.format("&nbsp;&nbsp;<a href='%s&pageSize=%d&pageIndx=%d' title='&Uacute;ltimo'>&gt;&gt;</a>", url, page.getPaginator().getPageSize(), page.getPaginator().getLastPageIndx()));
			}
		}

		getJspContext().getOut().write("</g-paginator>");
	}

	public void setPage(Page<?> page)
	{
		this.page = page;
	}
}
