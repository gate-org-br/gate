package gate.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ODSReader extends AbstractReader<List<List<String>>>
{

	private ODSReader()
	{
	}

	@Override
	public List<List<String>> read(InputStream is) throws IOException
	{
		try
		{
			String string = null;
			try (ZipInputStream istream = new ZipInputStream(is);)
			{
				for (ZipEntry entry = istream.getNextEntry();
					entry != null; entry = istream.getNextEntry())
					if (entry.getName().toLowerCase().equals("content.xml"))
					{
						string = StringReader.getInstance().read(istream);
						break;
					}
			}

			if (string == null)
				throw new IOException("Invalid ods file");

			Document document
				= DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new ByteArrayInputStream(string.getBytes()));

			List<List<String>> result = new ArrayList<>();

			NodeList rows = document.getElementsByTagName("table:table-row");

			for (int i = 0; i < rows.getLength(); i++)
				result.add(parseRow(rows.item(i)));

			return result;

		} catch (ParserConfigurationException | SAXException ex)
		{
			throw new IOException(ex.getMessage(), ex);
		}

	}

	private List<String> parseRow(Node row)
	{
		NodeList cells = row.getChildNodes();
		List<String> result = new ArrayList<>();
		for (int i = 0; i < cells.getLength(); i++)
		{
			Node cell = cells.item(i);

			Node attribute = cell.getAttributes()
				.getNamedItem("table:number-columns-repeated");

			int repeat = attribute != null
				? Integer.parseInt(attribute.getNodeValue()) : 1;

			for (int j = 0; j < repeat; j++)
				result.add(cell.getTextContent());
		}
		return result;
	}

	public static ODSReader getInstance()
	{
		return Instance.VALUE;
	}

	public static List<List<String>> load(byte[] bytes) throws IOException
	{
		try (ByteArrayInputStream is = new ByteArrayInputStream(bytes))
		{
			return ODSReader.getInstance().read(is);
		}
	}

	public static List<List<String>> load(File file) throws IOException
	{
		try (FileInputStream is = new FileInputStream(file))
		{
			return ODSReader.getInstance().read(is);
		}
	}

	public static List<List<String>> load(URL url) throws IOException
	{
		try (InputStream is = url.openStream())
		{
			return ODSReader.getInstance().read(is);
		}
	}

	public static List<List<String>> load(String string) throws IOException
	{
		try (ByteArrayInputStream is = new ByteArrayInputStream(string.getBytes()))
		{
			return ODSReader.getInstance().read(is);
		}
	}

	private static class Instance
	{

		public static final ODSReader VALUE = new ODSReader();
	}
}
