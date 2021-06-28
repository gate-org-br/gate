package gate.io;

import gate.type.DataFile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.OpenMode;
import net.schmizz.sshj.sftp.RemoteFile;
import net.schmizz.sshj.sftp.RemoteResourceFilter;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.InMemoryDestFile;
import net.schmizz.sshj.xfer.InMemorySourceFile;

public class SFTP implements AutoCloseable
{

	private String directory;
	private final SSHClient ssh;
	private final SFTPClient client;

	private SFTP(SSHClient ssh, SFTPClient client)
	{
		this.ssh = ssh;
		this.client = client;
	}

	public static SFTP connect(String ip, int port,
		String username, String password) throws IOException
	{
		SFTPClient client = null;
		SSHClient ssh = new SSHClient();
		try
		{
			ssh.addHostKeyVerifier(new PromiscuousVerifier());
			ssh.connect(ip, port);
			ssh.authPassword(username, password);
			client = ssh.newSFTPClient();
			return new SFTP(ssh, client);
		} catch (IOException ex)
		{
			SFTP.close(client);
			SFTP.close(ssh);
			throw ex;
		}
	}

	private static boolean close(Closeable closeable)
	{
		if (closeable != null)
		{
			try
			{
				closeable.close();
				return true;
			} catch (IOException ex)
			{
				return false;
			}
		}
		return true;
	}

	private String path(String filename)
	{
		return directory != null
			? Path.of(directory, filename).toString()
			: filename;
	}

	public void cd(String directory) throws IOException
	{
		this.directory = directory;
	}

	public void append(String filename, byte[] bytes) throws IOException
	{
		try (RemoteFile file = client.open(path(filename), Set.of(OpenMode.APPEND, OpenMode.WRITE)))
		{
			file.write(0, bytes, 0, bytes.length);
		}
	}

	public void rm(String filename) throws IOException
	{
		client.rm(path(filename));
	}

	public void rename(String filename, String newFilename) throws IOException
	{
		client.rename(path(filename), path(newFilename));
	}

	public List<String> ls(String directory) throws IOException
	{
		return client.ls(path(directory)).stream().map(e -> e.getName()).collect(Collectors.toList());
	}

	public List<String> ls(String directory, int limit, Predicate<String> predicate) throws IOException
	{
		Filter filter = new Filter(predicate, limit);
		return client.ls(path(directory), filter).stream().map(e -> e.getName()).collect(Collectors.toList());
	}

	public void put(String filename, byte[] bytes) throws IOException
	{
		try (ByteArrayInputStream is = new ByteArrayInputStream(bytes))
		{
			client.put(new InMemorySourceFile()
			{
				@Override
				public InputStream getInputStream() throws IOException
				{
					return is;
				}

				@Override
				public String getName()
				{
					return filename;
				}

				@Override
				public long getLength()
				{
					return bytes.length;
				}
			}, path(filename));
		}
	}

	public DataFile download(String filename) throws IOException
	{
		try (ByteArrayOutputStream out = new ByteArrayOutputStream())
		{
			client.getFileTransfer().download(path(filename), new InMemoryDestFile()
			{
				@Override
				public OutputStream getOutputStream() throws IOException
				{
					return out;
				}
			});

			return new DataFile(out.toByteArray(), Path.of(filename).getFileName().toString());
		}
	}

	public void append(String filename, String text) throws IOException
	{
		append(filename, text.getBytes());
	}

	public List<String> ls(String directory, int limit) throws IOException
	{
		return ls(directory, limit, e -> true);
	}

	public List<String> ls(String directory, Predicate<String> predicate) throws IOException
	{
		return ls(directory, -1, predicate);
	}

	public Optional<String> findFirst(String directory, Predicate<String> predicate) throws IOException
	{
		return ls(directory, 1, predicate).stream().findFirst();
	}

	public void put(String filename, String text) throws IOException
	{
		put(filename, text.getBytes());
	}

	@Override
	public void close() throws Exception
	{
		if (!SFTP.close(client) | !SFTP.close(ssh))
			throw new IOException("Error when attempting to close a SFTP connection");

	}

	private class Filter implements RemoteResourceFilter
	{

		private int limit;
		private final Predicate<String> predicate;

		public Filter(Predicate<String> predicate, int limit)
		{
			this.predicate = predicate;
			this.limit = limit;
		}

		@Override
		public boolean accept(RemoteResourceInfo rri)
		{
			if (limit > 0 && predicate.test(rri.getName()))
			{
				limit--;
				return true;
			}
			return false;
		}
	}

}
