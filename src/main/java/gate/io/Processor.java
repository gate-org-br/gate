package gate.io;

import java.io.IOException;
import java.io.InputStream;

public interface Processor<T>
{

	long process(InputStream is) throws IOException;

	String getCharset();

}
