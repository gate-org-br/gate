package gate.tags;

import gate.type.Date;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class CalendarTag extends DynamicAttributeTag
{

	private String date;
	private Integer year;
	private Integer month;
	private Integer columns;

	public void doTag() throws JspException, IOException
	{
		if (getMonth() == null)
			doYear(new Date(1, 1, getYear()));
		else
			doMonth(new Date(1, 1, getYear()), new Date(1, getMonth(), getYear()));
	}

	public void doYear(Date year) throws IOException, JspException
	{
		getJspContext().getOut().println(String.format("<table %s>", getMonth() == null ? getAttributes().toString() : ""));
		getJspContext().getOut().println(String.format("<caption>%d</caption>", year.getYear().getValue()));
		getJspContext().getOut().println("<tbody>");

		for (int i = 0; i < 12; i++)
		{
			if (i % getColumns() == 0)
				getJspContext().getOut().println("<tr>");
			getJspContext().getOut().println("<td>");
			doMonth(year, new Date(1, i, getYear()));
			getJspContext().getOut().println("</td>");
			if ((i + 1) % getColumns() == 0)
				getJspContext().getOut().println("</tr>");
		}

		getJspContext().getOut().println("</tbody>");
		getJspContext().getOut().println("</table>");
	}

	public void doMonth(Date year, Date month) throws IOException, JspException
	{
		getJspContext().getOut().println(String.format("<table %s>", getMonth() != null ? getAttributes().toString() : ""));
		getJspContext().getOut().println(String.format("<caption>%s de %d</caption>", month.getMonth().getFullName(), month.getYear().getValue()));

		getJspContext().getOut().println("<thead>");
		List<Date> names = new Date().getDayOfWeek().getAll();
		for (int i = 1; i < names.size(); i++)
			getJspContext().getOut().println(String.format("<th style='text-align: center;'>%S</th>", names.get(i).getDayOfWeek().getName()));
		getJspContext().getOut().println(String.format("<th style='text-align: center;'>%S</th>", names.get(0).getDayOfWeek().getName()));
		getJspContext().getOut().println("</thead>");

		getJspContext().getOut().println("<tbody>");

		for (int i = 1; i <= 6; i++)
			doWeek(year, month, month.getWeekOfMonth().set(i));

		getJspContext().getOut().println("</tbody>");
		getJspContext().getOut().println("</table>");
	}

	public void doWeek(Date year, Date month, Date week) throws IOException, JspException
	{
		getJspContext().getOut().println("<tr>");
		List<Date> days = week.getDayOfWeek().getAll();
		for (int i = 1; i < days.size(); i++)
			doDate(year, month, week, days.get(i));
		doDate(year, month, week, days.get(0));
		getJspContext().getOut().println("</tr>");
	}

	public void doDate(Date year, Date month, Date week, Date date) throws IOException, JspException
	{
		getJspContext().getOut().println("<td style='text-align: center;'>");
		getJspContext().setAttribute(getDate(), date, PageContext.REQUEST_SCOPE);
		if (date.getYear().getValue() == year.getYear().getValue())
			if (date.getMonth().getValue() == month.getMonth().getValue())
				getJspBody().invoke(null);
		getJspContext().removeAttribute(getDate(), PageContext.REQUEST_SCOPE);
		getJspContext().getOut().println("</td>");
	}

	public Integer getYear()
	{
		return year;
	}

	public void setYear(Integer year)
	{
		if (year < 0)
			throw new IllegalArgumentException("year");
		this.year = year;
	}

	public Integer getMonth()
	{
		return month;
	}

	public void setMonth(Integer month)
	{
		if (month < 0 || month > 11)
			throw new IllegalArgumentException("month");
		this.month = month;
	}

	public Integer getColumns()
	{
		if (columns == null)
			columns = 3;
		return columns;
	}

	public void setColumns(Integer columns)
	{
		if (columns != 1)
			if (columns != 2)
				if (columns != 3)
					if (columns != 4)
						if (columns != 6)
							if (columns != 12)
								throw new IllegalArgumentException("columns");
		this.columns = columns;
	}

	public String getDate()
	{
		if (date == null)
			date = "date";
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}
}
