package gate.event;

import gate.entity.User;
import gate.lang.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.BeforeDestroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletOutputStream;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class EventClient implements AutoCloseable
{
	private final User subject;
	private final AsyncContext asyncContext;
	private ServletOutputStream out;
	private volatile boolean closed = false;
	private volatile boolean connected = true;

	public EventClient(AsyncContext asyncContext, User user)
	{
		this.subject = user;
		this.asyncContext = asyncContext;
		HeartbeatRegistry.register(this);
	}

	public boolean dispatch(Predicate<User> predicate, String event, JsonObject message)
	{
		if (!predicate.test(subject))
			return true;
		var data = Base64.getEncoder()
				.encodeToString(message.toString()
						.getBytes(StandardCharsets.UTF_8));
		return send("""
				event: %s
				data: %s""".formatted(event, data));
	}

	public boolean dispatch(JsonObject message) {return dispatch(e -> true, "message", message);}

	public boolean dispatch(String event, JsonObject message) {return dispatch(e -> true, event, message);}

	public boolean dispatch(Predicate<User> predicate, JsonObject message) {return dispatch(predicate, "message", message);}

	@Override
	public synchronized void close()
	{
		if (closed)
			return;

		closed = true;
		HeartbeatRegistry.unregister(this);
		if (out != null)
			try {out.close();} catch (Exception ignored) {}
		try {asyncContext.complete();} catch (Exception ignored) {}
	}

	private synchronized boolean send(String message)
	{
		if (!connected)
			return false;

		try
		{
			if (out == null)
				out = asyncContext.getResponse().getOutputStream();
			out.println(message);
			out.println();
			out.flush();
			asyncContext.getResponse().flushBuffer();
			return true;
		} catch (Exception e) {return connected = false;}
	}

	public boolean isClosed() {return closed;}

	@ApplicationScoped
	public static class HeartbeatRegistry
	{

		private static final Set<EventClient> TARGETS =
				Collections.newSetFromMap(new ConcurrentHashMap<>());
		private static final long HEARTBEAT = 20L;
		@Inject
		Logger logger;
		private ScheduledExecutorService executor;

		static void register(EventClient target) {TARGETS.add(target);}

		static void unregister(EventClient target) {TARGETS.remove(target);}

		void init(@Observes @Initialized(ApplicationScoped.class) Object event)
		{
			executor = Executors.newSingleThreadScheduledExecutor(r ->
			{
				Thread thread = new Thread(r, "Heartbeat");
				thread.setDaemon(true);
				return thread;
			});
			executor.scheduleAtFixedRate(this::tick, HEARTBEAT, HEARTBEAT, TimeUnit.SECONDS);
		}

		void tick()
		{
			try
			{
				TARGETS.removeIf(target -> !target.send(": heartbeat"));
			} catch (RuntimeException ex)
			{
				logger.error("Error trying to send heartbeats", ex);
			}
		}

		void destroy(@Observes @BeforeDestroyed(ApplicationScoped.class) Object event)
		{
			if (executor != null)
				executor.shutdownNow();
		}
	}
}