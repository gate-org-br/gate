package gate.report;

import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.Font;
import java.util.Collections;

public final class Form extends ReportElement
{

	private Font font;
	private String caption;
	private Float percentage;
	private final int columns;
	private final List<FormElement> elements = new ArrayList<>();

	Form(Report report, int columns)
	{
		super(report);
		if (columns <= 0)
			throw new IllegalArgumentException("columns");
		this.columns = columns;
	}

	Form(Report report, gate.type.Form form)
	{
		super(report);
		this.columns = 8;
		form.getFields().stream().forEach((e) ->
		{
			if (Boolean.TRUE.equals(e.getMultiple()))
				elements.add(new Field(e.getName(), e.getValue()).colspan(e.getSize().ordinal() + 1).height(40f));
			else
				elements.add(new Field(e.getName(), e.getValue()).colspan(e.getSize().ordinal() + 1));
		});
	}

	public String getCaption()
	{
		return caption;
	}

	public List<FormElement> getElements()
	{
		return Collections.unmodifiableList(elements);
	}

	public Field add(String label, Object value)
	{
		Field field = new Field(label, value);
		elements.add(field);
		return field;
	}

	public Float getPercentage()
	{
		if (percentage == null)
			percentage = 100F;
		return percentage;
	}

	public int getColumns()
	{
		return columns;
	}

	public Form setCaption(String caption)
	{
		this.caption = caption;
		return this;
	}

	public Form setPercentage(Float percentage)
	{
		this.percentage = percentage;
		return this;
	}
}
