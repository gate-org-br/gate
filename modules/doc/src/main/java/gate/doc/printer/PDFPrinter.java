package gate.doc.printer;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.ListItem;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGraphics2D;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import gate.adapter.renderer.Renderer;
import gate.error.AppError;
import gate.report.Chart;
import gate.report.ChartGenerator;
import gate.report.Column;
import gate.report.Dictionary;
import gate.report.Field;
import gate.report.Footer;
import gate.report.Form;
import gate.report.Grid;
import gate.report.Header;
import gate.report.Image;
import gate.report.LineBreak;
import gate.report.PageBreak;
import gate.report.Paragraph;
import gate.report.Report;
import gate.report.ReportElement;
import gate.report.ReportList;
import gate.report.Style;
import gate.util.Toolkit;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Generates PDF documents from objects of type {@link gate.report.Report}.
 */
public class PDFPrinter implements Printer
{

	private Document document;
	private PdfWriter writer;

	private static final Color CAPTION_COLOR = new Color(120, 129, 133);
	private static final Color HEAD_COLOR = new Color(185, 198, 205);

	private static final Color FORM_COLOR = new Color(255, 255, 255);
	private static final Color FIELD_COLOR = new Color(230, 230, 230);

	private static final Color BODY_COLOR1 = new Color(255, 255, 255);
	private static final Color BODY_COLOR2 = new Color(245, 246, 248);

