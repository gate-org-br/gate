package gate.sql.fetcher;

import gate.sql.Cursor;
import gate.type.TempFile;
import gate.type.mime.MimeDataFile;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Fetches the cursor as a temporary zipped multipart temporary file.
 */
public class TempFileFetcher implements Fetcher<TempFile>
{

	/**
	 * Fetches the cursor as a temporary zipped multipart temporary file.
	 *
	 * @return The cursor as a temporary zipped multipart temporary
	 *
	 */
	@Override
	public TempFile fetch(Cursor cursor)
	{
		try
		{
			TempFile tempFile = TempFile.newInstance();
			try
			{

				try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(tempFile.getFile())))
				{
					while (cursor.next())
					{
						MimeDataFile mimeDataFile = cursor.getValue(MimeDataFile.class, 1);
						ZipEntry entry = new ZipEntry(mimeDataFile.getName());
						zipOutputStream.putNextEntry(entry);
						zipOutputStream.write(mimeDataFile.getData());
						zipOutputStream.closeEntry();
					}

					zipOutputStream.flush();
					byteArrayOutputStream.flush();
				}

				return tempFile;
			} catch (IOException ex)
			{
				tempFile.delete();
				throw new UnsupportedOperationException(ex);
			}
		} catch (IOException ex)
		{
			throw new UnsupportedOperationException(ex);
		}
	}
}
