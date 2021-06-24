package gate.producer;

import gate.type.DateTime;
import java.io.Serializable;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

/**
 * @author davins
 *
 * Produces DateTime objects with current Date and Time
 */
public class DateTimeProducer implements Serializable
{

	@Produces
	@Named("now")
	public DateTime now()
	{
		return DateTime.now();
	}
}
