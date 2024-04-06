package gate.producer;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalTime;

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
