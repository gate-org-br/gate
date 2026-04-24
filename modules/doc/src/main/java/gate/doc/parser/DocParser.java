package gate.doc.parser;

import java.io.IOException;
import java.io.InputStream;

public interface DocParser<T>
{
	T read(InputStream inputStream) throws IOException;
}