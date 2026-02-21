package gate;

import gate.lang.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Progress
{

	private static final int UNKNOWN = -1;
	private static final ThreadLocal<Progress> CURRENT = new ThreadLocal<>();
	private static final Logger LOGGER = LoggerFactory.getLogger(Progress.class);
	private static final Set<Progress> INSTANCES = Collections.newSetFromMap(new ConcurrentHashMap<>());

	private long todo = UNKNOWN;
	private long done = UNKNOWN;
	private final Writer writer;
	private String text = "Aguarde";
	private volatile Status status = Status.CREATED;

	public enum Status
	{
		CREATED, PENDING, COMMITED, CANCELED, DISCONNECTED
	}

	private Progress(Writer writer)
	{
		this.writer = writer;
	}

	private Progress update(Status status, long todo,
							long done, String text)
	{
		this.todo = todo;
		this.done = done;
		this.text = text;
		this.status = status;
		return this;
	}

	private void dispatch(String type, String message)
	{
		if (this.status != Status.DISCONNECTED)
		{
			try
			{
				writer.write("event: %s\n".formatted(type));
				writer.write("data: " + Base64.getEncoder()
						.encodeToString(message
												.getBytes(StandardCharsets.UTF_8))
									 + "\n\n");
				writer.flush();
			} catch (IOException ex)
			{
				this.status = Status.DISCONNECTED;
				LOGGER.info(ex.getMessage(), ex);
			}
		}
	}

	private void dispatch(String message)
	{
		dispatch("Progress", message);
	}

	public void close()
	{
		if (this.status != Status.DISCONNECTED)
		{
			try
			{
				writer.write("event: close\n");
				writer.write("data: Connection closed by server\n\n");
				writer.flush();
			} catch (IOException ex)
			{
				this.status = Status.DISCONNECTED;
				LOGGER.info(ex.getMessage(), ex);
			}
		}
	}

	public void result(String contentType,
					   String filename,
					   String data)
	{
		dispatch("Result", new JsonObject()
				.setString("contentType", contentType)
				.setString("filename", filename)
				.setString("data", data)
				.toString());
	}

	void abort(String message)
	{
		if (status == Progress.Status.PENDING
				|| status == Progress.Status.CREATED)
			update(Status.CANCELED, todo, done, message);
		else
			update(status, todo, done, message);
		dispatch(this.toString());

		dispatch("Failure", new JsonObject()
				.setString("message", message)
				.toString());
		close();
	}

	@Override
	public String toString()
	{
		return new JsonObject()
				.setLong("todo", todo)
				.setLong("done", done)
				.setString("text", text)
				.setString("event", "Progress")
				.setString("status", status.name())
				.toString();
	}

	private static Optional<Progress> current()
	{
		return Optional.ofNullable(CURRENT.get());
	}

	/**
	 * Initiates a new task of indeterminate size.
	 *
	 * @param text description of the task being initiated
	 */
	public static void startup(String text)
	{
		startup(UNKNOWN, text);
	}

	/**
	 * Initiates a new task.
	 *
	 * @param todo size of the task being initiated
	 * @param text description of the task being initiated
	 */
	public static void startup(long todo, String text)
	{
		Objects.requireNonNull(text);
		current().ifPresent(progress ->
							{
								if (Status.COMMITED.equals(progress.status)
										|| Status.CANCELED.equals(progress.status))
									throw new IllegalStateException("Attempt to startup finished task");
								progress.update(Status.PENDING, todo, 0, text)
										.dispatch(progress.toString());
							});
	}

	/**
	 * Display a message without updating progress.
	 *
	 * @param message message to be displayed
	 */
	public static void message(String message)
	{
		current().ifPresent(progress -> progress.update(progress.status,
														progress.todo,
														progress.done,
														message).dispatch(progress.toString()));
	}

	/**
	 * Increments the progress of the current task.
	 *
	 * @param step number of records to be processed before each notification
	 */
	public static void updateForEach(int step)
	{
		current().ifPresent(progress ->
							{
								if (!Status.PENDING.equals(progress.status))
									throw new IllegalStateException("Attempt to update non pending task");
								progress.update(progress.status, progress.todo, progress.done + 1, progress.text);
								if (progress.done % step == 0)
									progress.dispatch(progress.toString());
							});
	}

	/**
	 * Increments the progress for each one percent.
	 */
	public static void updatePercentage()
	{
		current().ifPresent(progress ->
							{
								if (!Status.PENDING.equals(progress.status))
									throw new IllegalStateException("Attempt to update non pending task");
								if (progress.todo == UNKNOWN)
									throw new IllegalStateException("updatePercentage requires a known todo size");
								progress.update(progress.status, progress.todo, progress.done + 1, progress.text);
								if (progress.done % Math.max(Math.floorDiv(progress.todo, 100), 1) == 0)
									progress.dispatch(progress.toString());
							});
	}

	/**
	 * Increments the progress of the current task.
	 */
	public static void update()
	{
		current().ifPresent(progress -> update(progress.done + 1, progress.text));
	}

	/**
	 * Updates the progress of the current task.
	 *
	 * @param done new progress of the current task
	 */
	public static void update(long done)
	{
		current().ifPresent(progress -> update(done, progress.text));
	}

	/**
	 * Increments the progress of the current task.
	 *
	 * @param text description of the progress made
	 */
	public static void update(String text)
	{
		current().ifPresent(progress -> update(progress.done + 1, text));
	}

	/**
	 * Increments the progress of the current task.
	 *
	 * @param done new progress of the current task
	 * @param text description of the progress made
	 */
	public static void update(long done, String text)
	{
		current().ifPresent(progress ->
							{
								if (!Status.PENDING.equals(progress.status))
									throw new IllegalStateException("Attempt to update non pending task");
								progress.update(progress.status, progress.todo, done, text)
										.dispatch(progress.toString());
							});
	}

	/**
	 * Conclude the task being executed
	 *
	 * @param text message indicating success
	 */
	public static void commit(String text)
	{
		Objects.requireNonNull(text);
		current().ifPresent(progress ->
							{
								if (!Status.PENDING.equals(progress.status))
									throw new IllegalStateException("Attempt to commit non pending task");
								progress.update(Status.COMMITED, progress.todo, progress.done, text)
										.dispatch(progress.toString());
							});
	}

	/**
	 * Cancel the task being executed
	 *
	 * @param text reason for the cancellation
	 */
	public static void cancel(String text)
	{
		Objects.requireNonNull(text);
		current().ifPresent(progress ->
							{
								if (!Status.PENDING.equals(progress.status))
									throw new IllegalStateException("Attempt to commit non pending task");
								progress.update(Status.CANCELED, progress.todo, progress.done, text)
										.dispatch(progress.toString());
							});
	}

	static void heartbeat()
	{
		INSTANCES.removeIf(progress ->
						   {
							   if (progress.status == Status.COMMITED
									   || progress.status == Status.CANCELED
									   || progress.status == Status.DISCONNECTED)
								   return true;

							   try
							   {
								   progress.writer.write(": heartbeat\n\n");
								   progress.writer.flush();
								   return false;
							   } catch (IOException e)
							   {
								   progress.status = Status.DISCONNECTED;
								   return true;
							   }
						   });
	}

	static Progress create(Writer writer)
	{
		Progress progress = new Progress(writer);
		INSTANCES.add(progress);
		CURRENT.set(progress);
		return progress;
	}

	static void finish()
	{
		INSTANCES.remove(CURRENT.get());
		CURRENT.remove();
	}
}
