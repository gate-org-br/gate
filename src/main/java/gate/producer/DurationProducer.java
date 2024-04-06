package gate.producer;

import gate.type.Duration;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import java.io.Serializable;

/**
 *
 * @author davins
 *
 * Produces a ZERO duration object
 *
 */
public class DurationProducer implements Serializable
{

	private static final long serialVersionUID = 1L;

	@Produces
	@Named("Duration.ZERO")
	public Duration produce()
	{
		return Duration.ZERO;
	}
}
