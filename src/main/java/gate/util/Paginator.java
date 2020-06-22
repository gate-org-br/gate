package gate.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Paginator<E> implements Iterable<Page<E>>, Serializable
{

	private List<E> data;
	private Object resume;
	private final int size;
	private final int pageSize;
	private final int dataSize;
	private static final long serialVersionUID = 1L;

	public Paginator(List<E> data, int pageSize, Object resume)
	{
		if (data == null)
			throw new IllegalArgumentException("data");
		if (pageSize < 1)
			throw new IllegalArgumentException("pageSize");
		this.data = data;
		this.pageSize = pageSize;
		this.dataSize = data.size();
		this.resume = resume;
		size = dataSize == 0 ? 0 : (int) Math.ceil(dataSize / (double) pageSize);
	}

	public Paginator(int dataSize, int pageSize, Object resume)
	{
		if (dataSize < 0)
			throw new IllegalArgumentException("dataSize");
		if (pageSize < 1)
			throw new IllegalArgumentException("pageSize");
		this.dataSize = dataSize;
		this.pageSize = pageSize;
		this.resume = resume;
		size = dataSize == 0 ? 0 : (int) Math.ceil(dataSize / (double) pageSize);
	}

	public Paginator(E[] data, int pageSize, Object resume)
	{
		this(Arrays.asList(data), pageSize, resume);
	}

	public Paginator(E[] data, int pageSize)
	{
		this(Arrays.asList(data), pageSize, null);
	}

	public Paginator(List<E> data, int pageSize)
	{
		this(data, pageSize, null);
	}

	public Paginator(int dataSize, int pageSize)
	{
		this(dataSize, pageSize, null);
	}

	public int getDataSize()
	{
		return dataSize;
	}

	public List<E> getData()
	{
		return data;
	}

	public int getPageSize()
	{
		return pageSize;
	}

	public int getSize()
	{
		return size;
	}

	public int getFrstPageIndx()
	{
		return size == 0 ? -1 : 0;
	}

	public int getLastPageIndx()
	{
		return size == 0 ? -1 : size - 1;
	}

	public Page<E> getFrstPage()
	{
		if (data == null || getSize() == 0)
			return null;
		return new Page<>(this, 0);
	}

	public Page<E> getLastPage()
	{
		if (data == null || getSize() == 0)
			return null;
		return new Page<>(this, getSize() - 1);
	}

	public Page<E> getPage(int indx)
	{
		if (data == null || getSize() == 0)
			return null;
		return new Page<>(this, Math.min(Math.max(indx, 0), getSize() - 1));
	}

	public Page<E> getPage(int indx, List<E> data)
	{
		if (indx < 0 || indx >= getSize())
			return null;
		return new Page<>(this, indx, data);
	}

	@Override
	public Iterator<Page<E>> iterator()
	{
		return new Iterator<Page<E>>()
		{

			private int indx = 0;

			@Override
			public boolean hasNext()
			{
				return data != null && indx < getSize() - 1;
			}

			@Override
			public Page<E> next()
			{
				if (!hasNext())
					throw new NoSuchElementException();
				return getPage(++indx);
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public Spliterator<Page<E>> spliterator()
	{
		return new Spliterator<Page<E>>()
		{

			private int indx = 0;

			@Override
			public int characteristics()
			{
				return Spliterator.ORDERED
					| Spliterator.NONNULL;
			}

			@Override
			public long estimateSize()
			{
				return getSize();
			}

			@Override
			public boolean tryAdvance(Consumer<? super Page<E>> action)
			{
				if (indx < getSize() - 1)
					action.accept(getPage(++indx));
				return false;
			}

			@Override
			public void forEachRemaining(Consumer<? super Page<E>> action)
			{
				while (indx < getSize() - 1)
					action.accept(getPage(++indx));
			}

			@Override
			public Spliterator<Page<E>> trySplit()
			{
				throw new UnsupportedOperationException("Not supported yet.");
			}
		};
	}

	public Stream<Page<E>> stream()
	{
		return StreamSupport.stream(spliterator(), false);
	}

	public Object getResume()
	{
		return resume;
	}

	public void setResume(Object resume)
	{
		this.resume = resume;
	}

}
