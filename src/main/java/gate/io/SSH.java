package gate.io;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import gate.type.DataFile;
import gate.util.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class SSH implements AutoCloseable
{

	private String folder;
	private final Session session;
	private static final byte LINE_FEED = 0x0a;
	private static final Pattern RESPONSE = Pattern.compile("^C[0-9]+ ([0-9]+) .+$");

	private SSH(Session session)
	{
		this.session = session;
	}

	public static SSH connect(String ip, int port, String username, String password) throws IOException
	{
		try
		{
			Session session = new JSch().getSession(username, ip, port);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(password);
			session.setServerAliveInterval(1000);
			session.connect();
			return new SSH(session);
		} catch (JSchException ex)
		{
			throw new IOException(ex.getMessage(), ex);
		}
	}

	public boolean execute(String command, Object... parameters) throws IOException
	{
		return execute(String.format(command, parameters));
	}

	public void setWorkDirectory(String folder)
	{
		this.folder = folder;
	}

	public boolean execute(String command) throws IOException
	{
		ChannelExec channel = null;
		try
		{
			channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(folder != null ? "cd " + folder + " && " + command : command);

			try ( InputStream is = channel.getInputStream())
			{
				channel.connect();
				while (is.read() != -1);
				while (!channel.isEOF())
					Toolkit.sleep(500);
				channel.disconnect();
			}

			return channel.getExitStatus() == 0;

		} catch (JSchException ex)
		{
			throw new IOException(ex.getMessage(), ex);
		} finally
		{
			if (channel != null && channel.isConnected())
				channel.disconnect();
		}
	}

	public SSHResult call(String command)
	{
		return new SSHResult(command);
	}

	public SSHResult call(String command, Object... parameters)
	{
		return call(String.format(command, parameters));
	}

	public byte[] get(String filename) throws IOException
	{
		try
		{
			ChannelExec channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand("scp -f " + filename);

			try ( OutputStream out = channel.getOutputStream();
				 InputStream in = channel.getInputStream();
				 ByteArrayOutputStream stream = new ByteArrayOutputStream())
			{
				channel.connect();

				out.write(0);
				out.flush();

				for (int read = in.read(); read != LINE_FEED; read = in.read())
				{
					if (read < 0)
						throw new IOException("Unexpected end of stream");
					stream.write(read);
				}

				String response = stream.toString("UTF-8");
				Matcher matcher = RESPONSE.matcher(response);
				if (!matcher.matches())
					throw new IOException(response.substring(1));
				long filesize = Long.parseLong(matcher.group(1));

				out.write(0);
				out.flush();

				stream.reset();
				for (int i = 0; i < filesize; i++)
				{
					int c = in.read();
					if (c < 0)
						throw new IOException("Unexpected end of stream");
					stream.write(c);
				}
				stream.flush();

				int b = in.read();
				if (b == -1)
					throw new IOException("Unexpected end of stream");

				if (b != 0)
				{
					StringBuilder sb = new StringBuilder();
					for (int c = in.read(); c > 0 && c != '\n'; c = in.read())
						sb.append((char) c);
					throw new IOException(sb.toString());
				}

				out.write(0);
				out.flush();

				return stream.toByteArray();
			}
		} catch (JSchException ex)
		{
			throw new IOException(ex.getMessage(), ex);
		}
	}

	public void put(String directory, String filename, byte[] data) throws IOException
	{
		try
		{

			ChannelExec channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand("cd " + folder + " && " + "scp -p -t " + filename);

			try ( OutputStream out = channel.getOutputStream();
				 InputStream in = channel.getInputStream();
				 ByteArrayOutputStream error = new ByteArrayOutputStream())
			{
				channel.setErrStream(error);
				channel.connect();

				String command = "C0644 " + data.length + " " + filename + "\n";
				out.write(command.getBytes());
				out.flush();

				for (int i = 0; i < data.length; i++)
					out.write(data[i]);
				out.flush();

				out.close();

				while (in.read() != -1);
				channel.disconnect();

				if (channel.getExitStatus() != 0)
					throw new IOException(new String(error.toByteArray()));
			}
		} catch (JSchException ex)
		{
			throw new IOException(ex.getMessage(), ex);
		}
	}

	public void put(String filename, byte[] data) throws IOException
	{
		put(folder, filename, data);
	}

	public DataFile download(String filename) throws IOException
	{
		return new DataFile(get(filename), new File(filename).getName());
	}

	@Override
	public void close()
	{
		if (session != null && session.isConnected())
			session.disconnect();
	}

	public class SSHResult implements IOResult
	{

		private final String command;

		private SSHResult(String command)
		{
			this.command = command;
		}

		@Override
		public <T> T read(Reader<T> loader) throws IOException
		{
			ChannelExec channel = null;
			try
			{
				ByteArrayOutputStream error = new ByteArrayOutputStream();
				channel = (ChannelExec) session.openChannel("exec");
				channel.setCommand(folder != null ? "cd " + folder + " && " + command : command);

				channel.setErrStream(error);
				channel.setInputStream(null);

				try ( InputStream is = channel.getInputStream())
				{
					channel.connect();
					T result = loader.read(is);
					while (!channel.isEOF())
						Toolkit.sleep(500);
					channel.disconnect();
					if (channel.getExitStatus() != 0)
						throw new IOException(new String(error.toByteArray()));
					return result;
				}

			} catch (JSchException ex)
			{
				throw new IOException(ex.getMessage(), ex);
			} finally
			{
				if (channel != null && channel.isConnected())
					channel.disconnect();
			}
		}

		@Override
		public <T> long process(Processor<T> processor) throws IOException, InvocationTargetException
		{
			ChannelExec channel = null;
			try
			{
				ByteArrayOutputStream error = new ByteArrayOutputStream();
				channel = (ChannelExec) session.openChannel("exec");
				channel.setCommand(folder != null ? "cd " + folder + " && " + command : command);

				channel.setErrStream(error);
				channel.setInputStream(null);

				try ( InputStream is = channel.getInputStream())
				{
					channel.connect();
					long result = processor.process(is);
					while (!channel.isEOF())
						Toolkit.sleep(500);
					channel.disconnect();
					if (channel.getExitStatus() != 0)
						throw new IOException(new String(error.toByteArray()));
					return result;
				}

			} catch (JSchException ex)
			{
				throw new IOException(ex.getMessage(), ex);
			} finally
			{
				if (channel != null && channel.isConnected())
					channel.disconnect();
			}
		}

		@Override
		public <T> Stream<T> stream(Function<InputStream, Spliterator<T>> spliterator) throws IOException
		{
			ChannelExec channel = null;
			try
			{
				ByteArrayOutputStream error = new ByteArrayOutputStream();
				channel = (ChannelExec) session.openChannel("exec");
				channel.setCommand(folder != null ? "cd " + folder + " && " + command : command);

				channel.setErrStream(error);
				channel.setInputStream(null);

				try ( InputStream is = channel.getInputStream())
				{
					channel.connect();
					ChannelExec c = channel;
					try
					{
						Stream stream = StreamSupport.stream(spliterator.apply(channel.getInputStream()), false)
							.onClose(() ->
							{
								while (!c.isEOF())
									Toolkit.sleep(500);

								c.disconnect();

								if (c.getExitStatus() != 0)
									throw new UncheckedIOException(new IOException(new String(error.toByteArray())));
							});

						return stream;
					} catch (UncheckedIOException ex)
					{
						throw ex.getCause();
					}
				}
			} catch (JSchException | RuntimeException ex)
			{
				if (channel != null && channel.isConnected())
					channel.disconnect();
				throw new IOException(ex.getMessage(), ex);
			}
		}
	}
}
