package gate.producer;

import gate.type.Money;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import java.io.Serializable;

/**
 *
 * @author davins
 *
 * Produces a ZERO Money object
 *
 */
@ApplicationScoped
public class MoneyProducer implements Serializable
{

	@Produces
	@Named("Money.ZERO")
	public Money produce()
	{
		return Money.ZERO;
	}
}
