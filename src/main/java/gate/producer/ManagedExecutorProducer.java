package gate.producer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.context.ThreadContext;

public class ManagedExecutorProducer
{

	@Produces
	@ApplicationScoped
	public ManagedExecutor produce()
	{
		return ManagedExecutor.builder()
			.propagated(ThreadContext.ALL_REMAINING)
			.build();
	}

	public void dispose(@Disposes ManagedExecutor managedExecutor)
	{
		managedExecutor.shutdownNow();
	}
}
