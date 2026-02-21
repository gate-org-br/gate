package gate;

import gate.lang.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

public class Progress implements Pinger
{

    private static final int UNKNOWN = -1;
    private static final ThreadLocal<Progress> CURRENT = new ThreadLocal<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(Progress.class);

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

    private synchronized void dispatch(String type, String message)
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

    public synchronized void close()
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

    public void redirect(String url)
    {
        dispatch("Redirect", new JsonObject()
                .setString("url", url)
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

    private static Progress current()
    {
        Progress progress = CURRENT.get();
        if (progress == null)
            throw new IllegalStateException(
                    "Progress not bound to current thread");
        return progress;
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
        Progress progress = current();
        if (Status.COMMITED.equals(progress.status)
                || Status.CANCELED.equals(progress.status))
            throw new IllegalStateException("Attempt to startup finished task");
        progress.update(Status.PENDING, todo, 0, text)
                .dispatch(progress.toString());
    }

    /**
     * Display a message without updating progress.
     *
     * @param message message to be displayed
     */
    public static void message(String message)
    {
        Progress progress = current();
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
        Progress progress = current();
        if (!Status.PENDING.equals(progress.status))
            throw new IllegalStateException("Attempt to update non pending task");
        progress.update(progress.status, progress.todo, progress.done + 1, progress.text);
        if (progress.done % step == 0)
            progress.dispatch(progress.toString());
    }

    /**
     * Increments the progress for each one percent.
     */
    public static void updatePercentage()
    {
        Progress progress = current();
        if (!Status.PENDING.equals(progress.status))
            throw new IllegalStateException("Attempt to update non pending task");
        if (progress.todo == UNKNOWN)
            throw new IllegalStateException("updatePercentage requires a known todo size");
        progress.update(progress.status, progress.todo, progress.done + 1, progress.text);
        if (progress.done % Math.max(Math.floorDiv(progress.todo, 100), 1) == 0)
            progress.dispatch(progress.toString());
    }

    /**
     * Increments the progress of the current task.
     */
    public static void update()
    {
        Progress progress = current();
        update(progress.done + 1, progress.text);
    }

    /**
     * Updates the progress of the current task.
     *
     * @param done new progress of the current task
     */
    public static void update(long done)
    {
        Progress progress = current();
        update(done, progress.text);
    }

    /**
     * Increments the progress of the current task.
     *
     * @param text description of the progress made
     */
    public static void update(String text)
    {
        Progress progress = current();
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
        Progress progress = current();
        if (!Status.PENDING.equals(progress.status))
            throw new IllegalStateException("Attempt to update non pending task");
        progress.update(progress.status, progress.todo, done, text)
                .dispatch(progress.toString());
    }

    /**
     * Conclude the task being executed
     *
     * @param text message indicating success
     */
    public static void commit(String text)
    {
        Objects.requireNonNull(text);
        Progress progress = current();
        if (!Status.PENDING.equals(progress.status))
            throw new IllegalStateException("Attempt to commit non pending task");
        progress.update(Status.COMMITED, progress.todo, progress.done, text)
                .dispatch(progress.toString());
    }

    /**
     * Cancel the task being executed
     *
     * @param text reason for the cancellation
     */
    public static void cancel(String text)
    {
        Objects.requireNonNull(text);
        Progress progress = current();
        if (!Status.PENDING.equals(progress.status))
            throw new IllegalStateException("Attempt to cancel non pending task");
        progress.update(Status.CANCELED, progress.todo, progress.done, text)
                .dispatch(progress.toString());
    }

    static Progress create(Writer writer)
    {
        Progress progress = new Progress(writer);
        CURRENT.set(progress);
        return progress;
    }

    static void finish()
    {
        CURRENT.remove();
    }

    @Override
    public synchronized boolean ping()
    {
        if (status == Status.COMMITED
                || status == Status.CANCELED
                || status == Status.DISCONNECTED)
            return false;

        try
        {
            writer.write(": ping\n\n");
            writer.flush();
            return true;
        } catch (IOException ex)
        {
            status = Status.DISCONNECTED;
            return false;
        }
    }
}
