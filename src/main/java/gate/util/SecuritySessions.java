package gate.util;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class SecuritySessions
{

	private final long timeout;
	private final Map<String, Session> sessions = new ConcurrentHashMap<>();

	private SecuritySessions(long timeout)
	{
		this.timeout = timeout;
	}

	public static SecuritySessions of(long timeout)
	{
		return new SecuritySessions(timeout);
	}

	public String create()
	{
		timeout();
		Session session = Session.create();
		String token = session.toString();
		sessions.put(token, session);
		return token;
	}

	public boolean check(String token)
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
		private final long sequence;
		private final static AtomicLong SEQUENCER = new AtomicLong();

		public static Session create()
		{
			return new Session(UUID.randomUUID().toString(),
				System.currentTimeMillis(), SEQUENCER.incrementAndGet());
		}

		private Session(String uuid, long timestamp, long sequence)
		{
			this.uuid = uuid;
			this.timestamp = timestamp;
			this.sequence = sequence;
		}

		public String uuid()
		{
			return uuid;
		}

		public long timestamp()
		{
			return timestamp;
		}

		public long sequence()
		{
			return sequence;
		}

		@Override
		public String toString()
		{
			return String.format("%s-%d-%d", uuid, timestamp, sequence);
		}
	}
}
