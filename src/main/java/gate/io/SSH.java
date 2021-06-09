package gate.io;

import gate.type.DataFile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.UserAuthException;
import net.schmizz.sshj.xfer.InMemoryDestFile;
import net.schmizz.sshj.xfer.InMemorySourceFile;

/**
 * Used to execute remote commands on remote hosts via SSH.
 */
public class SSH implements AutoCloseable
{

	private String directory = ".";
	private final SSHClient client;

	private SSH(SSHClient client)
	{
		this.client = client;
	}

	/**
	 * Create a new SSH connection to the specified host on port 22
	 *
	 * @param host address of name of the host where to connect
	 *
	 * @return this, for chained invocations
	 *
	 * @throws IOException in case of failure during connection
	 */
	public static SSH connect(String host) throws IOException
	{
		return connect(host, 22);
	}

	/**
	 * Create a new SSH connection to the specified host on the specified port
	 *
	 * @param host address of name of the host where to connect
	 * @param port the port to be used when connecting
	 *
	 * @return this, for chained invocations
	 *
	 * @throws IOException in case of failure during connection
	 */
	public static SSH connect(String host, int port) throws IOException
	{
		SSHClient client = new SSHClient();
		client.addHostKeyVerifier(new PromiscuousVerifier());
		client.connect(host, port);
		return new SSH(client);
	}

	/**
	 * Authenticate {@code username} using the {@code "publickey"} authentication method, with keys from some common locations on the file system. This
	 * method relies on {@code ~/.ssh/id_rsa} and {@code ~/.ssh/id_dsa}.
	 * <p>
	 * This method does not provide a way to specify a passphrase.
	 *
	 * @param username user to authenticate
	 *
	 * @return true if successful, false otherwise
	 *
	 * @throws IOException in case of failure during authentication
	 */
	public boolean authenticate(String username) throws IOException
	{
		try
		{
			client.authPublickey(username);
			return true;
		} catch (UserAuthException ex)
		{
			return false;
		} catch (TransportException ex)
		{
			throw ex;
		}
	}

	/**
	 * Authenticate {@code username} using the {@code "publickey"} authentication method, with keys from one or more {@code locations} in the file system.
	 * <p>
	 * In case multiple {@code locations} are specified; authentication is attempted in order as long as the {@code
	 * "publickey"} authentication method is available. If there is an error loading keys from any of them (e.g. file could not be read, file format not
	 * recognized) that key file it is ignored.
	 * <p>
	 * This method does not provide a way to specify a passphrase.
	 *
	 * @param username user to authenticate
	 * @param locations one or more locations in the file system containing the private key
	 *
	 * @return true if successful, false otherwise
	 *
	 * @throws IOException in case of failure during authentication
	 */
	public boolean authenticate(String username, Path... locations) throws IOException
	{
		try
		{
			client.authPublickey(username, Stream.of(locations)
				.map(e -> e.toAbsolutePath().toString())
				.toArray(String[]::new));
			return true;
		} catch (UserAuthException ex)
		{
			return false;
		} catch (TransportException ex)
		{
			throw ex;
		}
	}

	/**
	 * Authenticate {@code username} using the {@code "password"} authentication method and as a fallback basic challenge-response authentication.
	 *
	 * @param username user to authenticate
	 * @param password the password to use for authentication
	 *
	 * @return true if successful, false otherwise
	 *
	 * @throws IOException in case of failure during authentication
	 */
	public boolean authenticate(String username, String password) throws IOException
	{
		try
		{
			client.authPassword(username, password);
			return true;
		} catch (UserAuthException ex)
		{
			return false;
		} catch (TransportException ex)
		{
			throw ex;
		}
	}

	/**
	 * Change the working directory
	 *
	 * @param directory the new directory
	 */
	public void setWorkDirectory(String directory)
	{
		this.directory = directory != null && !directory.isBlank() ? directory : ".";

	}

	/**
	 * Execute the specified command on the remote host
	 *
	 * @param command command to execute
	 *
	 * @return true if the command was successful, false otherwise
	 *
	 * @throws IOException in case of failure during command execution
	 */
	public boolean execute(String command) throws IOException
	{
		try (Session session = client.startSession())
		{
			try (Session.Command sessionCommand = session.exec("cd " + directory + " && " + command))
			{
				try (InputStream stream = sessionCommand.getInputStream())
				{
					for (int c = stream.read(); c != -1; c = stream.read())
						OutputStream.nullOutputStream().write(c);
				}

				try (InputStream stream = sessionCommand.getErrorStream())
				{
					for (int c = stream.read(); c != -1; c = stream.read())
						OutputStream.nullOutputStream().write(c);
				}

				sessionCommand.close();
				return sessionCommand.getExitStatus() == 0;

			}
		}
	}

	/**
	 * Execute the specified command on the remote host
	 *
	 * @param command formatting string of the command to execute
	 * @param parameters parameters to be used when formatting the command to be executed
	 *
	 * @return true if the command was successful, false otherwise
	 *
	 * @throws IOException in case of failure during command execution
	 */
	public boolean execute(String command, Object... parameters) throws IOException
	{
		return execute(String.format(command, parameters));
	}

