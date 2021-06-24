package gate.producer;

import java.io.Serializable;
import java.time.LocalTime;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

/**
 * @author davins
 *
 * Produces LocalTime objects with current Time
 */
public class LocalTimeProducer implements Serializable
{

	@Produces
	@Named("time")
	public LocalTime dateTime()
	{
		return LocalTime.now();
	}
}
