package gate.report.doc;

import gate.annotation.Icon;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.report.Column;
import gate.report.Field;
import gate.report.Form;
import gate.report.Grid;
import gate.report.Report;
import gate.report.ReportElement;
import gate.report.Style;
import gate.util.Toolkit;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

/**
 * Generates XLS documents from objects of type {@link gate.report.Report}.
 */
@Icon("2221")
public class XLS extends Doc
{

	private int i;

	/**
	 * Constructs a new XLS Doc for the specified report.
	 *
	 * @param report the report to be used to generate the document
	 */
	public XLS(Report report)
	{
		super(report);
	}

	@Override
	public String getContentType()
	{
		return "application";
	}

	@Override
	public String getContentSubtype()
	{
		return "xls";
	}

	@Override
	public String getFileName()
	{
		return String.format("%s.xlsx", getReport().getName());
	}

	@Override
	public void print(OutputStream os)
	{
		try (SXSSFWorkbook workbook = new SXSSFWorkbook())
		{
			for (ReportElement e : getReport().getElements())
				if (e instanceof Grid)
					printGrid(workbook, (Grid) e);
				else if (e instanceof Form)
					printForm(workbook, (Form) e);
			workbook.write(os);
			workbook.dispose();
		} catch (IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}

	private void printForm(SXSSFWorkbook workbook, Form form) throws ConversionException
	{
		short index = -1;

		SXSSFSheet sheet
				= form.getCaption() != null
				? workbook.createSheet(form.getCaption().replaceAll("[^a-zA-Z0-9 ]", " "))
				: workbook.createSheet();
		for (Field e : form.getElements().stream().filter(e -> e instanceof Field).map(e -> (Field) e).collect(Collectors.toList()))
		{
			sheet.trackAllColumnsForAutoSizing();

			SXSSFRow row = sheet.createRow(++index);

			SXSSFCell label = row.createCell((short) 0);
			label.setCellStyle(workbook.createCellStyle());
			label.getCellStyle().setBorderTop(BorderStyle.THIN);
			label.getCellStyle().setBorderLeft(BorderStyle.THIN);
			label.getCellStyle().setBorderRight(BorderStyle.NONE);
			label.getCellStyle().setBorderBottom(BorderStyle.THIN);
			label.getCellStyle().setFillPattern(FillPatternType.SOLID_FOREGROUND);

			label.getCellStyle().setFont(workbook.createFont());
			label.getCellStyle().setAlignment(HorizontalAlignment.LEFT);
			label.getCellStyle().setFillPattern(FillPatternType.SOLID_FOREGROUND);
			((XSSFCellStyle) label.getCellStyle()).setFillForegroundColor(new XSSFColor(Color.GRAY));
			((XSSFCellStyle) label.getCellStyle()).getFont().setBold(true);
			label.setCellValue(new XSSFRichTextString(Converter.toText(e.getName())));

			SXSSFCell value = row.createCell((short) 1);
			value.setCellStyle(workbook.createCellStyle());
			value.getCellStyle().setBorderTop(BorderStyle.THIN);
			value.getCellStyle().setBorderLeft(BorderStyle.NONE);
			value.getCellStyle().setBorderRight(BorderStyle.THIN);
			value.getCellStyle().setBorderBottom(BorderStyle.THIN);
			value.getCellStyle().setFillPattern(FillPatternType.SOLID_FOREGROUND);

			value.getCellStyle().setFont(workbook.createFont());
			value.getCellStyle().setAlignment(HorizontalAlignment.LEFT);
			((XSSFCellStyle) value.getCellStyle()).setFillForegroundColor(new XSSFColor(Color.white));
			value.getCellStyle().setFillPattern(FillPatternType.SOLID_FOREGROUND);
			((XSSFCellStyle) value.getCellStyle()).getFont().setBold(true);

			if (e.getValue() == null)
				value.setCellType(CellType.BLANK);
			else if (e.getValue() instanceof Byte)
			{
				value.setCellType(CellType.NUMERIC);
				value.setCellValue(((Byte) e.getValue()).doubleValue());
			} else if (e.getValue() instanceof Short)
			{
				value.setCellType(CellType.NUMERIC);
				value.setCellValue(((Short) e.getValue()).doubleValue());
			} else if (e.getValue() instanceof Integer)
			{
				value.setCellType(CellType.NUMERIC);
				value.setCellValue(((Integer) e.getValue()).doubleValue());
			} else if (e.getValue() instanceof Long)
			{
				value.setCellType(CellType.NUMERIC);
				value.setCellValue(((Long) e.getValue()).doubleValue());
			} else if (e.getValue() instanceof Float)
			{
				value.setCellType(CellType.NUMERIC);
				value.setCellValue(((Float) e.getValue()).doubleValue());
			} else if (e.getValue() instanceof Double)
			{
				value.setCellType(CellType.NUMERIC);
				value.setCellValue(((Double) e.getValue()));
			} else if (e.getValue() instanceof BigInteger)
			{
				value.setCellType(CellType.NUMERIC);
				value.setCellValue(((BigInteger) e.getValue()).doubleValue());
			} else if (e.getValue() instanceof BigDecimal)
			{
				value.setCellType(CellType.NUMERIC);
				value.setCellValue(((BigDecimal) e.getValue()).doubleValue());
			} else if (e.getValue() instanceof Boolean)
			{
				value.setCellType(CellType.BOOLEAN);
				value.setCellValue(((Boolean) e.getValue()));
			} else
				value.setCellValue(new XSSFRichTextString(Converter.toText(e.getValue())));
		}
		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
	}

	private void printGrid(SXSSFWorkbook workbook, Grid<Object> grid) throws ConversionException
	{
		SXSSFSheet sheet = grid.getCaption() != null
				? workbook.createSheet(grid.getCaption().replaceAll("[^a-zA-Z0-9 ]", " "))
				: workbook.createSheet();

		sheet.trackAllColumnsForAutoSizing();
		i = -1;
		if (grid.getColumns().stream().anyMatch(e -> e.getHead() != null))
		{
			short j = -1;
			SXSSFRow row = sheet.createRow(++i);
			for (Column<?> col : grid.getColumns())
			{
				SXSSFCell cell = row.createCell(++j);
				cell.setCellStyle(workbook.createCellStyle());
				cell.getCellStyle().setFont(workbook.createFont());
				cell.getCellStyle().setFillPattern(FillPatternType.SOLID_FOREGROUND);
				((XSSFCellStyle) cell.getCellStyle()).setFillForegroundColor(new XSSFColor(Color.LIGHT_GRAY));
				((XSSFCellStyle) cell.getCellStyle()).getFont().setBold(true);

				switch (col.style().getTextAlign())
				{
					case LEFT:
						cell.getCellStyle().setAlignment(HorizontalAlignment.LEFT);
						break;
					case CENTER:
						cell.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
						break;
					case RIGHT:
						cell.getCellStyle().setAlignment(HorizontalAlignment.RIGHT);
						break;
					case JUSTIFY:
						cell.getCellStyle().setAlignment(HorizontalAlignment.JUSTIFY);
						break;
					default:
						break;
				}

				cell.setCellValue(new XSSFRichTextString(Converter.toText(col.getHead())));
			}
		}

		printGridData(workbook, sheet, grid, Toolkit.iterable(grid.getData()), 0);

		if (grid.getColumns().stream().anyMatch(e -> e.getFoot() != null))
		{
			short j = -1;
			SXSSFRow row = sheet.createRow(++i);
			for (Column<?> col : grid.getColumns())
			{
				SXSSFCell cell = row.createCell(++j);
				cell.setCellStyle(workbook.createCellStyle());
				cell.getCellStyle().setFont(workbook.createFont());
				cell.getCellStyle().setFillPattern(FillPatternType.SOLID_FOREGROUND);
				((XSSFCellStyle) cell.getCellStyle()).setFillForegroundColor(new XSSFColor(Color.LIGHT_GRAY));
				((XSSFCellStyle) cell.getCellStyle()).getFont().setBold(true);

				switch (col.style().getTextAlign())
				{
					case LEFT:
						cell.getCellStyle().setAlignment(HorizontalAlignment.LEFT);
						break;
					case CENTER:
						cell.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
						break;
					case RIGHT:
						cell.getCellStyle().setAlignment(HorizontalAlignment.RIGHT);
						break;
					case JUSTIFY:
						cell.getCellStyle().setAlignment(HorizontalAlignment.JUSTIFY);
						break;
					default:
						break;
				}

				cell.setCellValue(new XSSFRichTextString(Converter.toText(col.getFoot())));
			}
		}

		for (short col = 0; col < grid.getColumns().size(); col++)
			sheet.autoSizeColumn(col);
	}

	private void printGridData(SXSSFWorkbook workbook, SXSSFSheet sheet,
			Grid<Object> grid, Iterable<?> data, int level) throws ConversionException
	{

		for (Object object : data)
		{
			SXSSFRow row = sheet.createRow(++i);

			short j = -1;
			for (Column<Object> col : grid.getColumns())
			{
				SXSSFCell cell = row.createCell(++j);
				col.getStyler().apply(object, col.style());
				cell.setCellStyle(getXLSStyle(workbook, col.style()));

				Object value = col.getBody().apply(object);
				if (value == null)
					cell.setCellType(CellType.BLANK);
				else if (value instanceof Byte)
				{
					cell.setCellType(CellType.NUMERIC);
					cell.setCellValue(((Byte) value).doubleValue());
				} else if (value instanceof Short)
				{
					cell.setCellType(CellType.NUMERIC);
					cell.setCellValue(((Short) value).doubleValue());
				} else if (value instanceof Integer)
				{
					cell.setCellType(CellType.NUMERIC);
					cell.setCellValue(((Integer) value).doubleValue());
				} else if (value instanceof Long)
				{
					cell.setCellType(CellType.NUMERIC);
					cell.setCellValue(((Long) value).doubleValue());
				} else if (value instanceof Float)
				{
					cell.setCellType(CellType.NUMERIC);
					cell.setCellValue(((Float) value).doubleValue());
				} else if (value instanceof Double)
				{
					cell.setCellType(CellType.NUMERIC);
					cell.setCellValue(((Double) value));
				} else if (value instanceof BigInteger)
				{
					cell.setCellType(CellType.NUMERIC);
					cell.setCellValue(((BigInteger) value).doubleValue());
				} else if (value instanceof BigDecimal)
				{
					cell.setCellType(CellType.NUMERIC);
					cell.setCellValue(((BigDecimal) value).doubleValue());
				} else if (value instanceof Boolean)
				{
					cell.setCellType(CellType.BOOLEAN);
					cell.setCellValue(((Boolean) value));
				} else
				{
					StringBuilder text = new StringBuilder(Converter.toText(value));
					if (j == 0)
						for (int d = 0; d < level; d++)
							text.insert(0, "        ");
					cell.setCellValue(new XSSFRichTextString(text.toString()));
				}
			}

			if (grid.getChildren() != null)
				printGridData(workbook, sheet, grid, Toolkit
						.collection(grid.getChildren().apply(object)), level + 1);
		}
	}

	private final Map<Style, XSSFCellStyle> styles = new HashMap();

	private XSSFColor getXLSColor(Style style)
	{
		switch (style.getColor())
		{
			case BLACK:
				return new XSSFColor(Color.black);
			case BLUE:
				return new XSSFColor(Color.blue);
			case RED:
				return new XSSFColor(Color.red);
			case GREEN:
				return new XSSFColor(Color.green);
			default:
				return new XSSFColor(Color.black);
		}
	}

	private HorizontalAlignment getXLSAligment(Style style)
	{
		switch (style.getTextAlign())
		{
			case LEFT:
				return HorizontalAlignment.LEFT;
			case CENTER:
				return HorizontalAlignment.CENTER;
			case RIGHT:
				return HorizontalAlignment.RIGHT;
			case JUSTIFY:
				return HorizontalAlignment.JUSTIFY;
			default:
				return HorizontalAlignment.LEFT;
		}
	}

	private XSSFCellStyle getXLSStyle(SXSSFWorkbook workbook, Style style)
	{
		if (!styles.containsKey(style))
		{
			XSSFCellStyle XSSFCellStyle = (XSSFCellStyle) workbook.createCellStyle();
			XSSFCellStyle.setFont(workbook.createFont());
			XSSFCellStyle.getFont().setColor(getXLSColor(style));
			XSSFCellStyle.getFont().setBold(style.getFontWeight() == Style.FontWeight.BOLD);
			XSSFCellStyle.getFont().setFontHeight(style.getFontSize());
			XSSFCellStyle.setAlignment(getXLSAligment(style));
			styles.put(style, XSSFCellStyle);
		}
		return styles.get(style);
	}
}
