package gate.thymeleaf;

import java.util.concurrent.atomic.AtomicInteger;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Sequence
{

	private final AtomicInteger SEQUENCE = new AtomicInteger();

	public int next()
	{
		return SEQUENCE.incrementAndGet();
	}
}
