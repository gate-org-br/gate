package gate.report.doc;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import gate.annotation.Icon;
import gate.converter.Converter;
import gate.error.AppError;
import gate.type.Color;
import gate.report.Column;
import gate.report.Doc;
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
import static gate.report.Report.Orientation.LANDSCAPE;
import static gate.report.Report.Orientation.PORTRAIT;
import gate.report.ReportElement;
import gate.report.Style;
import gate.util.Toolkit;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Generates PDF documents from objects of type {@link gate.report.Report}.
 */
@Icon("gate.report.Doc$Type:PDF")
public class PDF extends Doc
{

	private static final BaseColor CAPTION_COLOR = new BaseColor(120, 129, 133);
	private static final BaseColor HEAD_COLOR = new BaseColor(185, 198, 205);

	private static final BaseColor FORM_COLOR = new BaseColor(255, 255, 255);
	private static final BaseColor FIELD_COLOR = new BaseColor(230, 230, 230);

	private static final BaseColor BODY_COLOR1 = new BaseColor(255, 255, 255);
	private static final BaseColor BODY_COLOR2 = new BaseColor(245, 246, 248);

	private static final Map<Color, BaseColor> COLORS = new ConcurrentHashMap<>();
	private static final ConcurrentMap<Style, Font> FONTS = new ConcurrentHashMap<>();
	private static final Font HEAD_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
	private static final Font FIELD_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
	private static final Font FORM_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
	private static final Font CAPTION_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.WHITE);

	/**
	 * Constructs a new PDF Doc for the specified report.
	 *
	 * @param report the report to be used to generate the document
	 */
	public PDF(Report report)
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
		return "pdf";
	}

	@Override
	public String getFileName()
	{
		return String.format("%s.pdf", getReport().getName());
	}

	@Override
	@SuppressWarnings("unchecked")
	public void print(OutputStream os)
	{
		try
		{
			Document document;
			switch (getReport().getOrientation())
			{
				case PORTRAIT:
					document = new Document(PageSize.A4);
					break;
				case LANDSCAPE:
					document = new Document(PageSize.A4.rotate());
					break;
				default:
					throw new IllegalArgumentException("Invalid report orientation");
			}

			PdfWriter.getInstance(document, os).setPageEvent(new Numerator());
			document.open();
			for (ReportElement element : getReport().getElements())
			{
				if (element instanceof Header)
					document.add(printHeader((Header) element));
				else if (element instanceof Paragraph)
					document.add(printParagraph((Paragraph) element));
				else if (element instanceof Footer)
					document.add(printFooter((Footer) element));
				else if (element instanceof LineBreak)
					document.add(printLineBreak());
				else if (element instanceof PageBreak)
					document.add(printPageBreak());
				else if (element instanceof Form
					&& (!((Form) element).getFields().isEmpty()))
					document.add(printForm((Form) element));
				else if (element instanceof Grid)
					document.add(printGrid((Grid) element));
				else if (element instanceof Image && ((Image) element).getSource() != null)
					document.add(printImage((Image) element));
			}
			document.close();
		} catch (DocumentException | AppError ex)
		{
			throw new AppError(ex);
		} catch (RuntimeException e)
		{
			throw e;
		}
	}

	private Element printHeader(Header header) throws AppError
	{
		try
		{
			String string = Converter.toText(header.getValue());
			com.itextpdf.text.Paragraph element = new com.itextpdf.text.Paragraph(string, getFont(header.style()));
			element.setAlignment(getAlignment(header.style()));
			return element;
		} catch (Exception e)
		{
			throw new AppError(e);
		}
	}

	private Element printParagraph(Paragraph paragraph) throws AppError
	{
		try
		{
			String string = Converter.toText(paragraph.getValue());
			com.itextpdf.text.Paragraph element = new com.itextpdf.text.Paragraph(string, getFont(paragraph.style()));
			element.setAlignment(getAlignment(paragraph.style()));
			return element;
		} catch (Exception e)
		{
			throw new AppError(e);
		}
	}

	private Element printFooter(Footer footer) throws AppError
	{
		try
		{
			String string = Converter.toText(footer.getValue());
			com.itextpdf.text.Paragraph element = new com.itextpdf.text.Paragraph(string, getFont(footer.style()));
			element.setAlignment(getAlignment(footer.style()));
			return element;
		} catch (Exception e)
		{
			throw new AppError(e);
		}
	}

	private Element printPageBreak() throws AppError
	{
		return Chunk.NEXTPAGE;
	}

	private Element printLineBreak() throws AppError
	{
		return new Phrase("\n");
	}

	private Element printImage(Image image) throws AppError
	{
		try
		{
			com.itextpdf.text.Image element;
			element = com.itextpdf.text.Image.getInstance((byte[]) image.getSource());
			element.setAlignment(getAlignment(image.style()));
			return element;
		} catch (BadElementException | IOException e)
		{
			throw new AppError(e);
		}
	}

	private PdfPCell printField(Field field) throws AppError
	{
		try
		{
			PdfPTable table = new PdfPTable(1);
			table.setWidths(new float[]
			{
				1f
			});

			PdfPCell label = new PdfPCell(new com.itextpdf.text.Paragraph(field.getName(), FIELD_FONT));
			label.setBorder(0);
			label.setPadding(0);
			label.setPaddingBottom(2);
			label.setBackgroundColor(FORM_COLOR);
			table.addCell(label);

			PdfPCell value = new PdfPCell(new com.itextpdf.text.Paragraph(Converter.toText(field.getValue()), FIELD_FONT));
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

	private Element printForm(Form form) throws AppError
	{
		try
		{
			PdfPTable element = new PdfPTable(1);
			element.setWidthPercentage(form.getPercentage());

			if (form.getCaption() != null)
			{
				PdfPCell caption = new PdfPCell(new com.itextpdf.text.Paragraph(form.getCaption(), FORM_FONT));
				caption.setBorder(0);
				caption.setMinimumHeight(20);
				caption.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
				caption.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
				element.addCell(caption);
			}

			float[] widths = new float[form.getColumns()];
			for (int i = 0; i < form.getColumns(); i++)
				widths[i] = 100 / form.getColumns();
			PdfPTable table = new PdfPTable(form.getColumns());
			table.setWidths(widths);

			PdfPCell body = new PdfPCell(table);
			body.setPadding(4);
			body.setBackgroundColor(FORM_COLOR);
			body.setBorderColor(BaseColor.LIGHT_GRAY);

			form.getFields().forEach(e -> table.addCell(printField(e)));

			element.addCell(body);

			return element;
		} catch (DocumentException ex)
		{
			throw new AppError(ex);
		}
	}

	private int getAlignment(Style style)
	{
		switch (style.getTextAlign())
		{
			case CENTER:
				return PdfPCell.ALIGN_CENTER;
			case JUSTIFY:
				return PdfPCell.ALIGN_JUSTIFIED;
			case LEFT:
				return PdfPCell.ALIGN_LEFT;
			case RIGHT:
				return PdfPCell.ALIGN_RIGHT;
			default:
				return PdfPCell.ALIGN_CENTER;
		}
	}

	private PdfPCell createHeadCell(String value, Style style) throws AppError
	{
		try
		{
			PdfPCell cell = new PdfPCell(new com.itextpdf.text.Paragraph(value, HEAD_FONT));
			cell.setMinimumHeight(16);
			cell.setBorderColor(BaseColor.GRAY);
			cell.setBackgroundColor(HEAD_COLOR);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(getAlignment(style));

			return cell;
		} catch (Exception e)
		{
			throw new AppError(e);
		}
	}

	private PdfPCell createBodyCell(Object value, Style style, int index, int level) throws AppError
	{
		try
		{

			String string = Converter.toText(value);

			PdfPCell cell = new PdfPCell(new com.itextpdf.text.Paragraph(string, getFont(style)));
			cell.setMinimumHeight(16);
			cell.setPaddingLeft(level * 50);
			cell.setBorderColor(BaseColor.GRAY);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(getAlignment(style));
			cell.setBackgroundColor(index % 2 == 0 ? BODY_COLOR1 : BODY_COLOR2);

			return cell;
		} catch (IllegalArgumentException e)
		{
			throw new AppError(e);
		}
	}

	private PdfPCell createFootCell(String value, Style style) throws AppError
	{
		try
		{
			PdfPCell cell = new PdfPCell(new com.itextpdf.text.Paragraph(value, getFont(style)));
			cell.setMinimumHeight(20);
			cell.setBorderColor(BaseColor.GRAY);
			cell.setBackgroundColor(BaseColor.GRAY);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(getAlignment(style));

			return cell;
		} catch (Exception e)
		{
			throw new AppError(e);
		}
	}

	private void addBodies(Grid<Object> grid, PdfPTable table, Object data, int level)
	{

		int size = grid.getLimit() != null
			? Math.min(grid.getLimit(), grid.getColumns().size())
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
				Toolkit.collection(grid.getChildren().apply(object))
					.forEach(e -> addBodies(grid, table, e, level + 1));
		}
	}

	private PdfPTable printGrid(Grid<Object> grid) throws AppError
	{
		try
		{
			int size = grid.getLimit() != null
				? Math.min(grid.getLimit(), grid.getColumns().size())
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
				table.getDefaultCell().setBorderColor(BaseColor.GRAY);
				table.getDefaultCell().setColspan(widths.length);
				table.getDefaultCell().setBackgroundColor(CAPTION_COLOR);
				table.getDefaultCell().setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
				table.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				table.addCell(new com.itextpdf.text.Paragraph(grid.getCaption(), CAPTION_FONT));
			}

			if (grid.getColumns().stream().limit(size).anyMatch(e -> e.getHead() != null))
			{
				grid.getColumns().stream().limit(size)
					.forEach(e -> table.addCell(createHeadCell(Converter.toText(e.getHead()), e.style())));
				table.setHeaderRows(table.getHeaderRows() + 1);
			}

			if (grid.getColumns().stream().limit(size).anyMatch(e -> e.getFoot() != null))
			{
				grid.getColumns().stream().limit(size)
					.forEach(e -> table.addCell(createFootCell(Converter.toText(e.getFoot()), e.style())));
				table.setFooterRows(1);
				table.setHeaderRows(table.getHeaderRows() + 1);
			}

			addBodies(grid, table, grid.getData(), 0);

			return table;
		} catch (DocumentException ex)
		{
			throw new AppError(ex);
		}
	}

	private class Numerator extends PdfPageEventHelper
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
		public void onCloseDocument(PdfWriter writer, com.itextpdf.text.Document document)
		{
			pages.beginText();
			pages.setFontAndSize(baseFont, FOOTER_SIZE);
			pages.setTextMatrix(0, 0);
			pages.showText(String.valueOf(writer.getPageNumber()));
			pages.endText();
		}
	}

	private BaseColor getColor(Style style)
	{
		return COLORS.computeIfAbsent(style.getColor(),
			c -> new BaseColor(c.getR(), c.getG(), c.getB()));
	}

	private int getFontWeight(Style style)
	{
		switch (style.getFontWeight())
		{
			case NORMAL:
				return Font.NORMAL;
			case BOLD:
				return Font.BOLD;
			default:
				return Font.NORMAL;
		}
	}

	private Font getFont(Style style)
	{
		return FONTS.computeIfAbsent(style, e
			-> new Font(Font.FontFamily.TIMES_ROMAN, e.getFontSize(),
				getFontWeight(e), getColor(e)));
	}
}
