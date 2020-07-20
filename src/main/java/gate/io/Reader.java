package gate.io;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
public interface Reader<T>
{

	T read(InputStream is) throws IOException;

	default String getCharset()
	{
		return "utf-8";
	}
}
