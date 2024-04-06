package gate.producer;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author davins
 *
 * Produces LocalDate objects with current Date
 */
public class LocalDateProducer implements Serializable
{

	@Produces
	@Named("date")
	public LocalDate dateTime()
	{
		return LocalDate.now();
	}
}
