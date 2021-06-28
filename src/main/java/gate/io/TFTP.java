package gate.io;

import gate.error.AppException;
import gate.type.DataFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketException;
import org.apache.commons.net.tftp.TFTPClient;

public class TFTP
{

	private final String server;
	private final int port;

	public TFTP(String server, int port)
	{
		this.server = server;
		this.port = port;
	}

	public TFTP(String server)
	{
		this(server, 69);
	}

	public String getServer()
	{
		return server;
	}

	public int getPort()
	{
		return port;
	}

	public DataFile get(String filename) throws AppException
	{
		TFTPClient tftp = null;
		try
		{
			tftp = new TFTPClient();
			tftp.setDefaultTimeout(60000);
			tftp.open();
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
			{
				tftp.receiveFile(filename, TFTPClient.OCTET_MODE, baos, this.server, this.port);
				baos.flush();
				return new DataFile(baos.toByteArray(), filename);
			}
		} catch (SocketException e)
		{
			throw new AppException(String.format("Error trying to receive file: %s", e.getMessage()));
		} catch (IOException e)
		{
			throw new AppException(String.format("Error trying to receive file: %s", e.getMessage()));
		} finally
		{
			if (tftp != null
				&& tftp.isOpen())
				tftp.close();
		}
	}
}
