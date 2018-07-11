package gate.io;

import java.io.IOException;
import java.io.InputStream;

public interface Processor<T>
{

	public void process(InputStream is) throws IOException;

	public String getCharset();

}
