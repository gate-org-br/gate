package gate.io;

import gate.type.mime.MimeDataFile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Spliterator;
import static java.util.Spliterator.NONNULL;
import static java.util.Spliterator.ORDERED;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * An utility class to compress and extract zipped multipart MimeDataFile objects
 */
public class Zip
{

	/**
	 * Compress a stream of MimeDataFile objects into a single multipart zipped MimeDataFile
	 *
	 * @param name the name of the zipped multipart MimeDataFile to be created
	 * @param source the stream to be compressed
	 * @return a multipart zipped MimeDataFile object containing the files from the specified stream
	 * @throws IOException if an IOException occurs during the compression process
	 */
	public static MimeDataFile compress(String name, Stream<MimeDataFile> source) throws IOException
	{
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream))
		{
			source.forEach(e ->
			{
				try
				{
					ZipEntry entry = new ZipEntry(e.getName());
					zipOutputStream.putNextEntry(entry);
					zipOutputStream.write(e.getData());
					zipOutputStream.closeEntry();
				} catch (IOException ex)
				{
					throw new UncheckedIOException(ex);
				}
			});

			zipOutputStream.flush();
			byteArrayOutputStream.flush();
			return new MimeDataFile("application", "zip", byteArrayOutputStream.toByteArray(), name);
		} catch (UncheckedIOException ex)
		{
			throw ex.getCause();
		}
	}

	/**
	 * Compress a list of MimeDataFile objects into a single multipart zipped MimeDataFile
	 *
	 * @param name the name of the zipped multipart MimeDataFile to be created
	 * @param source the list to be compressed
	 * @return a multipart zipped MimeDataFile object containing the files from the specified list
	 * @throws IOException if an IOException occurs during the compression process
	 */
	public static MimeDataFile compress(String name, List<MimeDataFile> source) throws IOException
	{
		return compress(name, source.stream());
	}

	/**
	 * Extract the specified zipped multipart MimeDataFile into a list
	 *
	 * @param source the zipped multipart MimeDataFile to be extracted
	 * @return a list with all the contents of the specified zipped multipart MimeDataFile
	 * @throws IOException if an IOException occurs during the extraction process
	 */
	public static List<MimeDataFile> extract(MimeDataFile source) throws IOException
	{
		try (Stream<MimeDataFile> stream = stream(source))
		{
			return stream.collect(Collectors.toList());
		}
	}

	/**
	 * Extract the specified zipped multipart MimeDataFile into a stream
	 *
	 * @param source the zipped multipart MimeDataFile to be extracted
	 * @return a Stream with all the contents of the specified zipped multipart MimeDataFile
	 * @throws IOException if an IOException occurs during the extraction process
	 */
	public static Stream<MimeDataFile> stream(MimeDataFile source) throws IOException
	{
		try
		{
			ZipInputStream zipInputStream
				= new ZipInputStream(new ByteArrayInputStream(source.getData()));
			return StreamSupport.stream(new Spliterator<MimeDataFile>()
			{
				@Override
				public boolean tryAdvance(Consumer<? super MimeDataFile> action)
				{
					try
					{
						ZipEntry entry;
						do
						{
							entry = zipInputStream.getNextEntry();
							if (entry == null)
								return false;

							action.accept(new MimeDataFile(zipInputStream.readAllBytes(),
								new File(entry.getName()).getName()));
							return true;
						} while (entry.isDirectory());
					} catch (IOException ex)
					{
						throw new UncheckedIOException(ex);
					}
				}

				@Override
				public Spliterator<MimeDataFile> trySplit()
				{
					return null;
				}

				@Override
				public long estimateSize()
				{
					return Long.MAX_VALUE;
				}

				@Override
				public int characteristics()
				{
					return ORDERED | NONNULL;
				}

			}, false).onClose(() ->
			{
				try
				{
					zipInputStream.close();
				} catch (IOException ex)
				{
					throw new UncheckedIOException(ex);
				}
			});
		} catch (UncheckedIOException ex)
		{
			throw ex.getCause();
		}
	}
}
