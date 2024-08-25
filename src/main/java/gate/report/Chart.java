package gate.report;

import gate.annotation.Name;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class Chart<T> extends ReportElement
{

	private String caption;
	private Float percentage;

	private final Format format;
	private Category<T> category;
	private final Collection<T> dataset;
	private final List<Value<T>> values = new ArrayList<>();

	public Chart(Class<T> type, Collection<T> dataset, Format format)
	{
		super(new Style());
		this.format = format;
		this.dataset = dataset;
	}

	public Format getFormat()
	{
		return format;
	}

	public Collection<T> getDataset()
	{
		return dataset;
	}

	public String getCaption()
	{
		return caption;
	}

	/**
	 * Sets a addValue to be displayed on the chat's caption.
	 *
	 * @param caption the caption to be displayed on the chat
	 *
	 * @return this, for chained invocations
	 */
	public Chart<T> setCaption(String caption)
	{
		this.caption = caption;
		return this;
	}

	public Float getPercentage()
	{
		return percentage;
	}

	public Chart<T> setPercentage(Float percentage)
	{
		this.percentage = percentage;
		return this;
	}

	public Category<T> getCategory()
	{
		return category;
	}

	public Chart setCategory(Category<T> category)
	{
		this.category = category;
		return this;
	}

	public Chart<T> setCategory(String name, Function<T, Object> value)
	{
		this.category = new Category<>(name, value);
		return this;
	}

	public List<Value<T>> getValues()
	{
		return Collections.unmodifiableList(values);
	}

	public Chart<T> addValue(Value<T> value)
	{
		this.values.add(value);
		return this;
	}

	public Chart<T> addValue(String name, Function<T, Number> value)
	{
		return addValue(new Value(name, value));
	}

	public enum Format
	{
		@Name("Pizza")
		PIE,
		@Name("Linhas")
		LINE,
		@Name("Áreas")
		AREA,
		@Name("Barras")
		BAR,
		@Name("Colunas")
		COLUMN
	}
}
