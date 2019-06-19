package gate.util;

import gate.error.AppException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConsoleParameters
{

	private final List<String> list = new ArrayList<>();
	private final Map<String, List<String>> map = new HashMap<>();

	private ConsoleParameters()
	{
	}

	public static ConsoleParameters parse(String[] args)
	{
		ConsoleParameters parameters = new ConsoleParameters();

		int i = 0;
		String name = null;

		while (i < args.length)
		{
			if (args[i].startsWith("-") || args[i].startsWith("--"))
			{
				name = args[i];
				parameters.map.putIfAbsent(name, new ArrayList<>());
			} else if (name != null)
				parameters.map.computeIfAbsent(name, e -> new ArrayList<>()).add(args[i]);
			else
				parameters.list.add(args[i]);

			i++;
		}
		return parameters;
	}

	public List<String> get()
	{
		return Collections.unmodifiableList(list);
	}

	public Optional<List<String>> get(String... names) throws AppException
	{
		if (Stream.of(names).noneMatch(e -> map.containsKey(e)))
			return Optional.empty();

		return Optional.of(Stream.of(names).filter(e -> map.containsKey(e))
			.map(e -> map.get(e)).flatMap(e -> e.stream())
			.collect(Collectors.toList()));
	}
}
