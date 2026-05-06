package gate;

import gate.entity.User;
import gate.event.EventClient;
import gate.event.EventClients;
import gate.lang.json.JsonObject;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("resource")
public class Progress implements AutoCloseable
{
	private final User user;
	private final String uuid;
	private volatile State state = State.DEFAULT;
	private final EventClients clients = new EventClients();

	private static final int UNKNOWN = -1;
	private static final ThreadLocal<Progress> CURRENT = new ThreadLocal<>();
	private static final Map<String, Progress> INSTANCES = new ConcurrentHashMap<>();

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

	private Progress(String uuid, User user, EventClient client)
	{
		this.uuid = uuid;
		this.user = user;
		this.clients.subscribe(client);
	}

	private Progress update(Status status, long todo, long done, String text)
	{
		this.state = new State(status, todo, done, text);
		return this;
	}

	private void dispatch(String message) {clients.dispatch("Progress", message);}

	private void dispatch(String type, String message) {clients.dispatch(type, message);}

	@Override
	public synchronized void close()
	{
		dispatch("Finish", "Connection closed");
		clients.close();
		INSTANCES.remove(this.uuid);
		CURRENT.remove();
	}

	synchronized void attach(EventClient client)
	{
		clients.subscribe(client);
		dispatch("UUID", '"' + uuid + '"');
		client.dispatch("Progress", state.toString());
	}

	public synchronized void result(String contentType, String filename, String data)
	{
		dispatch("Result", new JsonObject()
				.setString("contentType", contentType)
				.setString("filename", filename)
				.setString("data", data)
				.toString());
	}

	public synchronized void redirect(String url)
	{
		dispatch("Redirect", new JsonObject()
				.setString("url", url)
				.toString());
	}

	synchronized void abort(String message)
	{
		State state = this.state;
		if (state.status == Status.PENDING || state.status == Status.CREATED)
			update(Status.CANCELED, state.todo, state.done, message);
		else
			update(state.status, state.todo, state.done, message);
		dispatch(this.toString());
		dispatch("Failure", new JsonObject()
				.setString("message", message)
				.toString());
	}

	public String uuid() {return uuid;}

	@Override
	public String toString() {return state.toString();}

	private static Optional<Progress> current() {return Optional.ofNullable(CURRENT.get());}

	/**
	 * Initiates a new task of indeterminate size.
	 *
	 * @param text description of the task being initiated
	 */
	public static void startup(String text) {startup(UNKNOWN, text);}

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
			if (Status.COMMITED.equals(state.status) || Status.CANCELED.equals(state.status))
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
			if (Status.PENDING != state.status && Status.DISCONNECTED != state.status)
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
			if (Status.PENDING != state.status && Status.DISCONNECTED != state.status)
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
		current().map(e -> e.state)
				.ifPresent(state -> update(state.done + 1, state.text));
	}

	/**
	 * Updates the progress of the current task.
	 *
	 * @param done new progress of the current task
	 */
	public static void update(long done)
	{
		current().map(e -> e.state)
				.ifPresent(state -> update(done, state.text));
	}

	/**
	 * Increments the progress of the current task.
	 *
	 * @param text description of the progress made
	 */
	public static void update(String text)
	{
		current().map(e -> e.state)
				.ifPresent(state -> update(state.done + 1, text));
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
			if (!Status.PENDING.equals(state.status) && !Status.DISCONNECTED.equals(state.status))
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
			if (!Status.PENDING.equals(state.status) && !Status.DISCONNECTED.equals(state.status))
				throw new IllegalStateException("Attempt to commit non pending task");
			progress.update(Status.COMMITED, state.todo, state.done, text);
			if (!Status.DISCONNECTED.equals(state.status))
				progress.dispatch(progress.toString());
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
			if (!Status.PENDING.equals(state.status) && !Status.DISCONNECTED.equals(state.status))
				throw new IllegalStateException("Attempt to cancel non pending task");
			progress.update(Status.CANCELED, state.todo, state.done, text);
			if (!Status.DISCONNECTED.equals(state.status))
				progress.dispatch(progress.toString());
		});
	}

	static Progress create(User user, EventClient client)
	{
		String uuid = UUID.randomUUID().toString();
		Progress progress = new Progress(uuid, user, client);
		INSTANCES.put(uuid, progress);
		progress.dispatch("UUID", '"' + uuid + '"');
		CURRENT.set(progress);
		return progress;
	}

	public static String UUID() {return current().map(Progress::uuid).orElse(null);}

	public static State state(User user, String uuid)
	{
		return Optional.ofNullable(INSTANCES.get(uuid))
				.filter(e -> Objects.equals(e.user, user))
				.map(e -> e.state)
				.orElse(State.UNKNOWN);
	}

	static void attach(User user, String uuid, EventClient client) throws IOException
	{
		var progress = Optional.ofNullable(INSTANCES.get(uuid))
				.filter(e -> Objects.equals(e.user, user))
				.orElseThrow(() -> new IOException("Unable to initialize progress-monitor"));
		progress.attach(client);
	}
}