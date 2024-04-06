package gate.thymeleaf;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class Sequence
{

	private final AtomicInteger SEQUENCE = new AtomicInteger();

	public int next()
	{
		return SEQUENCE.incrementAndGet();
	}
}
