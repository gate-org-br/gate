package gate.io;

import java.io.IOException;
import java.io.InputStream;

public interface Reader<T>
{

	public T read(InputStream is) throws IOException;

	public String getCharset();
}
