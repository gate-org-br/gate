package gate.producer;

import gate.type.Month;
import java.io.Serializable;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

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
