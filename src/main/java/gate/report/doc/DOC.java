package gate.report.doc;

import gate.annotation.Icon;
import gate.converter.Converter;
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
import gate.report.ReportElement;
import gate.report.Style;
import gate.report.Style.TextAlign;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import javax.imageio.ImageIO;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;

/**
 * Generates XLS documents from objects of type {@link gate.report.Report}.
 */
@Icon("gate.report.Doc$Type:DOC")
public class DOC extends Doc
{

	private static final String LABEL_BACKGROUND_COLOR = "DDDDDD";
	private static final String CAPTION_BACKGROUND_COLOR = "666666";

	/**
	 * Constructs a new DOC Doc for the specified report.
	 *
	 * @param report the report to be used to generate the document
	 */
	public DOC(Report report)
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
		return "doc";
	}

	@Override
	public String getFileName()
	{
		return String.format("%s.docx", getReport().getName());
	}

	@Override
	public void print(OutputStream os)
	{
		try ( XWPFDocument XWPFDocument = new XWPFDocument())
		{
			CTSectPr section = XWPFDocument.getDocument().getBody().addNewSectPr();
			XWPFHeaderFooterPolicy XWPFHeaderFooterPolicy = new XWPFHeaderFooterPolicy(XWPFDocument, section);
			XWPFHeader XWPFHeader = XWPFHeaderFooterPolicy.createHeader(org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy.DEFAULT);
			XWPFFooter XWPFFooter = XWPFHeaderFooterPolicy.createFooter(org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy.DEFAULT);

			for (ReportElement e : getReport().getElements())
			{
				if (e instanceof Header)
					printHeader(XWPFHeader, (Header) e);
				else if (e instanceof Footer)
					printFooter(XWPFFooter, (Footer) e);
				else if (e instanceof Paragraph)
					printParagraph(XWPFDocument, (Paragraph) e);
				else if (e instanceof Grid)
					printGrid(XWPFDocument, (Grid<Object>) e);
				else if (e instanceof LineBreak)
					printLineBreak(XWPFDocument);
				else if (e instanceof PageBreak)
					printPageBreak(XWPFDocument);
				else if (e instanceof Form)
					printForm(XWPFDocument, (Form) e);
				else if (e instanceof Image)
					printImage(XWPFHeader, (Image) e);
			}
			XWPFDocument.write(os);
		} catch (IOException e)
		{
			throw new UncheckedIOException(e);
		} catch (InvalidFormatException ex)
		{
			throw new UncheckedIOException(new IOException(ex));
		}
	}

	private void printForm(XWPFDocument XWPFDocument, Form form)
	{
		int index = 0;
		XWPFTable XWPFTable = XWPFDocument.createTable();

		if (form.getCaption() != null)
		{
			printCaption(XWPFTable, form.getCaption(), 2);
			index++;
		}

		for (Field field : form.getFields())
			printField(getXWPFTableRow(XWPFTable, index++), field);

	}

	private void printField(XWPFTableRow XWPFTableRow, Field field)
	{
		printFieldLabel(getXWPFTableCell(XWPFTableRow, 0), field);
		printFieldValue(getXWPFTableCell(XWPFTableRow, 1), field);
	}

	private void printFieldLabel(XWPFTableCell XWPFTableCell, Field field)
	{
		XWPFTableCell.getCTTc().addNewTcPr().addNewShd().setFill(LABEL_BACKGROUND_COLOR);

		XWPFParagraph XWPFParagraph = XWPFTableCell.getParagraphs().get(0);

		XWPFParagraph.setVerticalAlignment(TextAlignment.CENTER);

		XWPFParagraph.setAlignment(ParagraphAlignment.LEFT);

		XWPFRun XWPFRun = XWPFParagraph.createRun();
		XWPFRun.setBold(true);
		XWPFRun.setText(Converter.toText(field.getName()));
	}

	private void printFieldValue(XWPFTableCell XWPFTableCell, Field field)
	{
		XWPFParagraph XWPFParagraph = XWPFTableCell.getParagraphs().get(0);

		XWPFParagraph.setVerticalAlignment(TextAlignment.CENTER);

		XWPFParagraph.setAlignment(ParagraphAlignment.LEFT);

		XWPFRun XWPFRun = XWPFParagraph.createRun();
		XWPFRun.setText(Converter.toText(field.getValue()));
	}

	private void printHeader(XWPFHeader XWPFHeader, Header header)
	{
		XWPFParagraph XWPFParagraph = XWPFHeader.createParagraph();
		XWPFParagraph.setAlignment(getParagraphAlignment(header.style().getTextAlign()));

		XWPFRun XWPFRun = XWPFParagraph.createRun();
		XWPFRun.setBold(header.style().getFontWeight() == Style.FontWeight.BOLD);
		XWPFRun.setFontSize(header.style().getFontSize());
		XWPFRun.setColor(header.style().getColor().toString().substring(1));
		XWPFRun.setText(Converter.toText(header.getValue()));
	}

	private void printFooter(XWPFFooter XWPFFooter, Footer footer)
	{

		XWPFParagraph XWPFParagraph = XWPFFooter.createParagraph();
		XWPFParagraph.setAlignment(getParagraphAlignment(footer.style().getTextAlign()));

		XWPFRun XWPFRun = XWPFParagraph.createRun();
		XWPFRun.setBold(footer.style().getFontWeight() == Style.FontWeight.BOLD);
		XWPFRun.setFontSize(footer.style().getFontSize());
		XWPFRun.setColor(footer.style().getColor().toString().substring(1));
		XWPFRun.setText(Converter.toText(footer.getValue()));
	}

	private void printParagraph(XWPFDocument XWPFDocument, Paragraph paragraph)
	{
		XWPFParagraph XWPFParagraph = XWPFDocument.createParagraph();
		XWPFParagraph.setAlignment(getParagraphAlignment(paragraph.style().getTextAlign()));

		XWPFRun XWPFRun = XWPFParagraph.createRun();
		XWPFRun.setBold(paragraph.style().getFontWeight() == Style.FontWeight.BOLD);
		XWPFRun.setFontSize(paragraph.style().getFontSize());
		XWPFRun.setColor(paragraph.style().getColor().toString().substring(1));
		XWPFRun.setText(Converter.toText(paragraph.getValue()));
	}

	private void printGrid(XWPFDocument XWPFDocument, Grid<Object> grid)
	{
		int index = 0;
		XWPFTable XWPFTable = XWPFDocument.createTable();
		if (grid.getCaption() != null)
		{
			printCaption(XWPFTable, grid.getCaption(),
				grid.getColumns().size());
			index++;
		}

		if (grid.getColumns().stream().anyMatch(e -> e.getHead() != null))
			printHeader(getXWPFTableRow(XWPFTable, index++), grid);

		for (Object obj : grid.getData())
			printRow(getXWPFTableRow(XWPFTable, index++), grid, obj);
	}

	private XWPFTableRow getXWPFTableRow(XWPFTable XWPFTable, int index)
	{
		return XWPFTable.getRow(index) != null ? XWPFTable.getRow(index) : XWPFTable.createRow();
	}

	private XWPFTableCell getXWPFTableCell(XWPFTableRow XWPFTableRow, int index)
	{
		return XWPFTableRow.getCell(index) != null ? XWPFTableRow.getCell(index) : XWPFTableRow.addNewTableCell();
	}

	private CTTcPr getCTTcPr(XWPFTableCell XWPFTableCell)
	{
		return XWPFTableCell.getCTTc().getTcPr() != null ? XWPFTableCell.getCTTc().getTcPr() : XWPFTableCell.getCTTc().addNewTcPr();
	}

	private void printCaption(XWPFTable XWPFTable, String caption, long span)
	{
		XWPFTableRow XWPFTableRow = getXWPFTableRow(XWPFTable, 0);
		XWPFTableCell XWPFTableCell = getXWPFTableCell(XWPFTableRow, 0);
		XWPFParagraph XWPFParagraph = XWPFTableCell.getParagraphs().get(0);

		XWPFParagraph.setVerticalAlignment(TextAlignment.CENTER);
		XWPFParagraph.setAlignment(ParagraphAlignment.CENTER);
		XWPFRun XWPFRun = XWPFParagraph.createRun();
		XWPFRun.setBold(true);
		XWPFRun.setColor("FFFFFF");
		XWPFRun.setText(Converter.toText(caption));

		CTTcPr CTTcPr = getCTTcPr(XWPFTableCell);
		CTTcPr.addNewShd().setFill(CAPTION_BACKGROUND_COLOR);
		CTTcPr.addNewHMerge().setVal(STMerge.RESTART);

		for (int i = 1; i < span; i++)
			getCTTcPr(getXWPFTableCell(XWPFTableRow, i)).addNewHMerge().setVal(STMerge.CONTINUE);
	}

	private void printImage(XWPFHeader XWPFHeader, Image image) throws IOException, InvalidFormatException
	{
		try ( ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(image.getSource()))
		{
			BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
			XWPFParagraph XWPFParagraph = XWPFHeader.createParagraph();
			XWPFParagraph.setVerticalAlignment(TextAlignment.CENTER);
			XWPFParagraph.setAlignment(ParagraphAlignment.CENTER);
			XWPFRun XWPFRun = XWPFParagraph.createRun();

			byteArrayInputStream.reset();
			XWPFRun.addPicture(byteArrayInputStream, getImageFormat(image.getFilename()), image.getFilename(),
				Units.toEMU(bufferedImage.getWidth()), Units.toEMU(bufferedImage.getHeight()));
		}
	}

	private void printLineBreak(XWPFDocument XWPFDocument)
	{
		XWPFDocument.createParagraph().setSpacingBetween(2);
	}

	private void printPageBreak(XWPFDocument XWPFDocument)
	{
		XWPFDocument.createParagraph().setPageBreak(true);
	}

	private void printHeader(XWPFTableRow XWPFTableRow, Grid<Object> grid)
	{
		for (int i = 0; i < grid.getColumns().size(); i++)
			printHeaderCell(getXWPFTableCell(XWPFTableRow, i),
				grid.getColumns().get(i), grid.getColumns().get(i).getHead());
	}

	private void printHeaderCell(XWPFTableCell XWPFTableCell, Column<?> column, Object value)
	{
		getCTTcPr(XWPFTableCell).addNewShd().setFill(LABEL_BACKGROUND_COLOR);
		if (XWPFTableCell.getParagraphs().isEmpty())
			XWPFTableCell.addParagraph();
		XWPFParagraph XWPFParagraph = XWPFTableCell.getParagraphs().get(0);
		XWPFParagraph.setVerticalAlignment(TextAlignment.CENTER);
		XWPFParagraph.setAlignment(getParagraphAlignment(column.style().getTextAlign()));

		XWPFRun XWPFRun = XWPFParagraph.createRun();
		XWPFRun.setBold(true);
		XWPFRun.setText(Converter.toText(value));
	}

	private void printRow(XWPFTableRow XWPFTableRow, Grid<Object> grid, Object value)
	{
		for (int i = 0; i < grid.getColumns().size(); i++)
		{
			Column<Object> column = grid.getColumns().get(i);
			printCell(getXWPFTableCell(XWPFTableRow, i),
				column.getBody().apply(value),
				column.getStyler().apply(value, column.style()));
		}

	}

	private void printCell(XWPFTableCell XWPFTableCell, Object value, Style style)
	{
		if (XWPFTableCell.getParagraphs().isEmpty())
			XWPFTableCell.addParagraph();

		XWPFParagraph XWPFParagraph = XWPFTableCell.getParagraphs().get(0);

		XWPFParagraph.setVerticalAlignment(TextAlignment.CENTER);

		XWPFParagraph.setAlignment(getParagraphAlignment(style.getTextAlign()));

		XWPFRun XWPFRun = XWPFParagraph.createRun();
		XWPFRun.setBold(style.getFontWeight() == Style.FontWeight.BOLD);
		XWPFRun.setFontSize(style.getFontSize());
		XWPFRun.setColor(style.getColor().toString().substring(1));
		XWPFRun.setText(Converter.toText(value));
	}

	public ParagraphAlignment getParagraphAlignment(TextAlign align)
	{
		switch (align)
		{
			case LEFT:
				return ParagraphAlignment.LEFT;
			case CENTER:
				return ParagraphAlignment.CENTER;
			case RIGHT:
				return ParagraphAlignment.RIGHT;
			case JUSTIFY:
				return ParagraphAlignment.DISTRIBUTE;
		}
		return ParagraphAlignment.LEFT;
	}

	private static int getImageFormat(String fileName)
	{
		fileName = fileName.toLowerCase();
		if (fileName.endsWith(".emf"))
			return XWPFDocument.PICTURE_TYPE_EMF;
		else if (fileName.endsWith(".wmf"))
			return XWPFDocument.PICTURE_TYPE_WMF;
		else if (fileName.endsWith(".pict"))
			return XWPFDocument.PICTURE_TYPE_PICT;
		else if (fileName.endsWith(".jpeg"))
			return XWPFDocument.PICTURE_TYPE_JPEG;
		else if (fileName.endsWith(".jpg"))
			return XWPFDocument.PICTURE_TYPE_JPEG;
		else if (fileName.endsWith(".png"))
			return XWPFDocument.PICTURE_TYPE_PNG;
		else if (fileName.endsWith(".dib"))
			return XWPFDocument.PICTURE_TYPE_DIB;
		else if (fileName.endsWith(".gif"))
			return XWPFDocument.PICTURE_TYPE_GIF;
		else if (fileName.endsWith(".tiff"))
			return XWPFDocument.PICTURE_TYPE_TIFF;
		else if (fileName.endsWith(".eps"))
			return XWPFDocument.PICTURE_TYPE_EPS;
		else if (fileName.endsWith(".bmp"))
			return XWPFDocument.PICTURE_TYPE_BMP;
		else if (fileName.endsWith(".wpg"))
			return XWPFDocument.PICTURE_TYPE_WPG;
		else
			return 0;
	}
}
