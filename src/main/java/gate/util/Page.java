package gate.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class Page<E> implements Collection<E>, Serializable
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

	public boolean isFirst()
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
		if (getPaginator().getData() == null || isFirst())
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
		return isFirst() ? -1 : indx - 1;
	}

	public int getSize()
	{
		if (data != null)
			return data.size();
		return Math.min(paginator.getPageSize(),
			paginator.getData().size() - ((indx) * paginator.getPageSize()));
	}

	public E getLine(int indx)
	{
		if (data != null)
			return data.get(indx);
		return indx >= 0 && indx <= getSize() - 1
			? paginator.getData().get((paginator.getPageSize() * (this.indx)) + indx)
			: null;
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

	@Override
	public int size()
	{
		return getSize();
	}

	@Override
	public boolean isEmpty()
	{
		return getSize() == 0;
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		for (Object element : c)
			if (!contains(element))
				return false;
		return true;
	}

	@Override
	public boolean contains(Object o)
	{
		for (E element : this)
			if (Objects.equals(element, o))
				return true;
		return false;
	}

	@Override
	public Object[] toArray()
	{
		List<E> array = new ArrayList<>();
		for (E element : this)
			array.add(element);
		return array.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		List<E> array = new ArrayList<>();
		for (E element : this)
			array.add(element);
		return array.toArray(a);
	}

	@Override
	public boolean add(E e)
	{
		throw new UnsupportedOperationException("Readonly collection.");
	}

	@Override
	public boolean remove(Object o)
	{
		throw new UnsupportedOperationException("Readonly collection.");
	}

	@Override
	public boolean addAll(Collection<? extends E> c)
	{
		throw new UnsupportedOperationException("Readonly collection.");
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		throw new UnsupportedOperationException("Readonly collection.");
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		throw new UnsupportedOperationException("Readonly collection.");
	}

	@Override
	public void clear()
	{
		throw new UnsupportedOperationException("Readonly collection.");
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
		return new Paginator<T>(data, pageSize).getPage(pageIndex);
	}

	public static <T> Page<T> of(List<T> data, int dataSize, int pageSize, int pageIndex)
	{
		return new Paginator<T>(dataSize, pageSize).getPage(pageIndex, data);
	}
}
