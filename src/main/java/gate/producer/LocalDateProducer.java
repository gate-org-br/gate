package gate.producer;

import java.io.Serializable;
import java.time.LocalDate;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

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
