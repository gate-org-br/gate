package gate.report;

import static gate.report.Chart.Format.AREA;
import static gate.report.Chart.Format.BAR;
import static gate.report.Chart.Format.COLUMN;
import static gate.report.Chart.Format.LINE;
import static gate.report.Chart.Format.PIE;
import java.awt.Font;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.util.TableOrder;
import org.jfree.data.category.DefaultCategoryDataset;

public class ChartGenerator
{

	public static <T> JFreeChart create(Chart<T> chart)
	{
		boolean integers = true;
		DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();

		for (T obj : chart.getDataset())
		{
			String categoryName = chart.getCategory().getValue().apply(obj);

			for (Value<T> value : chart.getValues())
			{
				Number number = value.getValue().apply(obj);
				if (number.doubleValue() != number.longValue())
					integers = false;

				categoryDataset.addValue(number, value.getName(), categoryName);
			}
		}
		try
		{

			switch (chart.getFormat())
			{
				case BAR:
				{
					JFreeChart jfreechat = ChartFactory.createBarChart(chart.getCaption(),
						chart.getCategory().getName(), null, categoryDataset,
						PlotOrientation.HORIZONTAL, true, false, false);
					if (integers)
						jfreechat.getCategoryPlot().getRangeAxis()
							.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

					return jfreechat;
				}
				case LINE:
				{
					JFreeChart jfreechat = ChartFactory.createLineChart(chart.getCaption(),
						chart.getCategory().getName(), null, categoryDataset,
						PlotOrientation.VERTICAL, true, false, false);
					if (integers)
						jfreechat.getCategoryPlot().getRangeAxis()
							.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

					return jfreechat;
				}

				case AREA:
				{

					JFreeChart jfreechart = ChartFactory.createAreaChart(chart.getCaption(),
						chart.getCategory().getName(), null, categoryDataset,
						PlotOrientation.VERTICAL, true, false, false);
					if (integers)
						jfreechart.getCategoryPlot().getRangeAxis()
							.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

					return jfreechart;
				}
				case COLUMN:
				{
					JFreeChart jfreechart = ChartFactory.createBarChart(chart.getCaption(),
						chart.getCategory().getName(), null, categoryDataset,
						PlotOrientation.VERTICAL, true, false, false);
					if (integers)
						jfreechart.getCategoryPlot().getRangeAxis()
							.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

					return jfreechart;
				}
				case PIE:
				{
					JFreeChart jfreechart = ChartFactory.createMultiplePieChart(chart.getCaption(),
						categoryDataset, TableOrder.BY_ROW, true, true, false);

					Font titleFont = new Font("Arial", Font.BOLD, 12);
					Font labelFont = new Font("Arial", Font.PLAIN, 8);

					jfreechart.getTitle().setFont(titleFont);
					jfreechart.getLegend().setItemFont(labelFont);

					MultiplePiePlot plot = (MultiplePiePlot) jfreechart.getPlot();
					((PiePlot) plot.getPieChart().getPlot()).setLabelFont(labelFont);

					plot.getPieChart().getTitle().setFont(titleFont);

					return jfreechart;
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
