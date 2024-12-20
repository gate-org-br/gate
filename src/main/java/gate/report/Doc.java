package gate.report;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.annotation.Icon;
import gate.converter.EnumStringConverter;
import gate.error.AppError;
import gate.handler.DocHandler;
import gate.lang.contentType.ContentType;
import gate.report.doc.CSV;
import gate.report.doc.DOC;
import gate.report.doc.PDF;
import gate.report.doc.XLS;
import gate.type.mime.MimeDataFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Generates documents from objects of type {@link gate.report.Report}.
 */
@Icon("2217")
@Handler(DocHandler.class)
public abstract class Doc
{

	private final Report report;

	/**
	 * Constructs a new Doc for the specified report.
	 *
	 * @param report the report to be used to generate the document
	 */
	public Doc(Report report)
	{
		this.report = report;
	}

	/**
	 * Returns the MIME content type associated with this Document type.
	 *
	 * @return Returns the MIME content type associated with this Document type
	 */
	public abstract ContentType getContentType();

	/**
	 * Returns the File name associated with this Document type.
	 *
	 * @return the File name associated with this Document type
	 */
	public abstract String getFileName();

	/**
	 * Generates a document from the previously specified {@link gate.report.Report} and prints it on the specified
	 * output stream.
	 *
	 * @param outputStream the OutputStream where the generated document document must be printed
	 */
	public abstract void print(OutputStream outputStream);

	/**
	 * Generates a document for the previously specified report.
	 *
	 * @return a MimeDataFile containing the generated document data
	 */
	public MimeDataFile getFile()
	{
		try (ByteArrayOutputStream os = new ByteArrayOutputStream())
		{
			print(os);
			os.flush();
			return MimeDataFile.of(getContentType(), os.toByteArray(), getFileName());
		} catch (IOException ex)
		{
			throw new AppError(ex);
		}
	}

	/**
	 * Returns the report associated with this document.
	 *
	 * @return the report associated with this document
	 */
	public Report getReport()
	{
		return report;
	}

	/**
	 * Creates a new document of the specified type for the specified Report.
	 *
	 * @param type type of the document to be created
	 * @param report Report from where the document will be generated
	 *
	 * @return the new Doc created
	 */
	public static Doc create(Type type, Report report)
	{
		if (type == null)
			throw new java.lang.IllegalArgumentException("type");
		if (report == null)
			throw new java.lang.IllegalArgumentException("report");
		switch (type)
		{
			case PDF:
				return new PDF(report);
			case XLS:
				return new XLS(report);
			case CSV:
				return new CSV(report);
			case DOC:
				return new DOC(report);
			default:
				return null;
		}
	}

	/**
	 * Types of documents generated by the gate API.
	 */
	@Converter(EnumStringConverter.class)
	public enum Type
	{
		/**
		 * PDF document type.
		 */
		@Icon("2218")
		PDF,
		/**
		 * XLS document type.
		 */
		@Icon("2221")
		XLS,
		/**
		 * CSV document type.
		 */
		@Icon("2222")
		CSV,
		/**
		 * DOC document type.
		 */
		@Icon("2220")
		DOC
	}
}
