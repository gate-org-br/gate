package gate.producer;

import gate.type.Duration;
import java.io.Serializable;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

/**
 *
 * @author davins
 *
 * Produces a ZERO duration object
 *
 */
@Dependent
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
