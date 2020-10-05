package gate.io;

import gate.stream.CheckedConsumer;
import gate.stream.CheckedPredicate;

public abstract class AbstractProcessor<T> implements Processor<T>
{

	protected final String charset;
	protected final CheckedPredicate<T> action;

	public AbstractProcessor(String charset, CheckedPredicate<T> action)
	{
		this.charset = charset;
		this.action = action;
	}

	public AbstractProcessor(CheckedPredicate<T> action)
	{
		this("utf-8", action);
	}

	public AbstractProcessor(String charset, CheckedConsumer<T> action)
	{
		this.charset = charset;
		this.action = e ->
		{
			action.accept(e);
			return true;
		};
	}

	public AbstractProcessor(CheckedConsumer<T> action)
	{
		this("utf-8", action);
	}

	@Override
	public String getCharset()
	{
		return charset;
	}
}
