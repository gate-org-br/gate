package gate.tags.grid;

import gate.lang.property.Property;
import gate.converter.Converter;
import gate.tags.DynamicAttributeTag;
import gate.util.Toolkit;
import gate.util.Page;
import gate.util.QueryString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GridTag extends DynamicAttributeTag
{

	private Object source;
	private String action;
	private Method method;
	private Target target;
	private Method sortable;
	private final List<ColumnTag> cols = new ArrayList<>();
	private final Pattern pattern = Pattern.compile("[!][{]([a-zA-Z0-9_$]+)[}]");

	@Override
	public void doTag() throws JspException, IOException
	{
		super.doTag();
		getJspBody().invoke(getJspContext().getOut());

		int size = source instanceof Page<?>
				? ((Page<?>) source).getPaginator().getDataSize()
				: Toolkit.getSize(source);

		if (size > 0)
		{

			getJspContext().getOut().print(String.format("<table %s>", getAttributes().toString()));

			getJspContext().getOut().print(String.format("<caption>REGISTROS ENCONTRADOS: %d</caption>", size));

			getJspContext().getOut().print("<colgroup>");
			for (ColumnTag col : cols)
				if (col.getSize() == null)
					getJspContext().getOut().print("<col/>");
				else
					getJspContext().getOut().print(String.format("<col style='width: %s'/>", col.getSize()));
			getJspContext().getOut().print("</colgroup>");

			getJspContext().getOut().print("<thead>");
			getJspContext().getOut().print("<tr>");
			for (ColumnTag col : cols)
			{
				getJspContext().getOut().print(String.format("<th style='text-align: %s'>", col.getAlign().name()));
				if (sortable != null)
				{
					HttpServletRequest request = (HttpServletRequest) ((PageContext) getJspContext()).getRequest();
					String orderBy = request.getParameter("orderBy");
					QueryString qs = new QueryString(request.getQueryString());
					qs.remove("orderBy");

					if (Method.post.equals(sortable))
					{
						if (col.getProperty().equals(orderBy))
						{
							qs.put("orderBy", "-".concat(col.getProperty()));
							getAttributes().put("formaction", String.format("Gate?%s", qs.toString()));
							getJspContext().getOut().write(String.format("<button %s>&uarr;&nbsp;%s</button>", getAttributes().toString(), Converter.toText(col
									.getName())));
						} else if ("-".concat(col.getProperty()).equals(orderBy))
						{
							getAttributes().put("formaction", String.format("Gate?%s", qs.toString()));
							getJspContext().getOut().write(String.format("<button %s>&darr;&nbsp;%s</button>", getAttributes().toString(), Converter.toText(col
									.getName())));
						} else
						{
							qs.put("orderBy", col.getProperty());
							getAttributes().put("formaction", String.format("Gate?%s", qs.toString()));
							getJspContext().getOut().write(String.format("<button %s>%s</button>", getAttributes().toString(), Converter.toText(col.getName())));
						}
					} else if (col.getProperty().equals(orderBy))
					{
						qs.put("orderBy", "-".concat(col.getProperty()));
						getAttributes().put("href", String.format("Gate?%s", qs.toString()));
						getJspContext().getOut().write(String.format("<a %s>&uarr;&nbsp;%s</a>", getAttributes().toString(), Converter.toText(col.getName())));
					} else if ("-".concat(col.getProperty()).equals(orderBy))
					{
						getAttributes().put("href", String.format("Gate?%s", qs.toString()));
						getJspContext().getOut().write(String.format("<a %s>&darr;&nbsp;%s</a>", getAttributes().toString(), Converter.toText(col.getName())));
					} else
					{
						qs.put("orderBy", col.getProperty());
						getAttributes().put("href", String.format("Gate?%s", qs.toString()));
						getJspContext().getOut().write(String.format("<a %s>%s</a>", getAttributes().toString(), Converter.toText(col.getName())));
					}
				} else
					getJspContext().getOut().print(Converter.toText(col.getName()));

				getJspContext().getOut().print("</th>");
			}
			getJspContext().getOut().print("</tr>");
			getJspContext().getOut().print("</thead>");

			getJspContext().getOut().print("<tbody>");
			for (Object target : Toolkit.iterable(this.source))
			{
				if (action != null)
				{
					String url = action;
					Matcher matcher = pattern.matcher(url);
					while (matcher.find())
					{
						String group = matcher.group();
						Property property = Property.getProperty(target.getClass(), group.substring(2, group.length() - 1));
						url = url.replace(group, Converter.toString(property.getValue(target)));
					}

					getJspContext().getOut().print(String.format("<tr data-action='%s' data-method='%s' data-target='%s'>", url, getMethod().name(), getTarget()
							.name()));
				} else
					getJspContext().getOut().print("<tr>");

				for (ColumnTag col : cols)
					col.print(target);
				getJspContext().getOut().print("</tr>");
			}
			getJspContext().getOut().print("</tbody>");

			if (source instanceof Page)
			{
				getJspContext().getOut().print("<tfoot>");
				getJspContext().getOut().print("<tr>");
				getJspContext().getOut().print(String.format("<td colspan='%d' style='text-align: right'>", cols.size()));

				Page<?> page = (Page<?>) source;
				HttpServletRequest request = (HttpServletRequest) ((PageContext) getJspContext()).getRequest();
				QueryString qs = new QueryString(request.getQueryString());
				qs.remove("pageSize");
				qs.remove("pageIndx");
				String url = String.format("Gate?%s", qs.toString());

				getJspContext().getOut().write(String.format("<span %s>", getAttributes().toString()));
				if (!page.isFrst())
				{
					getJspContext().getOut().write(String.format("<button formaction='%s&pageSize=%d&pageIndx=%d'>&lt;&lt;</button>&nbsp;&nbsp;", url, page
							.getPaginator().getPageSize(), page.getPaginator().getFrstPageIndx()));
					getJspContext().getOut().write(String.format("<button formaction='%s&pageSize=%d&pageIndx=%d'>&lt;</button>&nbsp;&nbsp;", url, page
							.getPaginator().getPageSize(), page.getPrevIndx()));
				}
				getJspContext().getOut().write(String.format("<button formaction='%s&pageSize=%d&pageIndx=%d'>[P&Aacute;GINA&nbsp;%d&nbsp;DE&nbsp;%d]</button>",
						url, page.getSize() == page.getPaginator().getDataSize() ? 10 : page.getPaginator().getDataSize(), page.getPaginator().getFrstPageIndx(),
						page.getIndx() + 1, page.getPaginator().getSize()));
				if (!page.isLast())
				{
					getJspContext().getOut().write(String.format("&nbsp;&nbsp;<button formaction='%s&pageSize=%d&pageIndx=%d'>&gt;</button>", url, page
							.getPaginator().getPageSize(), page.getNextIndx()));
					getJspContext().getOut().write(String.format("&nbsp;&nbsp;<button formaction='%s&pageSize=%d&pageIndx=%d'>&gt;&gt;</button>", url, page
							.getPaginator().getPageSize(), page.getPaginator().getLastPageIndx()));
				}
				getJspContext().getOut().write("</span>");

				getJspContext().getOut().print("</td>");
				getJspContext().getOut().print("</tr>");
				getJspContext().getOut().print("</tfoot>");
			}

			getJspContext().getOut().print("</table>");
		} else
		{
			getJspContext().getOut().print("<div class='TEXT'>");
			getJspContext().getOut().print("<h1>");
			getJspContext().getOut().print("Nenhum registro encontrado");
			getJspContext().getOut().print("</h1>");
			getJspContext().getOut().print("</div>");
		}
	}

	public void setSource(Object source)
	{
		this.source = source;
	}

	public void setSortable(Method sortable)
	{
		this.sortable = sortable;
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public Method getMethod()
	{
		if (method == null)
			method = Method.get;
		return method;
	}

	public void setMethod(Method method)
	{
		this.method = method;
	}

	public Target getTarget()
	{
		if (target == null)
			target = Target._self;
		return target;
	}

	public void setTarget(Target target)
	{
		this.target = target;
	}

	public List<ColumnTag> getCols()
	{
		return cols;
	}

	public enum Method
	{
		get, post
	}

	public enum Target
	{
		_blank, _self, _dialog
	}

}
