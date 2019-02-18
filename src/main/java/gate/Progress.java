package gate;

import gate.entity.App;
import gate.entity.Org;
import gate.entity.User;
import gate.lang.json.JsonObject;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.CloseReason;
import javax.websocket.Session;

public class Progress
{

	private static final int UNKNOWN = -1;
	private static final AtomicInteger SEQUENCE = new AtomicInteger();
	private static final ThreadLocal<Progress> CURRENT = new ThreadLocal<Progress>();
	static final ConcurrentMap<Integer, Progress> INSTANCES = new ConcurrentHashMap<>();

	private String url;
	private int todo = UNKNOWN;
	private int done = UNKNOWN;
	private String text = "Aguarde";
	private Status status = Status.CREATED;
	private final int process = SEQUENCE.incrementAndGet();
	private final List<Session> sessions = new CopyOnWriteArrayList<>();

	private final Org org;
	private final App app;
	private final User user;

	public enum Status
	{
		CREATED, PENDING, COMMITED, CANCELED
	}

	Progress(Org org, App app, User user)
	{
		this.org = org;
		this.app = app;
		this.user = user;
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

	public void update(Status status, int todo, int done, String text)
	{
		this.todo = todo;
		this.done = done;
		this.text = text;
		this.status = status;
	}

	public void dispatch(String event)
	{
		sessions.forEach(e -> e.getAsyncRemote().sendText(event));
	}

	@Override
	public String toString()
	{
		return new JsonObject()
			.setInt("todo", todo)
			.setInt("done", done)
			.setString("text", text)
			.setInt("process", process)
			.setString("event", "Progress")
			.setString("status", status.name()).toString();
	}

	static void bind(Progress progress)
	{
		CURRENT.set(progress);
	}

	static Progress create(Org org, App app, User user)
	{
		Progress progress = new Progress(org, app, user);
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
	public static void startup(int todo, String text)
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
	public static void update(int done)
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
	public static void update(int done, String text)
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
			if (!Status.PENDING.equals(progress.status))
				throw new IllegalStateException("Attempt to cancel non pending task");
			progress.update(Status.CANCELED, progress.todo, progress.done, text);
			progress.dispatch(progress.toString());
		}
	}

	/**
	 * Obtains the status of the current task.
	 *
	 * @return the status of the current task
	 */
	public static Status status()
	{
		Progress progress = CURRENT.get();
		if (progress != null)
			return progress.status;
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
					Logger.getLogger(Progress.class.getName()).log(Level.SEVERE, null, ex);
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

	public static Org getOrg()
	{
		Progress progress = CURRENT.get();
		if (progress != null)
			return progress.org;
		return null;
	}

	public static App getApp()
	{
		Progress progress = CURRENT.get();
		if (progress != null)
			return progress.app;
		return null;
	}

	public static User getUser()
	{
		Progress progress = CURRENT.get();
		if (progress != null)
			return progress.user;
		return null;
	}
}
