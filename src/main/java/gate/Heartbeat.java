package gate;

import gate.util.SystemProperty;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
class Heartbeat
{
    @Inject
    Logger logger;

    @Inject
    SSEClients clients;

    private ScheduledExecutorService executor;
    private static final long HEARTBEAT = SystemProperty.get("gate.sse.heartbeat")
            .filter(e -> !e.isBlank())
            .filter(e -> e.chars().allMatch(Character::isDigit))
            .filter(e -> e.length() <= 10)
            .map(Long::parseLong)
            .orElse(20L);

    @PostConstruct
    void init()
    {
        if (HEARTBEAT == 0)
            return;

        executor = Executors.newSingleThreadScheduledExecutor(r ->
        {
            Thread thread = new Thread(r, "SSEHeartbeat");
            thread.setDaemon(true);
            return thread;
        });
        executor.scheduleAtFixedRate(this::tick, HEARTBEAT, HEARTBEAT, TimeUnit.SECONDS);
    }

    void tick()
    {
        try
        {
            clients.heartbeat();
            Progress.heartbeat();
        } catch (RuntimeException ex)
        {
            logger.error("Error trying to send heartbeats", ex);
        }
    }

    @PreDestroy
    void destroy()
    {
        if (executor != null)
            executor.shutdownNow();
    }
}
