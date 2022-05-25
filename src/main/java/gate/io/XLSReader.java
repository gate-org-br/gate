package gate.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class XLSReader extends AbstractReader<List<List<Object>>>
{

	private XLSReader()
	{
	}

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
			switch (cell.getCellType())
			{
				case STRING:
					return cell.getStringCellValue();
				case NUMERIC:
					if (DateUtil.isCellDateFormatted(cell))
						return cell.getLocalDateTimeCellValue();
					return BigDecimal.valueOf(cell.getNumericCellValue());
				case BOOLEAN:
					return cell.getBooleanCellValue();
				case BLANK:
					return "";
				case ERROR:
					return cell.getErrorCellValue();
				case FORMULA:
					switch (cell.getCachedFormulaResultType())
					{
						case STRING:
							return cell.getStringCellValue();
						case NUMERIC:
							if (DateUtil.isCellDateFormatted(cell))
								return cell.getLocalDateTimeCellValue();
							return BigDecimal.valueOf(cell.getNumericCellValue());
						case BOOLEAN:
							return cell.getBooleanCellValue();
						case BLANK:
							return "";
						case ERROR:
							return String.valueOf(cell.getErrorCellValue());
					}
			}
		return "";
	}

	public static XLSReader getInstance()
	{
		return Instance.VALUE;
	}

	public static List<List<Object>> load(byte[] bytes) throws IOException
	{
		try ( ByteArrayInputStream is = new ByteArrayInputStream(bytes))
		{
			return XLSReader.getInstance().read(is);
		}
	}

	public static List<List<Object>> load(File file) throws IOException
	{
		try ( FileInputStream is = new FileInputStream(file))
		{
			return XLSReader.getInstance().read(is);
		}
	}

	public static List<List<Object>> load(URL url) throws IOException
	{
		try ( InputStream is = url.openStream())
		{
			return XLSReader.getInstance().read(is);
		}
	}

	public static List<List<Object>> load(String string) throws IOException
	{
		try ( ByteArrayInputStream is = new ByteArrayInputStream(string.getBytes()))
		{
			return XLSReader.getInstance().read(is);
		}
	}

	private static class Instance
	{

		public static final XLSReader VALUE = new XLSReader();
	}
}
