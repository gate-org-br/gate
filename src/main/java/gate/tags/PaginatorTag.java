package gate.tags;

import gate.base.Screen;
import gate.lang.property.Property;
import gate.util.Page;
import gate.util.QueryString;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class PaginatorTag extends AttributeTag
{

	private Page<?> page;

	@Override
	public void doTag() throws JspException, IOException
	{
		HttpServletRequest request = (HttpServletRequest) ((PageContext) getJspContext()).getRequest();
		QueryString qs = new QueryString(request.getQueryString());
		qs.remove("pageSize");
		qs.remove("pageIndx");
		String url = String.format("Gate?%s", qs.toString());
		Screen screen = (Screen) getJspContext().findAttribute("screen");
		if (getPage() == null)
			setPage((Page<?>) Property.getValue(screen, "page"));

		getJspContext().getOut().write(String.format("<span %s>", getAttributes().toString()));
		if (!getPage().isFrst())
		{
			getJspContext().getOut().write(
					String.format(
							"<button formaction='%s&pageSize=%d&pageIndx=%d' title='Primeiro'>&lt;&lt;</button>&nbsp;&nbsp;",
							url, getPage()
									.getPaginator()
									.getPageSize(), getPage().getPaginator().getFrstPageIndx()));
			getJspContext().getOut().write(
					String.format(
							"<button formaction='%s&pageSize=%d&pageIndx=%d' title='Anterior'>&lt;</button>&nbsp;&nbsp;",
							url, getPage().getPaginator()
									.getPageSize(), getPage().getPrevIndx()));
		}

		if (getPage().getSize() == screen.getDefaultPageSize())
			getJspContext().getOut().write(
					String.format(
							"<button formaction='%s&pageSize=%d&pageIndx=%d' title='Expandir'>[P&aacute;gina&nbsp;%d&nbsp;de&nbsp;%d]</button>",
							url,
							Math.min(getPage().getPaginator().getDataSize(), 1000), getPage().getPaginator()
							.getFrstPageIndx(), getPage().getIndx() + 1,
							getPage().getPaginator().getSize()));
		else
			getJspContext().getOut().write(
					String.format(
							"<button formaction='%s&pageSize=%d&pageIndx=%d' title='Contrair'>[P&aacute;gina&nbsp;%d&nbsp;de&nbsp;%d]</button>",
							url,
							screen.getDefaultPageSize(), getPage().getPaginator().getFrstPageIndx(),
							getPage().getIndx() + 1, getPage().getPaginator()
							.getSize()));

		if (!getPage().isLast())
		{
			getJspContext().getOut().write(
					String.format(
							"&nbsp;&nbsp;<button formaction='%s&pageSize=%d&pageIndx=%d' title='Pr&oacute;ximo'>&gt;</button>",
							url, getPage()
									.getPaginator().getPageSize(), getPage().getNextIndx()));
			getJspContext().getOut().write(
					String.format(
							"&nbsp;&nbsp;<button formaction='%s&pageSize=%d&pageIndx=%d' title='&Uacute;ltimo'>&gt;&gt;</button>",
							url, getPage()
									.getPaginator().getPageSize(), getPage().getPaginator().getLastPageIndx()));
		}
		getJspContext().getOut().write("</span>");
	}

	public Page<?> getPage()
	{
		return page;
	}

	public void setPage(Page<?> page)
	{
		this.page = page;
	}
}
