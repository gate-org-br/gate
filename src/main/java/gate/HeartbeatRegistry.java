package gate;

import gate.util.SystemProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.BeforeDestroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
class HeartbeatRegistry
{

    @Inject
    Logger logger;

    private final Set<Heartbeat> targets = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private ScheduledExecutorService executor;

    private static final long HEARTBEAT = SystemProperty.get("gate.sse.heartbeat")
            .filter(e -> !e.isBlank())
            .filter(e -> e.chars().allMatch(Character::isDigit))
            .filter(e -> e.length() <= 10)
            .map(Long::parseLong)
            .orElse(20L);

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

    void register(Heartbeat target)
    {
        targets.add(target);
    }

    void unregister(Heartbeat target)
    {
        targets.remove(target);
    }

    void tick()
    {
        try
        {
            targets.removeIf(target -> !target.heartbeat());
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
