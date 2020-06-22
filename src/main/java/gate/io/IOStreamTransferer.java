package gate.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOStreamTransferer
{

	public static void transfer(InputStream inputStream, OutputStream outputStream) throws IOException
	{
		BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
		for (int b = bufferedInputStream.read(); b != -1; b = bufferedInputStream.read())
			bufferedOutputStream.write(b);
		bufferedOutputStream.flush();
	}
}
