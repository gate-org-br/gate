package gate.producer;

import gate.Request;
import gate.annotation.Current;
import gate.entity.User;
import gate.error.InvalidCredentialsException;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

public class UserProducer
{

	@Current
	@Produces
	@Named(value = "user")
	public User getUser() throws InvalidCredentialsException
	{
		return Request.get() != null
				? Request.get().getUser()
						.orElseGet(User::new) : new User();
	}
}
