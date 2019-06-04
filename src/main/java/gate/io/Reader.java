package gate.io;

import java.io.IOException;
import java.io.InputStream;

public interface Reader<T>
{

	T read(InputStream is) throws IOException;

	String getCharset();
}
