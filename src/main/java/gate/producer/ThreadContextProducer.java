package gate.producer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.context.ThreadContext;

@ApplicationScoped
public class ThreadContextProducer
{

	@Produces
	public ThreadContext produceThreadContext()
	{
		return ThreadContext.builder()
			.propagated(ThreadContext.ALL_REMAINING)
			.build();
	}
}
