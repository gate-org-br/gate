package gate.io;

import java.io.IOException;
import java.io.OutputStream;

public class GateOutputStream<T extends OutputStream> extends OutputStream
{

	private final T ostream;

	public GateOutputStream(T ostream)
	{

		this.ostream = ostream;
	}

	@Override
	public void write(int c) throws IOException
	{
		ostream.write(c);
	}

	@Override
	public void flush() throws IOException
	{
		ostream.flush();
	}

	@Override
	public void close() throws IOException
	{
		ostream.flush();
		ostream.close();
	}

	public T getOutputStream()
	{
		return ostream;
	}
}
