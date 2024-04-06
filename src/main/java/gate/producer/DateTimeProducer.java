package gate.producer;

import gate.type.DateTime;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import java.io.Serializable;

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
