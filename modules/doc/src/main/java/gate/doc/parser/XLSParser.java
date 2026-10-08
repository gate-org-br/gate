package gate.doc.parser;

import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class XLSParser implements DocParser<List<List<Object>>>
{

	private static final DataFormatter FORMATTER = new DataFormatter();

	private XLSParser()
	{}

	@Override
	public List<List<Object>> read(InputStream is) throws IOException
	{
		Workbook workbook = WorkbookFactory.create(is);

		List<List<Object>> result = new ArrayList<>();
		for (Sheet sheet : workbook)
			for (Row row : sheet)
			{
				List<Object> line = new ArrayList<>();
				for (int i = 0; i < row.getLastCellNum(); i++)
					line.add(getValue(row.getCell(i)));
				result.add(line);
			}

		return result;
	}

	private Object getValue(Cell cell)
	{
		if (cell != null)
			return switch (cell.getCellType())
			{
				case STRING -> cell.getStringCellValue();
				case NUMERIC -> DateUtil.isCellDateFormatted(cell)
						? cell.getLocalDateTimeCellValue()
						: getNumber(cell);
				case BOOLEAN -> cell.getBooleanCellValue();
				case ERROR -> cell.getErrorCellValue();
				case FORMULA -> switch (cell.getCachedFormulaResultType())
				{
					case STRING -> cell.getStringCellValue();
					case NUMERIC -> DateUtil.isCellDateFormatted(cell)
							? cell.getLocalDateTimeCellValue()
							: getNumber(cell);
					case BOOLEAN -> cell.getBooleanCellValue();
					case ERROR -> String.valueOf(cell.getErrorCellValue());
					default -> "";
				};

				default -> "";
			};
		return "";
	}

	private String getNumber(Cell cell)
	{
		return FORMATTER.formatCellValue(cell);
	}

	public static XLSParser getInstance()
	{
		return Instance.VALUE;
	}

	public static List<List<Object>> parse(byte[] bytes) throws IOException
	{
		try (ByteArrayInputStream is = new ByteArrayInputStream(bytes))
		{
			return XLSParser.getInstance().read(is);
		}
	}

	public static List<List<Object>> parse(File file) throws IOException
	{
		try (FileInputStream is = new FileInputStream(file))
		{
			return XLSParser.getInstance().read(is);
		}
	}

	public static List<List<Object>> parse(URL url) throws IOException
	{
		try (InputStream is = url.openStream())
		{
			return XLSParser.getInstance().read(is);
		}
	}

	public static List<List<Object>> parse(String string) throws IOException
	{
		try (ByteArrayInputStream is = new ByteArrayInputStream(string.getBytes()))
		{
			return XLSParser.getInstance().read(is);
		}
	}

	private static class Instance
	{
		public static final XLSParser VALUE = new XLSParser();
	}
}