package gate;

import gate.entity.User;
import gate.lang.json.JsonObject;
import gate.type.ID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Progress implements Heartbeat
{

	private static final int UNKNOWN = -1;
	private static final ThreadLocal<Progress> CURRENT = new ThreadLocal<>();
	private static final Logger LOGGER = LoggerFactory.getLogger(Progress.class);
	private static final Map<String, Progress> INSTANCES = new ConcurrentHashMap<>();

	private final Writer writer;
	private final ID user;
	private final String uuid;
	private volatile State state = State.DEFAULT;

	public record State(Status status, long todo, long done, String text)
	{
		public static final State UNKNOWN = new State(Status.UNKNOWN, Progress.UNKNOWN, Progress.UNKNOWN, "");
		public static final State DEFAULT = new State(Status.CREATED, Progress.UNKNOWN, Progress.UNKNOWN, "");

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
	}

	public enum Status
	{
		CREATED, PENDING, COMMITED, CANCELED, DISCONNECTED, UNKNOWN
	}

	private Progress(ID user, Writer writer)
	{
		this.writer = writer;
		this.user = user;
		this.uuid = UUID.randomUUID().toString();
		INSTANCES.put(uuid, this);
	}

	private Progress update(Status status, long todo, long done, String text)
	{
		this.state = new State(status, todo, done, text);
		return this;
	}

	private synchronized void dispatch(String type, String message)
	{
		if (state.status() != Status.DISCONNECTED)
		{
			try
			{
				writer.write("event: %s\n".formatted(type));
				writer.write("data: " + Base64.getEncoder()
						.encodeToString(message.getBytes(StandardCharsets.UTF_8))
							 + "\n\n");
				writer.flush();
			} catch (IOException ex)
			{
				State state = this.state;
				update(Status.DISCONNECTED, state.todo, state.done, state.text);
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
		dispatch("close", "Connection closed by server");
	}

	public void result(String contentType, String filename, String data)
	{
		dispatch("Result", new JsonObject()
				.setString("contentType", contentType)
				.setString("filename", filename)
				.setString("data", data)
				.toString());
	}

	public void redirect(String url)
	{
		dispatch("Redirect", new JsonObject()
				.setString("url", url)
				.toString());
	}

	public String uuid()
	{
		return uuid;
	}

	void abort(String message)
	{
		State state = this.state;
		if (state.status() == Progress.Status.PENDING
			|| state.status() == Progress.Status.CREATED)
			update(Status.CANCELED, state.todo, state.done, message);
		else
			update(state.status, state.todo, state.done, message);
		dispatch(this.toString());

		dispatch("Failure", new JsonObject()
				.setString("message", message)
				.toString());
		close();
	}

	@Override
	public String toString()
	{
		return state.toString();
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
			State state = progress.state;
			if (Status.COMMITED.equals(state.status)
				|| Status.CANCELED.equals(state.status))
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
		current().ifPresent(progress ->
		{
			State state = progress.state;
			progress.update(state.status, state.todo, state.done, message)
					.dispatch(progress.toString());
		});
	}

	/**
	 * Increments the progress of the current task.
	 *
	 * @param step number of records to be processed before each notification
	 */
	public static void updateForEach(int step)
	{
		if (step <= 0)
			throw new IllegalArgumentException("Step must be a whole positive number");
		current().ifPresent(progress ->
		{
			State state = progress.state;
			if (!Status.PENDING.equals(state.status))
				throw new IllegalStateException("Attempt to update non pending task");
			progress.update(state.status, state.todo, state.done + 1, state.text);
			if (progress.state.done % step == 0)
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
			State state = progress.state;
			if (!Status.PENDING.equals(state.status))
				throw new IllegalStateException("Attempt to update non pending task");
			if (state.todo == UNKNOWN)
				throw new IllegalStateException("updatePercentage requires a known todo size");
			progress.update(state.status, state.todo, state.done + 1, state.text);
			state = progress.state;
			if (state.done % Math.max(Math.floorDiv(state.todo, 100), 1) == 0)
				progress.dispatch(progress.toString());
		});
	}

	/**
	 * Increments the progress of the current task.
	 */
	public static void update()
	{
		current().ifPresent(progress ->
		{
			State state = progress.state;
			update(state.done + 1, state.text);
		});
	}

	/**
	 * Updates the progress of the current task.
	 *
	 * @param done new progress of the current task
	 */
	public static void update(long done)
	{
		current().ifPresent(progress ->
		{
			State state = progress.state;
			update(done, state.text);
		});
	}

	/**
	 * Increments the progress of the current task.
	 *
	 * @param text description of the progress made
	 */
	public static void update(String text)
	{
		current().ifPresent(progress ->
		{
			State state = progress.state;
			update(state.done + 1, text);
		});
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
			State state = progress.state;
			if (!Status.PENDING.equals(state.status))
				throw new IllegalStateException("Attempt to update non pending task");
			progress.update(state.status, state.todo, done, text)
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
			State state = progress.state;
			if (!Status.PENDING.equals(state.status))
				throw new IllegalStateException("Attempt to commit non pending task");
			progress.update(Status.COMMITED, state.todo, state.done, text)
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
			State state = progress.state;
			if (!Status.PENDING.equals(state.status))
				throw new IllegalStateException("Attempt to cancel non pending task");
			progress.update(Status.CANCELED, state.todo, state.done, text)
					.dispatch(progress.toString());
		});
	}

	static Progress create(User user, Writer writer)
	{
		ID id = user != null && user.getId() != null
				? user.getId() : ID.valueOf(0);
		Progress progress = new Progress(id, writer);
		progress.dispatch("UUID", progress.uuid);
		CURRENT.set(progress);
		return progress;
	}

	public static State get(ID user, String uuid)
	{
		return Optional.ofNullable(INSTANCES.get(uuid))
				.filter(e -> Objects.equals(e.user, user))
				.map(e -> e.state)
				.orElse(State.UNKNOWN);
	}

	public static String UUID()
	{
		return current().map(Progress::uuid)
				.orElse(null);
	}

	static Progress create(Writer writer)
	{
		return create(null, writer);
	}

	static void finish()
	{
		current().ifPresent(progress -> INSTANCES.remove(progress.uuid));
		CURRENT.remove();
	}

	@Override
	public synchronized boolean heartbeat()
	{
		State state = this.state;
		if (state.status == Status.COMMITED
			|| state.status == Status.CANCELED
			|| state.status == Status.DISCONNECTED)
			return false;

		try
		{
			writer.write(": heartbeat\n\n");
			writer.flush();
			return true;
		} catch (IOException ex)
		{
			update(Status.DISCONNECTED, state.todo, state.done, state.text);
			return false;
		}
	}
}