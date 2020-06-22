package gate.io;

import java.util.function.Consumer;

public abstract class AbstractProcessor<T> implements Processor<T>
{

	protected final String charset;
	protected final Consumer<T> consumer;

	public AbstractProcessor(String charset, Consumer<T> consumer)
	{
		this.charset = charset;
		this.consumer = consumer;
	}

	public AbstractProcessor(Consumer<T> consumer)
	{
		this("utf-8", consumer);
	}

	@Override
	public String getCharset()
	{
		return charset;
	}
}
