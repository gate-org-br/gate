package gate;

import gate.lang.json.JsonElement;
import gate.lang.json.JsonObject;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import javax.websocket.CloseReason;
import javax.websocket.Session;
import org.slf4j.LoggerFactory;

public class Progress
{

	private static final int UNKNOWN = -1;
	private static final AtomicInteger SEQUENCE = new AtomicInteger();
	private static final ThreadLocal<Progress> CURRENT = new ThreadLocal<>();
	static final ConcurrentMap<Integer, Progress> INSTANCES = new ConcurrentHashMap<>();

	private String url;
	private JsonElement data;
	private long todo = UNKNOWN;
	private long done = UNKNOWN;
	private String text = "Aguarde";
	private Status status = Status.CREATED;
	private final int process = SEQUENCE.incrementAndGet();
	private final List<Session> sessions = new CopyOnWriteArrayList<>();

	public enum Status
	{
		CREATED, PENDING, COMMITED, CANCELED
	}

	Progress()
	{
	}

	int getProcess()
	{
		return process;
	}

	public void add(Session session)
	{
		session.getAsyncRemote().sendText(toString());
		if (url != null)
			session.getAsyncRemote()
				.sendText(new JsonObject()
					.setString("event", "Redirect")
					.setString("url", url).toString());
		sessions.add(session);
	}

	public void rem(Session session)
	{
		sessions.remove(session);
	}

	public void update(Status status, long todo,
		long done, String text, JsonElement data)
	{
		this.todo = todo;
		this.done = done;
		this.text = text;
		this.status = status;
		this.data = data;
	}

	public void update(Status status, long todo, long done, String text)
	{
		update(status, todo, done, text, null);
	}

	public void dispatch(String event)
	{
		sessions.forEach(e -> e.getAsyncRemote().sendText(event));
	}

	@Override
	public String toString()
	{
		return new JsonObject()
			.setLong("todo", todo)
			.setLong("done", done)
			.setString("text", text)
			.setInt("process", process)
			.setString("event", "Progress")
			.setString("status", status.name())
			.set("data", data)
			.toString();
	}

	static Progress create()
	{
		Progress progress = new Progress();
		CURRENT.set(progress);
		INSTANCES.put(progress.getProcess(), progress);
		return progress;
	}

	/**
	 * Initiates a new of task indeterminate size
	 *
	 * @param text description of the task being initiated
	 */
	public static void startup(String text)
	{
		Objects.requireNonNull(text);
		Progress progress = CURRENT.get();
		if (progress != null)
		{
			if (Status.COMMITED.equals(progress.status)
				|| Status.CANCELED.equals(progress.status))
				throw new IllegalStateException("Attempt to startup finished task");
			progress.update(Status.PENDING, UNKNOWN, 0, text);
			progress.dispatch(progress.toString());
		}
	}

	/**
	 * Initiates a new of task
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
			progress.update(Status.PENDING, todo, 0, text);
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
		{
			if (!Status.PENDING.equals(progress.status))
				throw new IllegalStateException("Attempt to update non pending task");
			progress.update(progress.status, progress.todo, progress.done + 1, progress.text);
			progress.dispatch(progress.toString());
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
		{
			progress.update(progress.status, progress.todo, progress.done, message);
			progress.dispatch(progress.toString());
		}
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
	 * Updates the progress of the current task.
	 *
	 * @param done new progress of the current task
	 */
	public static void update(long done)
	{
		Progress progress = CURRENT.get();
		if (progress != null)
		{
			if (!Status.PENDING.equals(progress.status))
				throw new IllegalStateException("Attempt to update non pending task");
			progress.update(progress.status, progress.todo, done, progress.text);
			progress.dispatch(progress.toString());
		}
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
		{
			if (!Status.PENDING.equals(progress.status))
				throw new IllegalStateException("Attempt to update non pending task");
			progress.update(progress.status, progress.todo, progress.done + 1, text);
			progress.dispatch(progress.toString());
		}
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
			progress.update(progress.status, progress.todo, done, text);
			progress.dispatch(progress.toString());
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
			progress.update(Status.COMMITED, progress.todo, progress.done, text);
			progress.dispatch(progress.toString());
		}
	}

	/**
	 * Conclude the task being executed
	 *
	 * @param text message indicating success
	 * @param data result of the operation
	 */
	public static void commit(String text, JsonElement data)
	{
		Objects.requireNonNull(text);
		Progress progress = CURRENT.get();
		if (progress != null)
		{
			if (!Status.PENDING.equals(progress.status))
				throw new IllegalStateException("Attempt to commit non pending task");
			progress.update(Status.COMMITED, progress.todo, progress.done, text, data);
			progress.dispatch(progress.toString());
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
		{
			progress.update(Status.CANCELED, progress.todo, progress.done, text);
			progress.dispatch(progress.toString());
		}
	}

	/**
	 * Obtains the the current task status.
	 *
	 * @return the current task status
	 */
	public static Status status()
	{
		Progress progress = CURRENT.get();
		if (progress != null)
			return progress.status;
		return null;
	}

	/**
	 * Obtains the current task size.
	 *
	 * @return the current task size
	 */
	public static long todo()
	{
		Progress progress = CURRENT.get();
		if (progress != null)
			return progress.todo;
		return -1;
	}

	/**
	 * Obtains the current task progress.
	 *
	 * @return the current task progress
	 */
	public static long done()
	{
		Progress progress = CURRENT.get();
		if (progress != null)
			return progress.done;
		return -1;
	}

	/**
	 * Obtains the current task text.
	 *
	 * @return the current task text
	 */
	public static String text()
	{
		Progress progress = CURRENT.get();
		if (progress != null)
			return progress.text;
		return null;
	}

	static void dispose()
	{
		Progress progress = CURRENT.get();
		if (progress != null)
		{
			progress.sessions.forEach(session ->
			{
				try
				{
					session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE,
						"Tarefa inexistente"));
				} catch (IOException ex)
				{
					LoggerFactory.getLogger(Progress.class).error(ex.getMessage(), ex);
				}
			});

			Progress.CURRENT.set(null);
			Progress.INSTANCES.remove(progress.getProcess(), progress);
		}
	}

	static void redirect(String url)
	{
		Progress progress = CURRENT.get();
		if (progress != null)
		{
			progress.url = url;
			progress.dispatch(new JsonObject()
				.setString("event", "Redirect")
				.setString("url", url).toString());

		}
	}

	public String getUrl()
	{
		return url;
	}
}
