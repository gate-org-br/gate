package gate.io;

import gate.Progress;
import gate.type.DataFile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipIterable implements Iterable<DataFile>, AutoCloseable
{

	private int size;
	private final byte[] data;
	private final List<ZipIterator> iterators = new ArrayList<>();

	public ZipIterable(byte[] data)
	{
		this.data = data;
		try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(data)))
		{
			for (ZipEntry entry = zipInputStream.getNextEntry(); entry != null; entry = zipInputStream.getNextEntry())
			{
				if (!entry.isDirectory())
					Progress.update(++size);
				zipInputStream.closeEntry();
			}
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}

	}

	@Override
	public Iterator<DataFile> iterator()
	{
		ZipIterator extractorIterator = new ZipIterator();
		iterators.add(extractorIterator);
		return extractorIterator;
	}

	@Override
	public Spliterator<DataFile> spliterator()
	{
		ZipIterator extractorIterator = new ZipIterator();
		iterators.add(extractorIterator);
		return extractorIterator;
	}

	/**
	 * Returns a sequential Stream of the files stored on the zip.
	 *
	 * @return a sequential Stream of the files stored on the zip
	 */
	public Stream<DataFile> stream()
	{
		return StreamSupport.stream(spliterator(), false);
	}

	@Override
	public void close()
	{
		iterators.forEach(ZipIterator::close);
	}

	/**
	 * Returns the number of files on this pack.
	 *
	 * @return the number of files on this pack
	 */
	public int getSize()
	{
		return size;
	}

	private class ZipIterator implements Iterator<DataFile>, Spliterator<DataFile>
	{

		private int done;
		private final ZipInputStream zipInputStream;

		public ZipIterator()
		{
			zipInputStream = new ZipInputStream(new ByteArrayInputStream(data));
		}

		@Override
		public boolean hasNext()
		{
			return done < size;
		}

		@Override
		public DataFile next()
		{
			try
			{
				ZipEntry entry = zipInputStream.getNextEntry();
				while (entry.isDirectory())
				{
					zipInputStream.closeEntry();
					entry = zipInputStream.getNextEntry();
				}

				try (ByteArrayOutputStream data = new ByteArrayOutputStream())
				{
					for (int c = zipInputStream.read();
						c != -1;
						c = zipInputStream.read())
						data.write(c);
					data.flush();

					zipInputStream.closeEntry();

					done++;
					return new DataFile(data.toByteArray(), entry.getName());
				}
			} catch (IOException ex)
			{
				throw new UncheckedIOException(ex);
			}
		}

		@Override
		public void forEachRemaining(Consumer<? super DataFile> action)
		{
			while (hasNext())
				action.accept(next());
		}

		@Override
		public boolean tryAdvance(Consumer action)
		{
			if (!hasNext())
				return false;
			action.accept(next());
			return true;
		}

		@Override
		public Spliterator trySplit()
		{
			return null;
		}

		@Override
		public long estimateSize()
		{
			return size;
		}

		@Override
		public int characteristics()
		{
			return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.NONNULL;
		}

		public void close()
		{
			try
			{
				zipInputStream.close();
			} catch (IOException ex)
			{
				throw new UncheckedIOException(ex);
			}
		}

	}

}
