package gate;

import gate.entity.User;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonObject;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Progress
{

	private static final int UNKNOWN = -1;
	private static final ThreadLocal<Progress> CURRENT = new ThreadLocal<>();

	private JsonElement data;
	private long todo = UNKNOWN;
	private long done = UNKNOWN;
	private final Writer writer;
	private String text = "Aguarde";
	private Status status = Status.CREATED;

	public enum Status
	{
		CREATED, PENDING, COMMITED, CANCELED, DISCONNECTED
	}

	private Progress(User user, Writer writer)
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

	private void dispatch(String message)
	{
		if (this.status != Status.DISCONNECTED)
		{
			try
			{
				writer.write("event: Progress\n");
				writer.write("data: " + Base64.getEncoder().encodeToString(message
					.getBytes(Charset.forName("UTF-8"))) + "\n\n");
				writer.flush();
			} catch (IOException ex)
			{
				this.status = Status.DISCONNECTED;
				Logger.getLogger(Progress.class.getName())
					.log(Level.INFO, null, ex);
			}
		}
	}

	public void close()
	{
		if (this.status != Status.DISCONNECTED)
		{
			try
			{
				writer.write("event: close\nConnection closed by server");
				writer.flush();
			} catch (IOException ex)
			{
				this.status = Status.DISCONNECTED;
				Logger.getLogger(Progress.class.getName())
					.log(Level.INFO, null, ex);
			}
		}
	}

	static Progress create(User user, Writer writer)
	{
		Progress progress = new Progress(user, writer);
		CURRENT.set(progress);
		return progress;
	}

	public void result(String contentType,
		String filename,
		String data)
	{
		dispatch(new JsonObject()
			.setString("event", "Result")
			.setString("contentType", contentType)
			.setString("filename", filename)
			.setString("data", data)
			.toString());
	}

	void abort(String message)
	{
		if (status == Progress.Status.PENDING
			|| status == Progress.Status.CREATED)
			update(Status.CANCELED, todo, done, text);
		else
			update(status, todo, done, message);
		dispatch(this.toString());
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
			.set("data", data)
			.toString();
	}

	/**
	 * Initiates a new of task indeterminate size.
	 *
	 * @param text description of the task being initiated
	 */
	public static void startup(String text)
	{
		startup(UNKNOWN, text);
	}

	/**
	 * Initiates a new of task.
	 *
	 * @param todo size of the task being initiated
	 * @param text description of the task being initiated
	 */
	public static void startup(long todo, String text)
	{
		Objects.requireNonNull(text);
		Progress progress = CURRENT.get();
		if (progress != null)
		{
			if (Status.COMMITED.equals(progress.status)
				|| Status.CANCELED.equals(progress.status))
				throw new IllegalStateException("Attempt to startup finished task");
			progress.update(Status.PENDING, todo, 0, text)
				.dispatch(progress.toString());
		}
	}

	/**
	 * Display a message without updating progress.
	 *
	 * @param message message to be displayed
	 */
	public static void message(String message)
	{
		Progress progress = CURRENT.get();
		if (progress != null)
			progress.update(progress.status, progress.todo, progress.done, message)
				.dispatch(progress.toString());
	}

	/**
	 * Increments the progress of the current task.
	 *
	 * @param step number of records to be processed before each notification
	 */
	public static void updateForEach(int step)
	{
		Progress progress = CURRENT.get();
		if (progress != null)
		{
			if (!Status.PENDING.equals(progress.status))
				throw new IllegalStateException("Attempt to update non pending task");
			progress.update(progress.status, progress.todo, progress.done + 1, progress.text);
			if (progress.done % step == 0)
				progress.dispatch(progress.toString());
		}
	}

	/**
	 * Increments the progress for each one percent.
	 */
	public static void updatePercentage()
	{
		Progress progress = CURRENT.get();
		if (progress != null)
		{
			if (!Status.PENDING.equals(progress.status))
				throw new IllegalStateException("Attempt to update non pending task");
			progress.update(progress.status, progress.todo, progress.done + 1, progress.text);
			if (progress.done % Math.max(Math.floorDiv(progress.todo, 100), 1) == 0)
				progress.dispatch(progress.toString());
		}
	}

	/**
	 * Increments the progress of the current task.
	 */
	public static void update()
	{
		Progress progress = CURRENT.get();
		if (progress != null)
			update(progress.done + 1, progress.text);
	}

	/**
	 * Updates the progress of the current task.
	 *
	 * @param done new progress of the current task
	 */
	public static void update(long done)
	{
		Progress progress = CURRENT.get();
		if (progress != null)
			update(done, progress.text);
	}

	/**
	 * Increments the progress of the current task.
	 *
	 * @param text description of the progress made
	 */
	public static void update(String text)
	{
		Progress progress = CURRENT.get();
		if (progress != null)
			update(progress.done + 1, text);
	}

	/**
	 * Increments the progress of the current task.
	 *
	 * @param done new progress of the current task
	 * @param text description of the progress made
	 */
	public static void update(long done, String text)
	{
		Progress progress = CURRENT.get();
		if (progress != null)
		{
			if (!Status.PENDING.equals(progress.status))
				throw new IllegalStateException("Attempt to update non pending task");
			progress.update(progress.status, progress.todo, done, text)
				.dispatch(progress.toString());
		}
	}

	/**
	 * Conclude the task being executed
	 *
	 * @param text message indicating success
	 */
	public static void commit(String text)
	{
		Objects.requireNonNull(text);
		Progress progress = CURRENT.get();
		if (progress != null)
		{
			if (!Status.PENDING.equals(progress.status))
				throw new IllegalStateException("Attempt to commit non pending task");
			progress.update(Status.COMMITED, progress.todo, progress.done, text)
				.dispatch(progress.toString());
		}
	}

	/**
	 * Cancel the task being executed
	 *
	 * @param text reason for the cancellation
	 */
	public static void cancel(String text)
	{
		Objects.requireNonNull(text);
		Progress progress = CURRENT.get();
		if (progress != null)
			progress.update(Status.CANCELED, progress.todo, progress.done, text)
				.dispatch(progress.toString());
	}
}
