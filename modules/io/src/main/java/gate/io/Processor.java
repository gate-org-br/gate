package gate.io;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

@FunctionalInterface
public interface Processor<T>
{

	long process(InputStream is) throws IOException,
		InvocationTargetException;

	default String getCharset()
	{
		return "utf-8";
	}
}
