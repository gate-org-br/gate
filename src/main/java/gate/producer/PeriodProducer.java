package gate.producer;

import gate.type.Period;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import java.io.Serializable;

/**
 *
 * @author davins
 *
 * Produces a ZERO period object
 *
 */
public class PeriodProducer implements Serializable
{

	@Produces
	@Named("Period.ZERO")
	public Period produce()
	{
		return Period.ZERO;
	}
}
