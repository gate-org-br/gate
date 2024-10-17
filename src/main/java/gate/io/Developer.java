package gate.io;

import gate.GateControl;
import gate.entity.User;
import gate.error.HierarchyException;
import gate.error.InvalidUsernameException;
import gate.util.SystemProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;

@ApplicationScoped
public class Developer
{

	@Inject
	GateControl control;

	private final String developer
			= SystemProperty.get("gate.developer").orElse(null);

	private Developer()
	{

	}

	public Optional<User> get()
			throws InvalidUsernameException, HierarchyException
	{
		return developer != null
				? Optional.of(control.select(developer))
				: Optional.empty();
	}
}
