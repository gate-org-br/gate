package gate.util;

import java.util.Enumeration;
import java.util.Iterator;

class IteratorEnumeration<E> implements Enumeration<E>
{

	private final Iterator<E> iterator;

	public IteratorEnumeration(Iterator<E> iterator)
	{
		this.iterator = iterator;
	}

	@Override
	public E nextElement()
	{
		return iterator.next();
	}

	@Override
	public boolean hasMoreElements()
	{
		return iterator.hasNext();
	}
}
