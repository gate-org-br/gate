package gate.io;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.SCPOutputStream;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import gate.type.DataFile;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;

public class SSH implements AutoCloseable
{

	private final Connection connection;

	private SSH(Connection connection)
	{
		this.connection = connection;
	}

	public static SSH connect(String ip, int port) throws IOException
	{
		Connection connection = new Connection(ip, port);
		try
		{
			connection.connect(null, 10000, 10000);
			return new SSH(connection);
		} catch (IOException ex)
		{
			connection.close();
			throw ex;
		}
	}

	public boolean authenticate(String username) throws IOException
	{
		return connection.authenticateWithNone(username);
	}

	public boolean authenticate(String username, String password) throws IOException
	{
		return connection.authenticateWithPassword(username, password);
	}

	public boolean authenticate(String username, File key, String password) throws IOException
	{
		return connection.authenticateWithPublicKey(username, key, password);
	}

	public boolean authenticate(String username, char[] key, String password) throws IOException
	{
		return connection.authenticateWithPublicKey(username, key, password);
	}

	public boolean execute(String command, Object... parameters) throws IOException
	{
		return execute(String.format(command, parameters));
	}

	public boolean execute(String command) throws IOException
	{
		Session session = connection.openSession();
		try
		{
			session.execCommand(command);
			int condition = session
					.waitForCondition(ChannelCondition.STDOUT_DATA
							| ChannelCondition.STDERR_DATA
							| ChannelCondition.EXIT_STATUS, 0);

			if ((condition & ChannelCondition.STDOUT_DATA) != 0)
				return true;
			if ((condition & ChannelCondition.STDERR_DATA) != 0)
				return false;
			if ((condition & ChannelCondition.EXIT_STATUS) != 0)
				return session.getExitStatus() == 0;

			return false;
		} finally
		{
			session.close();
		}
	}

	public Result call(String command)
	{
		return new Result(command);
	}

	public Result call(String command, Object... parameters)
	{
		return call(String.format(command, parameters));
	}

	public byte[] get(String filename) throws IOException
	{
		try (InputStream is = new SCPClient(connection).get(filename))
		{
			return new ByteArrayReader().read(is);
		}
	}

	public void put(String filename, byte[] data) throws IOException
	{
		try (SCPOutputStream os = new SCPClient(connection)
				.put(filename, data.length, "", "0600"))
		{
			os.write(data);
		}
	}

	public DataFile download(String filename) throws IOException
	{
		return new DataFile(get(filename), new File(filename).getName());
	}

	@Override
	public void close()
	{
		if (connection != null)
			connection.close();
	}

	public class Result implements Readable, Processable
	{

		private final String command;

		private Result(String command)
		{
			this.command = command;
		}

		@Override
		public <T> T read(Reader<T> loader) throws IOException
		{
			Session session = connection.openSession();
			try
			{
				session.execCommand(command);
				int condition = session.waitForCondition(ChannelCondition.STDOUT_DATA
						| ChannelCondition.STDERR_DATA, 0);

				if ((condition & ChannelCondition.STDOUT_DATA) != 0)
				{
					try (InputStream stream = new StreamGobbler(session.getStdout()))
					{
						return loader.read(stream);
					}
				} else if ((condition & ChannelCondition.EOF) != 0)
				{
					try (InputStream stream = new ByteArrayInputStream(new byte[0]))
					{
						return loader.read(stream);
					}
				} else if ((condition & ChannelCondition.STDERR_DATA) != 0)
				{
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(new StreamGobbler(session
							.getStdout()),
							Charset.forName("UTF-8")));
							StringWriter writer = new StringWriter())
					{
						for (int c = reader.read(); c != -1; c = reader.read())
							writer.write((char) c);
						writer.flush();
						throw new IOException(writer.toString());
					}
				} else if ((condition & ChannelCondition.CLOSED) != 0)
					throw new IOException("Closed SSH Connection");
				else if ((condition & ChannelCondition.TIMEOUT) != 0)
					throw new IOException("Timeout exceeded");
				else
					throw new IOException("SSH error");
			} finally
			{
				session.close();
			}
		}

		@Override
		public <T> void process(Processor<T> processor) throws IOException
		{
			Session session = connection.openSession();
			try
			{
				session.execCommand(command);
				int condition = session.waitForCondition(ChannelCondition.STDOUT_DATA
						| ChannelCondition.STDERR_DATA, 0);

				if ((condition & ChannelCondition.STDOUT_DATA) != 0)
				{
					try (InputStream stream = new StreamGobbler(session.getStdout()))
					{
						processor.process(stream);
					}
				} else if ((condition & ChannelCondition.STDERR_DATA) != 0)
				{
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(new StreamGobbler(session
							.getStdout()),
							Charset.forName("UTF-8")));
							StringWriter writer = new StringWriter())
					{
						for (int c = reader.read(); c != -1; c = reader.read())
							writer.write((char) c);
						writer.flush();
						throw new IOException(writer.toString());
					}
				} else if ((condition & ChannelCondition.CLOSED) != 0)
					throw new IOException("Closed SSH Connection");
				else if ((condition & ChannelCondition.TIMEOUT) != 0)
					throw new IOException("Timeout exceeded");
				else
					throw new IOException("SSH error");
			} finally
			{
				session.close();
			}

		}
	}
}
