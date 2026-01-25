package gate.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OneTimeValueStore
{

	private final long timeout;
	private final Map<String, Entry> entries = new ConcurrentHashMap<>();

	private OneTimeValueStore(long timeout)
	{
		this.timeout = timeout;
	}

	public static OneTimeValueStore of(long timeout)
	{
		return new OneTimeValueStore(timeout);
	}

	public void store(String key, String value)
	{
		timeout();
		entries.put(key, new Entry(value, System.currentTimeMillis()));
	}

	public String consume(String key)
	{
		timeout();
		Entry entry = entries.remove(key);
		return entry != null ? entry.value : null;
	}

	public void timeout()
	{
		long timestamp = System.currentTimeMillis();
		entries.entrySet().removeIf(e -> timestamp - e.getValue().timestamp > timeout);
	}

	private record Entry(String value, long timestamp)
		{

	}
}
