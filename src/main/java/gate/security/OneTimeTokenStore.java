package gate.security;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class OneTimeTokenStore
{

	private final long timeout;
	private final Map<String, Session> sessions = new ConcurrentHashMap<>();

	private OneTimeTokenStore(long timeout)
	{
		this.timeout = timeout;
	}

	public static OneTimeTokenStore of(long timeout)
	{
		return new OneTimeTokenStore(timeout);
	}

	public String create()
	{
		timeout();
		Session session = Session.create();
		String token = session.toString();
		sessions.put(token, session);
		return token;
	}

	public boolean consume(String token)
	{
		timeout();
		return sessions.remove(token) != null;
	}

	public void timeout()
	{
		long timestamp = System.currentTimeMillis();
		sessions.entrySet().removeIf(e -> timestamp - e.getValue().timestamp() > timeout);
	}

	public static class Session
	{

		private final String uuid;
		private final long timestamp;

		public static Session create()
		{
			return new Session(UUID.randomUUID().toString(),
				System.currentTimeMillis());
		}

		private Session(String uuid, long timestamp)
		{
			this.uuid = uuid;
			this.timestamp = timestamp;
		}

		public String uuid()
		{
			return uuid;
		}

		public long timestamp()
		{
			return timestamp;
		}

		@Override
		public String toString()
		{
			return String.format("%s-%d", uuid, timestamp);
		}
	}
}
