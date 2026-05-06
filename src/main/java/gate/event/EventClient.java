package gate.event;

import gate.entity.User;
import gate.util.SystemProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.BeforeDestroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletOutputStream;
import org.slf4j.Logger;

import java.io.IOException;
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
	private ServletOutputStream out;
	private final AsyncContext asyncContext;

	public EventClient(User user, AsyncContext asyncContext)
	{
		this.subject = user;
		this.asyncContext = asyncContext;
		HeartbeatRegistry.register(this);
	}

	public synchronized boolean dispatch(Predicate<User> predicate, String event, String message)
	{
		if (!predicate.test(subject))
			return true;

		try
		{
			if (out == null)
				out = asyncContext.getResponse().getOutputStream();

			var data = Base64.getEncoder()
					.encodeToString(message.getBytes(StandardCharsets.UTF_8));

			out.println("event: " + event);
			out.println("data: " + data);
			out.println();
			out.flush();
			asyncContext.getResponse().flushBuffer();
			return true;
		} catch (IOException | RuntimeException ex)
		{
			close();
			return false;
		}
	}

	public boolean dispatch(String message) {return dispatch(e -> true, "message", message);}

	public boolean dispatch(String event, String message) {return dispatch(e -> true, event, message);}

	public boolean dispatch(Predicate<User> predicate, String message) {return dispatch(predicate, "message", message);}

	@Override
	public synchronized void close()
	{
		HeartbeatRegistry.unregister(this);
		if (out != null)
			try {out.close();} catch (Exception ignored) {}
		try {asyncContext.complete();} catch (Exception ignored) {}
	}

	synchronized boolean heartbeat()
	{
		try
		{
			if (out == null)
				out = asyncContext.getResponse().getOutputStream();
			out.println(": heartbeat");
			out.println();
			out.flush();
			return true;
		} catch (Exception e)
		{
			close();
			return false;
		}
	}

	@ApplicationScoped
	public static class HeartbeatRegistry
	{

		@Inject
		Logger logger;

		private static final Set<EventClient> TARGETS = Collections.newSetFromMap(new ConcurrentHashMap<>());
		private ScheduledExecutorService executor;

		private static final long HEARTBEAT = SystemProperty.get("gate.sse.heartbeat")
				.filter(e -> !e.isBlank())
				.filter(e -> e.chars().allMatch(Character::isDigit))
				.filter(e -> e.length() <= 10)
				.map(Long::parseLong)
				.orElse(20L);

		static void register(EventClient target) {TARGETS.add(target);}

		static void unregister(EventClient target) {TARGETS.remove(target);}

		void init(@Observes @Initialized(ApplicationScoped.class) Object event)
		{
			if (HEARTBEAT == 0)
				return;

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
				TARGETS.removeIf(target -> !target.heartbeat());
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