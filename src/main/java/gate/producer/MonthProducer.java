package gate.producer;

import gate.type.Month;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import java.io.Serializable;

/**
 *
 * @author davins
 *
 * Produces a Month object with current Month
 *
 */
public class MonthProducer implements Serializable
{

	@Produces
	@Named("month")
	public Month produce()
	{
		return Month.now();
	}
}
