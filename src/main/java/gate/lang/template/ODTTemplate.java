package gate.lang.template;

import gate.error.TemplateException;
import gate.io.ByteArrayReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ODTTemplate
{

	public static byte[] evaluate(byte[] file, Object entity) throws TemplateException
	{

		try
		{
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			try (ZipInputStream istream = new ZipInputStream(new ByteArrayInputStream(file));
				ZipOutputStream ostream = new ZipOutputStream(result))
			{

				ZipEntry entry;
				while ((entry = istream.getNextEntry()) != null)
				{
					byte[] data = ByteArrayReader.getInstance().read(istream);

					if (entry.getName().toLowerCase().equals("content.xml"))
					{
						String string = new String(data);
						entry = new ZipEntry("content.xml");
						string = Template.compile(string).evaluate(entity);
						data = string.getBytes();
					}

					ostream.putNextEntry(entry);
					ostream.write(data);
					ostream.flush();
					ostream.closeEntry();

				}
				ostream.flush();
				ostream.finish();
				return result.toByteArray();
			}
		} catch (IOException ex)
		{
			throw new TemplateException(ex.getMessage());
		}
	}
}
