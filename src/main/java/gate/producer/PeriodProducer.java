package gate.producer;

import gate.type.Period;
import java.io.Serializable;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

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
