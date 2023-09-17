package gate.util;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class SecuritySessions
{

	private final long timeout;
	private final Map<String, SecuritySession> sessions = new ConcurrentHashMap<>();

	private SecuritySessions(long timeout)
	{
		this.timeout = timeout;
	}

	public static SecuritySessions of(long timeout)
	{
		return new SecuritySessions(timeout);
	}

	public SecuritySession create()
	{
		timeout();
		SecuritySession securitySession = SecuritySession.create();
		sessions.put(securitySession.state().toString(), securitySession);
		return securitySession;
	}

	public boolean check(String state, String nonce)
	{
		timeout();
		SecuritySession session = sessions.remove(state);
		return session != null && session.nonce().toString().equals(nonce);
	}

	public void timeout()
	{
		long timestamp = System.currentTimeMillis();
		sessions.entrySet().removeIf(e -> timestamp - e.getValue().state().timestamp() > timeout);
	}

	public static class SecuritySession
	{

		private final Token state;
		private final Token nonce;

		private static final AtomicLong sequencer = new AtomicLong();

		private SecuritySession(Token state, Token nonce)
		{
			this.state = state;
			this.nonce = nonce;
		}

		public static SecuritySession create()
		{
			long sequence = sequencer.incrementAndGet();
			long timestamp = System.currentTimeMillis();
			return new SecuritySession(Token.create(timestamp, sequence),
				Token.create(timestamp, sequence));
		}

		public Token state()
		{
			return state;
		}

		public Token nonce()
		{
			return nonce;
		}

		public static class Token
		{

			private final String uuid;
			private final long timestamp;
			private final long sequence;

			private Token(String uuid, long timestamp, long sequence)
			{
				this.uuid = uuid;
				this.timestamp = timestamp;
				this.sequence = sequence;
			}

			private static Token create(long timestamp, long sequence)
			{
				return new Token(UUID.randomUUID().toString(),
					timestamp, sequence);
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
}
