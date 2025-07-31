package gate.producer;

import gate.Call;
import gate.base.Screen;
import gate.error.BadRequestException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.AmbiguousResolutionException;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import java.util.stream.Stream;
import gate.annotation.MainAction;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;

@ApplicationScoped
public class MainActionProducer
{

	@Inject
	private Instance<Screen> instances;

	private Call call;

	@PostConstruct
	public void build()
	{
		var methods = instances.stream()
			.map(e -> e.getClass())
			.map(e -> e.isSynthetic() ? e.getSuperclass() : e)
			.flatMap(e -> Stream.of(e.getDeclaredMethods()))
			.filter(e -> e.isAnnotationPresent(MainAction.class))
			.toList();

		if (methods.size() > 1)
			throw new AmbiguousResolutionException("Only a single screen of action can be the main one");

		call = !methods.isEmpty() ? Call.of(methods.get(0)) : Call.NONE;
	}

	@Produces
	@Dependent
	public Call produces() throws BadRequestException
	{

		return call;
	}
}