	/**
	 * Execute the specified command on the remote host and read result
	 *
	 * @param command command to execute
	 *
	 * @return The command result as a SSHResult object
	 */
	public SSHResult call(String command)
	{
		return new SSHResult("cd " + directory + " && " + command);
	}

	/**
	 * Execute the specified command with the specified parameters on the remote host and read result
	 *
	 * @param command command to execute
	 * @param parameters parameters to be used on execution
	 *
	 * @return The command result as a SSHResult object
	 */
	public SSHResult call(String command, Object... parameters)
	{
		return call(String.format(command, parameters));
	}

	/**
	 * Downloads the specified file from the remote host via SCP
	 *
	 * @param filename the file to be downloaded
	 *
	 * @return The contents of the downloaded file as a byte array
	 *
	 * @throws IOException in case of failure during command execution
	 */
	public byte[] get(String filename) throws IOException
	{
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream())
		{
			client.newSCPFileTransfer().download(filename, new InMemoryDestFile()
			{
				@Override
				public OutputStream getOutputStream() throws IOException
				{
					return stream;
				}
			});

			stream.flush();
			return stream.toByteArray();
		}

	}

	/**
	 * Uploads a file to the remote host
	 *
	 * @param directory the directory where to put file
	 * @param filename the name of the file to be uploaded
	 * @param data the contents of the file to be uploaded
	 *
	 * @throws IOException in case of failure during command execution
	 */
	public void put(String directory, String filename, byte[] data) throws IOException
	{
		try (ByteArrayInputStream stream = new ByteArrayInputStream(data))
		{
			client.newSCPFileTransfer().upload(new InMemorySourceFile()
			{
				@Override
				public InputStream getInputStream() throws IOException
				{
					return stream;
				}

				@Override
				public long getLength()
				{
					return data.length;
				}

				@Override
				public String getName()
				{
					return filename;
				}

			}, directory + "/" + filename);
		}
	}

	/**
	 * Uploads a file to the remote host
	 *
	 * @param filename the name of the file to be uploaded
	 * @param data the contents of the file to be uploaded
	 *
	 * @throws IOException in case of failure during command execution
	 */
	public void put(String filename, byte[] data) throws IOException
	{
		put(directory, filename, data);
	}

	/**
	 * Downloads the specified file from the remote host via SCP
	 *
	 * @param filename the file to be downloaded
	 *
	 * @return The contents of the downloaded file as a DataFile
	 *
	 * @throws IOException in case of failure during command execution
	 */
	public DataFile download(String filename) throws IOException
	{
		return new DataFile(get(filename),
			new File(filename).getName());
	}

	@Override
	public void close() throws IOException
	{
		try
		{
			client.disconnect();
		} finally
		{
			client.close();
		}
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
			try (Session session = client.startSession())
			{
				try (Session.Command command = session.exec(this.command))
				{
					T result;
					try (InputStream stream = command.getInputStream())
					{
						result = loader.read(stream);
					}

					String error;
					try (InputStream stream = command.getErrorStream();
						ByteArrayOutputStream baos = IOUtils.readFully(stream))
					{
						error = new String(baos.toByteArray());
					}

					command.close();

					if (command.getExitStatus() != 0)
						throw new IOException(error.isBlank() ? "Error Status: " + command.getExitStatus() : error);
					return result;

				}
			}
		}

		@Override
		public <T> long process(Processor<T> processor) throws IOException, InvocationTargetException
		{
			try (Session session = client.startSession())
			{
				try (Session.Command command = session.exec(this.command))
				{

					long result;
					try (InputStream stream = command.getInputStream())
					{
						result = processor.process(stream);
					}

					String error;
					try (InputStream stream = command.getErrorStream();
						ByteArrayOutputStream baos = IOUtils.readFully(stream))
					{
						error = new String(baos.toByteArray());
					}

					command.close();

					if (command.getExitStatus() != 0)
						throw new IOException(error.isBlank() ? "Error Status: " + command.getExitStatus() : error);
					return result;

				}
			}
		}

		@Override
		public <T> Stream<T> stream(Function<InputStream, Spliterator<T>> spliterator) throws IOException
		{
			Session session = null;
			Session.Command command = null;
			InputStream inputStream = null;

			try
			{
				Session _session = session = client.startSession();
				Session.Command _command = command = session.exec(this.command);
				InputStream _inputStream = inputStream = command.getInputStream();

				return StreamSupport.stream(spliterator.apply(inputStream), false).onClose(() ->
				{
					try
					{
						String error;
						try (InputStream stream = _command.getErrorStream();
							ByteArrayOutputStream baos = IOUtils.readFully(stream))
						{
							error = new String(baos.toByteArray());
						}

						_command.close();

						if (_command.getExitStatus() != 0)
							throw new IOException(error);
					} catch (IOException ex)
					{
						throw new UncheckedIOException(ex);
					} finally
					{
						IOUtils.closeQuietly(_inputStream, _command, _session);
					}
				});
			} catch (IOException | RuntimeException ex)
			{
				IOUtils.closeQuietly(inputStream, command, session);
				throw ex;
			}
		}
	}

}
