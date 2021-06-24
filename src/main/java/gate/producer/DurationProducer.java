package gate.producer;

import gate.type.Duration;
import java.io.Serializable;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

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
