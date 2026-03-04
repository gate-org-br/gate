package gate.io;

import gate.function.TryConsumer;
import gate.function.TryPredicate;

public abstract class AbstractProcessor<T> implements Processor<T>
{

	protected final String charset;
	protected final TryPredicate<T> action;

	public AbstractProcessor(String charset, TryPredicate<T> action)
	{
		this.charset = charset;
		this.action = action;
	}

	public AbstractProcessor(TryPredicate<T> action)
	{
		this("utf-8", action);
	}

	public AbstractProcessor(String charset, TryConsumer<T> action)
	{
		this.charset = charset;
		this.action = e ->
		{
			action.accept(e);
			return true;
		};
	}

	public AbstractProcessor(TryConsumer<T> action)
	{
		this("utf-8", action);
	}

	@Override
	public String getCharset()
	{
		return charset;
	}
}
