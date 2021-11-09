package gate.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Form extends ReportElement
{

	private String caption;
	private Float percentage;
	private final int columns;
	private final List<Field> elements = new ArrayList<>();

	public Form(int columns)
	{
		super(new Style());
		if (columns <= 0)
			throw new IllegalArgumentException("columns");
		this.columns = columns;
	}

	public Form(gate.type.Form form)
	{
		super(new Style());
		this.columns = 8;
		form.getFields().forEach((e) ->
		{
			if (Boolean.TRUE.equals(e.getMultiple()))
				elements.add(new Field(e.getName(), e.getValue()).colspan(e.getSize().ordinal() + 1).height(40f));
			else
				elements.add(new Field(e.getName(), e.getValue()).colspan(e.getSize().ordinal() + 1));
		});
	}

	public final String getCaption()
	{
		return caption;
	}

	public final List<Field> getFields()
	{
		return Collections.unmodifiableList(elements);
	}

	public final Form add(Field field)
	{
		elements.add(field);
		return this;
	}

	public final Field add(String label, Object value)
	{
		Field field = new Field(label, value);
		elements.add(field);
		return field;
	}

	public final Form add(gate.type.Form form)
	{
		Objects.requireNonNull(form);

		if (!form.getFields().isEmpty())
		{

			form.getFields().forEach(e -> add(e.getName(), e.getValue())
				.colspan(e.getSize().ordinal() + 1));

			int count = form.getFields().stream()
				.mapToInt(e -> e.getSize().ordinal() + 1)
				.sum() % columns;
			if (count > 0)
			{
				Field field = (Field) elements.get(elements.size() - 1);
				field.colspan(field.getColspan() + count);
			}
		}
		return this;
	}

	public final Float getPercentage()
	{
		if (percentage == null)
			percentage = 100F;
		return percentage;
	}

	public final int getColumns()
	{
		return columns;
	}

	public final Form setCaption(String caption)
	{
		this.caption = caption;
		return this;
	}

	public final Form setPercentage(Float percentage)
	{
		this.percentage = percentage;
		return this;
	}

	@Override
	public final Form style(Style style)
	{
		return (Form) super.style(style);
	}

	@Override
	public Element compact()
	{
		elements.removeIf(e -> e.getValue() == null);
		if (!elements.isEmpty())
			while (elements.stream().mapToInt(Field::getColspan).sum() % columns != 0)
			{
				int min = elements.stream().mapToInt(Field::getColspan).min().orElse(0);
				elements.stream().filter(e -> e.getColspan() == min).findFirst().ifPresent(e -> e.colspan(e.getColspan() + 1));
			}

		return this;
	}

	@Override
	public boolean isEmpty()
	{
		return elements.isEmpty();
	}

}
