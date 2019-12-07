package gate.io;

import java.nio.charset.Charset;
import java.util.Objects;

public abstract class AbstractReader<T> implements Reader<T>
{

	protected final String charset;

	public AbstractReader(String charset)
	{
		Objects.requireNonNull(charset);
		this.charset = charset;
	}

	public AbstractReader()
	{
		this(Charset.defaultCharset().name());
	}

	@Override
	public String getCharset()
	{
		return charset;
	}
}
