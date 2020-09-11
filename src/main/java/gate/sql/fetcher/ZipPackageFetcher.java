package gate.sql.fetcher;

import gate.sql.Cursor;
import gate.type.NamedTempFile;
import gate.type.TempFile;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Fetches the cursor as a temporary zipped multipart temporary file.
 */
public class ZipPackageFetcher implements Fetcher<NamedTempFile>
{

	public static final ZipPackageFetcher INSTANCE = new ZipPackageFetcher();

	private ZipPackageFetcher()
	{

	}

	/**
	 * Fetches the cursor as a temporary zipped multipart temporary file.
	 *
	 * <p>
	 * Each row must contain a name column with the correspondent file name
	 * and a data column with the correspondent file data
	 *
	 * @return The cursor as a temporary zipped multipart temporary file
	 */
	@Override
	public NamedTempFile fetch(Cursor cursor)
	{
		List<String> names
			= new ArrayList<>();
		TempFile tempFile = TempFile.empty();

		try
		{
			try (ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(tempFile.getOutputStream())))
			{
				while (cursor.next())
				{
					String name = cursor.getValue(String.class, "name");

					if (!names.contains(name))
					{
						try (BufferedInputStream bufferedInputStream
							= new BufferedInputStream(cursor.getResultSet().getBinaryStream("data")))
						{

							ZipEntry entry = new ZipEntry(name);
							zipOutputStream.putNextEntry(entry);
							bufferedInputStream.transferTo(zipOutputStream);
							zipOutputStream.closeEntry();
						}
						names.add(name);
					}

				}

				zipOutputStream.finish();
				zipOutputStream.flush();
			}

			return tempFile.named("package.zip");
		} catch (IOException | SQLException ex)
		{
			tempFile.close();
			throw new UnsupportedOperationException(ex);
		}
	}
}
