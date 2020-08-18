package gate.io;

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

	@Override
	public String getCharset()
	{
		return charset;
	}
}
