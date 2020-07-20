package gate.io;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
public interface Processor<T>
{

	long process(InputStream is) throws IOException;

	default String getCharset()
	{
		return "utf-8";
	}
}
