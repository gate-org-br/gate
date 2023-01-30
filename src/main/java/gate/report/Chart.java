package gate.report;

import java.awt.Font;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.encoders.EncoderUtil;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.util.TableOrder;

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

	public Chart<T> setCategory(String name, Function<T, String> value)
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
		PIE, LINE, AREA, BAR, COLUMN
	}

	public byte[] create(int width, int height)
	{
		boolean integers = true;
		DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();

		for (T obj : dataset)
		{
			String categoryName = getCategory().getValue().apply(obj);

			for (Value<T> value : values)
			{
				Number number = value.getValue().apply(obj);
				if (number.doubleValue() != number.longValue())
					integers = false;

				categoryDataset.addValue(number, value.getName(), categoryName);
			}
		}
		try
		{

			switch (format)
			{
				case BAR:
				{
					JFreeChart chart = ChartFactory.createBarChart(getCaption(),
						getCategory().getName(), null, categoryDataset,
						PlotOrientation.HORIZONTAL, true, false, false);
					if (integers)
						chart.getCategoryPlot().getRangeAxis()
							.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

					return EncoderUtil.encode(chart.createBufferedImage(width, height), "png");
				}
				case LINE:
				{
					JFreeChart chart = ChartFactory.createLineChart(getCaption(),
						getCategory().getName(), null, categoryDataset,
						PlotOrientation.HORIZONTAL, true, false, false);
					if (integers)
						chart.getCategoryPlot().getRangeAxis()
							.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

					return EncoderUtil.encode(chart.createBufferedImage(width, height), "png");
				}

				case AREA:
				{

					JFreeChart chart = ChartFactory.createAreaChart(getCaption(),
						getCategory().getName(), null, categoryDataset,
						PlotOrientation.HORIZONTAL, true, false, false);
					if (integers)
						chart.getCategoryPlot().getRangeAxis()
							.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

					return EncoderUtil.encode(chart.createBufferedImage(width, height), "png");
				}
				case COLUMN:
				{
					JFreeChart chart = ChartFactory.createBarChart(getCaption(),
						getCategory().getName(), null, categoryDataset,
						PlotOrientation.VERTICAL, true, false, false);
					if (integers)
						chart.getCategoryPlot().getRangeAxis()
							.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

					return EncoderUtil.encode(chart.createBufferedImage(width, height), "png");
				}
				case PIE:
				{
					JFreeChart chart = ChartFactory.createMultiplePieChart(getCaption(),
						categoryDataset, TableOrder.BY_ROW, true, true, false);

					Font titleFont = new Font("Arial", Font.BOLD, 12);
					Font labelFont = new Font("Arial", Font.PLAIN, 8);

					chart.getTitle().setFont(titleFont);
					chart.getLegend().setItemFont(labelFont);

					MultiplePiePlot plot = (MultiplePiePlot) chart.getPlot();
					((PiePlot) plot.getPieChart().getPlot()).setLabelFont(labelFont);

					plot.getPieChart().getTitle().setFont(titleFont);

					return EncoderUtil.encode(chart.createBufferedImage(width, height), "png");
				}
				default:
					throw new IOException();
			}
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}
