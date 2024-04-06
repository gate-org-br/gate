package gate.producer;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author davins
 *
 * Produces LocalDateTime objects with current Date and Time
 */
public class LocalDateTimeProducer implements Serializable
{

	@Produces
	@Named("dateTime")
	public LocalDateTime dateTime()
	{
		return LocalDateTime.now();
	}
}