	private static final Font HEAD_FONT = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
	private static final Font FIELD_FONT = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);
	private static final Font FORM_FONT = new Font(Font.TIMES_ROMAN, 10, Font.BOLD, Color.BLACK);
	private static final Font CAPTION_FONT = new Font(Font.TIMES_ROMAN, 10, Font.BOLD, Color.WHITE);

	private static final Map<gate.type.Color, Color> COLORS = new ConcurrentHashMap<>();
	private static final ConcurrentMap<Style, Font> FONTS = new ConcurrentHashMap<>();

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void print(OutputStream os, Report report)
	{
		try
		{
			Document document = switch (report.getOrientation())
			{
				case PORTRAIT -> new Document(PageSize.A4);
				case LANDSCAPE -> new Document(PageSize.A4.rotate());
			};

			var writer = PdfWriter.getInstance(document, os);
			writer.setPageEvent(new Numerator());
			document.open();

			this.document = document;
			this.writer = writer;

			for (ReportElement element : report.getElements())
				if (element instanceof Header header)
					printHeader(header);
				else if (element instanceof Paragraph paragraph)
					printParagraph(paragraph);
				else if (element instanceof Footer footer)
					printFooter(footer);
				else if (element instanceof LineBreak lineBreak)
					printLineBreak(lineBreak);
				else if (element instanceof PageBreak pageBreak)
					printPageBreak(pageBreak);
				else if (element instanceof Form form && !form.getFields().isEmpty())
					printForm(form);
				else if (element instanceof Grid grid)
					printGrid(grid);
				else if (element instanceof ReportList reportList)
					printList(reportList);
				else if (element instanceof Image image && image.getSource() != null)
					printImage(image);
				else if (element instanceof Chart chart)
					printChart(chart);
				else if (element instanceof Dictionary dictionary)
					printDictionary(dictionary);

			document.close();
		} catch (DocumentException ex)
		{
			throw new AppError(ex);
		} finally
		{
			this.document = null;
			this.writer = null;
		}
	}

	private void printHeader(Header header)
	{
		String string = Renderer.render(header.getValue());
		com.lowagie.text.Paragraph element = new com.lowagie.text.Paragraph(string, getFont(header.style()));
		element.setAlignment(getAlignment(header.style()));
		add(element);
	}

	private void printParagraph(Paragraph paragraph)
	{
		String string = Renderer.render(paragraph.getValue());
		com.lowagie.text.Paragraph element = new com.lowagie.text.Paragraph(string, getFont(paragraph.style()));
		element.setAlignment(getAlignment(paragraph.style()));
		add(element);
	}

	private void printFooter(Footer footer)
	{
		String string = Renderer.render(footer.getValue());
		com.lowagie.text.Paragraph element = new com.lowagie.text.Paragraph(string, getFont(footer.style()));
		element.setAlignment(getAlignment(footer.style()));
		add(element);
	}

	private void printLineBreak(LineBreak lineBreak)
	{
		add(new Phrase("\n"));
	}

	private void printPageBreak(PageBreak pageBreak)
	{
		add(Chunk.NEXTPAGE);
	}

	private void printImage(Image image)
	{
		try
		{
			com.lowagie.text.Image element = com.lowagie.text.Image.getInstance(image.getSource());
			element.setAlignment(getAlignment(image.style()));
			add(element);
		} catch (BadElementException | IOException e)
		{
			throw new AppError(e);
		}
	}

	private void printChart(Chart<?> chart)
	{
		float width = document.getPageSize().getWidth() - 40;
		float height = (document.getPageSize().getHeight() - 40) / 2;

		PdfContentByte cb = writer.getDirectContent();
		PdfTemplate template = cb.createTemplate(width, height);
		Graphics2D g2d = new PdfGraphics2D(template, width, height);
		Rectangle2D r2d = new Rectangle2D.Double(0, 0, width, height);
		ChartGenerator.create(chart).draw(g2d, r2d);
		g2d.dispose();

		var image = com.lowagie.text.Image.getInstance(template);
		image.setAlignment(PdfPCell.ALIGN_CENTER);
		add(image);
	}

	private void printForm(Form form)
	{
		try
		{
			PdfPTable element = new PdfPTable(1);
			element.setWidthPercentage(form.getPercentage());

			if (form.getCaption() != null)
			{
				PdfPCell caption = new PdfPCell(new com.lowagie.text.Paragraph(form.getCaption(), FORM_FONT));
				caption.setBorder(0);
				caption.setMinimumHeight(20);
				caption.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
				caption.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
				element.addCell(caption);
			}

			float[] widths = new float[form.getColumns()];
			for (int i = 0; i < form.getColumns(); i++)
				widths[i] = (float) 100 / form.getColumns();
			PdfPTable table = new PdfPTable(form.getColumns());
			table.setWidths(widths);

			PdfPCell body = new PdfPCell(table);
			body.setPadding(4);
			body.setBackgroundColor(FORM_COLOR);
			body.setBorderColor(Color.LIGHT_GRAY);

			form.getFields().forEach(e -> table.addCell(printField(e)));

			element.addCell(body);
			add(element);
		} catch (DocumentException ex)
		{
			throw new AppError(ex);
		}
	}

	private PdfPCell printField(Field field)
	{
		try
		{
			PdfPTable table = new PdfPTable(1);
			table.setWidths(new float[]
					{1f});

			PdfPCell label = new PdfPCell(new com.lowagie.text.Paragraph(field.getName(), FIELD_FONT));
			label.setBorder(0);
			label.setPadding(0);
			label.setPaddingBottom(2);
			label.setBackgroundColor(FORM_COLOR);
			table.addCell(label);

			PdfPCell value = new PdfPCell(
					new com.lowagie.text.Paragraph(Renderer.render(field.getValue()), FIELD_FONT));
			value.setPadding(2);
			value.setMinimumHeight(field.getHeight());
			value.setBorder(0);
			value.setBackgroundColor(FIELD_COLOR);
			value.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			table.addCell(value);

			PdfPCell cell = new PdfPCell(table);
			cell.setBorder(0);
			cell.setPadding(3);
			cell.setColspan(field.getColspan());
			return cell;
		} catch (DocumentException e)
		{
			throw new AppError(e);
		}
	}

	private void printGrid(Grid<Object> grid)
	{
		try
		{
			int size = grid.getLimit() != null ? Math.min(grid.getLimit(), grid.getColumns().size())
					: grid.getColumns().size();

			float[] widths = new float[size];
			for (int i = 0; i < size; i++)
				widths[i] = (float) grid.getColumns().get(i).style().getWidth();

			PdfPTable table = new PdfPTable(widths.length);
			table.setWidths(widths);
			table.setWidthPercentage(100);

			if (grid.getCaption() != null)
			{
				table.setHeaderRows(table.getHeaderRows() + 1);
				table.getDefaultCell().setMinimumHeight(16);
				table.getDefaultCell().setBorderColor(Color.GRAY);
				table.getDefaultCell().setColspan(widths.length);
				table.getDefaultCell().setBackgroundColor(CAPTION_COLOR);
				table.getDefaultCell().setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
				table.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				table.addCell(new com.lowagie.text.Paragraph(grid.getCaption(), CAPTION_FONT));
			}

			if (grid.getColumns().stream().limit(size).anyMatch(e -> e.getHead() != null))
			{
				grid.getColumns().stream().limit(size)
						.forEach(e -> table.addCell(createHeadCell(Renderer.render(e.getHead()), e.style())));
				table.setHeaderRows(table.getHeaderRows() + 1);
			}

			if (grid.getColumns().stream().limit(size).anyMatch(e -> e.getFoot() != null))
			{
				grid.getColumns().stream().limit(size)
						.forEach(e -> table.addCell(createFootCell(Renderer.render(e.getFoot()), e.style())));
				table.setFooterRows(1);
				table.setHeaderRows(table.getHeaderRows() + 1);
			}

			addBodies(grid, table, grid.getData(), 0);
			add(table);
		} catch (DocumentException ex)
		{
			throw new AppError(ex);
		}
	}

	private PdfPCell createHeadCell(String value, Style style)
	{
		PdfPCell cell = new PdfPCell(new com.lowagie.text.Paragraph(value, HEAD_FONT));
		cell.setMinimumHeight(16);
		cell.setBorderColor(Color.GRAY);
		cell.setBackgroundColor(HEAD_COLOR);
		cell.setVerticalAlignment(com.lowagie.text.Element.ALIGN_MIDDLE);
		cell.setHorizontalAlignment(getAlignment(style));

		return cell;
	}

	private PdfPCell createBodyCell(Object value, Style style, int index, int level)
	{
		String string = Renderer.render(value);

		PdfPCell cell = new PdfPCell(new com.lowagie.text.Paragraph(string, getFont(style)));
		cell.setMinimumHeight(16);
		cell.setPaddingLeft(level * 50);
		cell.setBorderColor(Color.GRAY);
		cell.setVerticalAlignment(com.lowagie.text.Element.ALIGN_MIDDLE);
		cell.setHorizontalAlignment(getAlignment(style));
		cell.setBackgroundColor(index % 2 == 0 ? BODY_COLOR1 : BODY_COLOR2);

		return cell;
	}

	private PdfPCell createFootCell(String value, Style style)
	{
		PdfPCell cell = new PdfPCell(new com.lowagie.text.Paragraph(value, getFont(style)));
		cell.setMinimumHeight(20);
		cell.setBorderColor(Color.GRAY);
		cell.setBackgroundColor(Color.GRAY.brighter());
		cell.setVerticalAlignment(com.lowagie.text.Element.ALIGN_MIDDLE);
		cell.setHorizontalAlignment(getAlignment(style));

		return cell;
	}

	private void addBodies(Grid<Object> grid, PdfPTable table, Object data, int level)
	{

		int size = grid.getLimit() != null ? Math.min(grid.getLimit(), grid.getColumns().size())
				: grid.getColumns().size();
		for (Object object : Toolkit.iterable(data))
		{
			int index = table.getRows().size() - 1;

			for (int i = 0; i < size; i++)
			{
				Column<Object> column = grid.getColumns().get(i);
				Object value = column.getBody().apply(object);

				Style style = column.computeStyle(object, value);

				PdfPCell cell = createBodyCell(value, style, index, i == 0 ? level : 0);
				table.addCell(cell);
			}

			if (grid.getChildren() != null)
				Toolkit.collection(grid.getChildren().apply(object)).forEach(e -> addBodies(grid, table, e, level + 1));
		}
	}

	private void printList(ReportList reportList)
	{
		add(createList(reportList));
	}

	private com.lowagie.text.List createList(ReportList reportList)
	{
		com.lowagie.text.List list = new com.lowagie.text.List();

		Font font = getFont(reportList.style());
		switch (reportList.style().getListStyleType())
		{
			case DECIMAL ->
			{
				list.setNumbered(true);
				list.setLettered(false);
				list.setListSymbol(new Chunk("", font));
			}

			case LOWER_ALPHA ->
			{
				list.setNumbered(false);
				list.setLettered(true);
				list.setListSymbol(new Chunk("", font));
			}
			case DISC ->
			{
				list.setNumbered(false);
				list.setLettered(false);
				list.setListSymbol(new Chunk("\u2022 ", font));
			}
			case NONE ->
			{
				list.setNumbered(false);
				list.setLettered(false);
				list.setListSymbol(new Chunk("", font));
			}
		}

		reportList.getElements().forEach(e ->
		{
			if (e instanceof String string)
				list.add(new ListItem(string, font));
			else if (e instanceof ReportList reportList1)
			{
				ListItem item = new ListItem();
				item.add(createList(reportList1));
				list.add(item);
			}
		});

		return list;
	}

	private void printDictionary(Dictionary dictionary)
	{
		try
		{

			PdfPTable table = new PdfPTable(2);
			table.setWidths(new float[]
					{0.5f, 0.5f});
			table.setWidthPercentage(100);

			Color lightGray = new Color(250, 250, 250);
			Font valueFont = getFont(dictionary.style());
			Font propertyFont = new Font(valueFont.getFamily(), valueFont.getSize(), Font.BOLD);

			if (dictionary.getCaption() != null)
			{
				table.getDefaultCell().setColspan(2);
				table.setHeaderRows(table.getHeaderRows() + 1);
				table.getDefaultCell().setMinimumHeight(16);
				table.getDefaultCell().setBorderColor(Color.GRAY);
				table.getDefaultCell().setBackgroundColor(CAPTION_COLOR);
				table.getDefaultCell().setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
				table.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				table.addCell(new com.lowagie.text.Paragraph(dictionary.getCaption(), CAPTION_FONT));
			}

			for (var entry : dictionary.getElements().entrySet())
			{
				PdfPCell propertyCell = new PdfPCell(new Phrase(entry.getKey(), propertyFont));
				propertyCell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
				propertyCell.setBorder(Rectangle.NO_BORDER);
				propertyCell.setPaddingRight(10f);
				propertyCell.setBackgroundColor(lightGray);

				PdfPCell valueCell = new PdfPCell(new Phrase(Renderer.render(entry.getValue()), valueFont));
				valueCell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_LEFT);
				valueCell.setBorder(Rectangle.NO_BORDER);
				valueCell.setBackgroundColor(lightGray);

				table.addCell(propertyCell);
				table.addCell(valueCell);
			}

			add(table);
		} catch (DocumentException ex)
		{
			throw new AppError(ex);
		}
	}

	private void add(com.lowagie.text.Element element)
	{
		try
		{
			document.add(element);
		} catch (DocumentException ex)
		{
			throw new AppError(ex);
		}
	}

	private int getAlignment(Style style)
	{
		return switch (style.getTextAlign())
		{
			case JUSTIFY -> PdfPCell.ALIGN_JUSTIFIED;
			case LEFT -> PdfPCell.ALIGN_LEFT;
			case RIGHT -> PdfPCell.ALIGN_RIGHT;
			default -> PdfPCell.ALIGN_CENTER;
		};
	}

	private Font getFont(Style style)
	{
		return FONTS.computeIfAbsent(style,
				e -> new Font(Font.TIMES_ROMAN, e.getFontSize(), getFontWeight(e), getColor(e)));
	}

	private Color getColor(Style style)
	{
		return COLORS.computeIfAbsent(style.getColor(), c -> new Color(c.getR(), c.getG(), c.getB()));
	}

	private int getFontWeight(Style style)
	{
		return style.getFontWeight() == Style.FontWeight.BOLD ? Font.BOLD : Font.NORMAL;
	}

	private static class Numerator extends PdfPageEventHelper
	{

		private PdfTemplate pages;
		protected BaseFont baseFont;
		private static final float FOOTER_SIZE = 8f;

		public Numerator()
		{
		}

		@Override
		public void onOpenDocument(PdfWriter writer, Document document)
		{
			try
			{
				baseFont = BaseFont.createFont();
				pages = writer.getDirectContent().createTemplate(100, 100);
				pages.setBoundingBox(new Rectangle(-20, -20, 100, 100));
			} catch (DocumentException | IOException e)
			{
				throw new AppError(e);
			}
		}

		@Override
		public void onEndPage(PdfWriter writer, Document document)
		{
			PdfContentByte cb = writer.getDirectContent();
			cb.saveState();
			String text = String.format("Página %s de ", writer.getPageNumber());

			float textBase = document.bottom() - 20;
			float textSize = baseFont.getWidthPoint(text, FOOTER_SIZE);

			cb.beginText();
			cb.setFontAndSize(baseFont, FOOTER_SIZE);
			cb.setTextMatrix((document.right() / 2), textBase);
			cb.showText(text);
			cb.endText();
			cb.addTemplate(pages, (document.right() / 2) + textSize, textBase);
			cb.restoreState();
		}

		@Override
		public void onCloseDocument(PdfWriter writer, Document document)
		{
			pages.beginText();
			pages.setFontAndSize(baseFont, FOOTER_SIZE);
			pages.setTextMatrix(0, 0);
			pages.showText(String.valueOf(writer.getPageNumber() - 1));
			pages.endText();
		}
	}
}