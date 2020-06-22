package gate.sql;

import java.util.Spliterator;

public abstract class CursorSpliterator<T> implements Spliterator<T>
{

	@Override
	public Spliterator<T> trySplit()
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
		return ORDERED;
	}
}
