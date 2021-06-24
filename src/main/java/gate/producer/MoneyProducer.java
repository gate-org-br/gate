package gate.producer;

import gate.type.Money;
import java.io.Serializable;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

/**
 *
 * @author davins
 *
 * Produces a ZERO Money object
 *
 */
public class MoneyProducer implements Serializable
{

	@Produces
	@Named("Money.ZERO")
	public Money produce()
	{
		return Money.ZERO;
	}
}
