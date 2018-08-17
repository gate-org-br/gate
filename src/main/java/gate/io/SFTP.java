package gate.io;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import gate.type.DataFile;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SFTP implements AutoCloseable
{

	private final Session session;
	private final ChannelSftp channel;

	private SFTP(Session session, ChannelSftp channel)
			throws IOException
	{
		this.session = session;
		this.channel = channel;
	}

	public static SFTP connect(String ip, int port, String username, String password) throws IOException
	{
		Session session = null;
		ChannelSftp channel = null;

		try
		{
			JSch jsch = new JSch();
			session = jsch.getSession(username, ip, port);
			session.setPassword(password);
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = (ChannelSftp) session.openChannel("sftp");
			channel.connect();
			return new SFTP(session, channel);
		} catch (JSchException ex)
		{
			if (channel != null)
				channel.disconnect();
			if (session != null)
				session.disconnect();
			throw new IOException("Erro trying to connect to SFTP server: " + ip, ex);
		}
	}

	public void cd(String directory) throws IOException
	{
		try
		{
			channel.cd(directory);
		} catch (SftpException ex)
		{
			throw new IOException("Error trying to change directory: " + directory, ex);
		}
	}

	public List<String> ls(String directory) throws IOException
	{
		try
		{
			List<?> list = Collections
					.list(channel.ls(directory).elements());
			return list
					.stream()
					.map(e -> ((ChannelSftp.LsEntry) e))
					.map(e -> e.getFilename())
					.collect(Collectors.toList());
		} catch (SftpException ex)
		{
			throw new IOException("Error trying to list directory: " + directory, ex);
		}
	}

	public void rm(String filename) throws IOException
	{
		try
		{
			channel.rm(filename);
		} catch (SftpException ex)
		{
			throw new IOException("Error trying to list directory", ex);
		}
	}

	public void rename(String filename, String newFilename) throws IOException
	{
		try
		{
			channel.rename(filename, newFilename);
		} catch (SftpException ex)
		{
			throw new IOException("Error trying to rename file", ex);
		}
	}

	public List<String> ls(String directory, int limit, Predicate<String> predicate) throws IOException
	{
		try
		{
			Filter filter = new Filter(predicate, limit);
			channel.ls(directory, filter);
			return filter.getResult();
		} catch (SftpException ex)
		{
			throw new IOException("Error trying to list directory: " + directory, ex);
		}
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

	public void put(String filename,
			byte[] bytes) throws IOException
	{
		try
		{
			try (ByteArrayInputStream byteArrayInputStream
					= new ByteArrayInputStream(bytes))
			{
				channel.put(byteArrayInputStream, filename);
			}
		} catch (SftpException ex)
		{
			throw new IOException("Error when trying to create file: " + filename, ex);
		}
	}

	public void append(String filename, String text) throws IOException
	{
		append(filename, text.getBytes());
	}

	public void append(String filename,
			byte[] bytes) throws IOException
	{
		try
		{
			try (ByteArrayInputStream byteArrayInputStream
					= new ByteArrayInputStream(bytes))
			{
				channel.put(byteArrayInputStream, filename, ChannelSftp.APPEND);
			}
		} catch (SftpException ex)
		{
			throw new IOException("Error when trying to append data to file: " + filename, ex);
		}
	}

	public DataFile download(String filename) throws IOException
	{
		try (BufferedInputStream i = new BufferedInputStream(channel.get(filename)))
		{
			try (ByteArrayOutputStream o = new ByteArrayOutputStream())
			{
				for (int c = i.read(); c != -1; c = i.read())
					o.write(c);
				o.flush();
				return new DataFile(o.toByteArray(), filename);
			}
		} catch (SftpException ex)
		{
			throw new IOException("Error trying to download file: " + filename, ex);
		}
	}

	@Override
	public void close()
	{
		channel.disconnect();
		session.disconnect();
	}

	private class Filter implements ChannelSftp.LsEntrySelector
	{

		private int limit;
		private final Predicate<String> predicate;
		private final List<String> result = new ArrayList<>();

		public Filter(Predicate<String> predicate, int limit)
		{
			this.predicate = predicate;
			this.limit = limit;
		}

		@Override
		public int select(ChannelSftp.LsEntry le)
		{
			if (limit != 0 && predicate.test(le.getFilename()))
			{
				limit--;
				result.add(le.getFilename());

			}
			return limit == 0
					? ChannelSftp.LsEntrySelector.BREAK
					: ChannelSftp.LsEntrySelector.CONTINUE;
		}

		private List<String> getResult()
		{
			return result;
		}
	}

}
