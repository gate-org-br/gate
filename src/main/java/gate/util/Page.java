package gate.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Page<E> implements Iterable<E>, Serializable
{

	private final int indx;
	private List<E> data;
	private final Paginator<E> paginator;
	private static final long serialVersionUID = 1L;

	Page(Paginator<E> paginator, int indx)
	{
		this.indx = indx;
		this.paginator = paginator;
	}

	Page(Paginator<E> paginator, int indx, List<E> data)
	{
		this.indx = indx;
		this.data = data;
		this.paginator = paginator;
	}

	public Paginator<E> getPaginator()
	{
		return paginator;
	}

	public int getIndx()
	{
		return indx;
	}

	public boolean isFrst()
	{
		return indx == 0;
	}

	public boolean isLast()
	{
		return indx == paginator.getSize() - 1;
	}

	public List<E> getData()
	{
		return Collections.unmodifiableList(data);
	}

	public Page<E> getPrev()
	{
		if (getPaginator().getData() == null || isFrst())
			return null;
		return new Page<>(paginator, indx - 1);
	}

	public Page<E> getNext()
	{
		if (getPaginator().getData() == null || isLast())
			return null;
		return new Page<>(paginator, indx + 1);
	}

	public int getNextIndx()
	{
		return isLast() ? -1 : indx + 1;
	}

	public int getPrevIndx()
	{
		return isFrst() ? -1 : indx - 1;
	}

	public int getSize()
	{
		if (data != null)
			return data.size();
		return Math.min(paginator.getPageSize(), paginator.getData().size() - ((indx) * paginator.getPageSize()));
	}

	public E getLine(int indx)
	{
		if (data != null)
			return data.get(indx);
		return indx >= 0 && indx <= getSize() - 1 ? paginator.getData().get((paginator.getPageSize() * (this.indx)) + indx) : null;
	}

	public int getFrstLineIndx()
	{
		return 0;
	}

	public int getLastLineIndx()
	{
		return getSize() - 1;
	}

	public E getFrstLine()
	{
		return getLine(getFrstLineIndx());
	}

	public E getLastLine()
	{
		return getLine(getLastLineIndx());
	}

	@Override
	public Iterator<E> iterator()
	{
		return new PageIterator();
	}

	private class PageIterator implements Iterator<E>
	{

		private int indx;

		public PageIterator()
		{
			indx = 0;
		}

		@Override
		public boolean hasNext()
		{
			return indx < getSize();
		}

		@Override
		public E next()
		{
			if (!hasNext())
				throw new NoSuchElementException();
			return getLine(indx++);
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}

	public static <T> Page<T> of(List<T> data, int pageSize, int pageIndex)
	{
		return new Paginator(data, pageSize).getPage(pageIndex);
	}

	public static <T> Page<T> of(List<T> data, int dataSize, int pageSize, int pageIndex)
	{
		return new Paginator(dataSize, pageSize).getPage(pageIndex, data);
	}
}
