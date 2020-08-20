package gate.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class XLSReader extends AbstractReader<List<List<String>>>
{

	private XLSReader()
	{
	}

	@Override
	public List<List<String>> read(InputStream is) throws IOException
	{
		Workbook workbook = WorkbookFactory.create(is);

		List<List<String>> result = new ArrayList<>();
		for (Sheet sheet : workbook)
			for (Row row : sheet)
			{
				List<String> line = new ArrayList<>();
				for (Cell cell : row)
					line.add(cell.getStringCellValue());
				result.add(line);
			}

		return result;
	}

	public static XLSReader getInstance()
	{
		return Instance.VALUE;
	}

	public static List<List<String>> load(byte[] bytes) throws IOException
	{
		try (ByteArrayInputStream is = new ByteArrayInputStream(bytes))
		{
			return XLSReader.getInstance().read(is);
		}
	}

	public static List<List<String>> load(File file) throws IOException
	{
		try (FileInputStream is = new FileInputStream(file))
		{
			return XLSReader.getInstance().read(is);
		}
	}

	public static List<List<String>> load(URL url) throws IOException
	{
		try (InputStream is = url.openStream())
		{
			return XLSReader.getInstance().read(is);
		}
	}

	public static List<List<String>> load(String string) throws IOException
	{
		try (ByteArrayInputStream is = new ByteArrayInputStream(string.getBytes()))
		{
			return XLSReader.getInstance().read(is);
		}
	}

	private static class Instance
	{

		public static final XLSReader VALUE = new XLSReader();
	}
}
