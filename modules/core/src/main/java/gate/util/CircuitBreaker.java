package gate.util;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class CircuitBreaker
{
	@FunctionalInterface
	public interface Action<T>
	{
		T execute() throws IOException, InterruptedException;
	}

	private final int failureThreshold;
	private final int maxRetries;
	private final int retryDelayMs;
	private final Duration openDuration;

	public enum State
	{CLOSED, OPEN, HALF_OPEN}

	private final AtomicInteger failures = new AtomicInteger(0);
	private volatile Instant openedAt = Instant.MIN;

	private CircuitBreaker(Builder builder)
	{
		this.failureThreshold = builder.failureThreshold;
		this.maxRetries = builder.maxRetries;
		this.retryDelayMs = builder.retryDelayMs;
		this.openDuration = builder.openDuration;
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public State getState()
	{
		if (failures.get() < failureThreshold)
			return State.CLOSED;
		if (Duration.between(openedAt, Instant.now()).compareTo(openDuration) <= 0)
			return State.OPEN;
		return State.HALF_OPEN;
	}

	public <T> T call(Action<T> action) throws IOException, InterruptedException
	{
		if (getState() == State.OPEN)
			throw new IOException("Serviço temporariamente indisponível. Tente novamente em instantes.");

		IOException lastException = null;
		for (int attempt = 0; attempt <= maxRetries; attempt++)
		{
			if (!Toolkit.sleep(retryDelayMs * attempt))
				break;
			try
			{
				T result = action.execute();
				failures.set(0);
				return result;
			} catch (InterruptedException ex)
			{
				Thread.currentThread().interrupt();
				throw ex;
			} catch (IOException ex)
			{
				lastException = ex;
			}
		}

		if (failures.incrementAndGet() >= failureThreshold)
			openedAt = Instant.now();
		throw Objects.requireNonNullElse(lastException, new IOException("falha desconhecida"));
	}

	public static class Builder
	{
		private int failureThreshold = 3;
		private int maxRetries = 2;
		private int retryDelayMs = 1000;
		private Duration openDuration = Duration.ofSeconds(30);

		public Builder failureThreshold(int failureThreshold)
		{
			this.failureThreshold = failureThreshold;
			return this;
		}

		public Builder maxRetries(int maxRetries)
		{
			this.maxRetries = maxRetries;
			return this;
		}

		public Builder retryDelayMs(int retryDelayMs)
		{
			this.retryDelayMs = retryDelayMs;
			return this;
		}

		public Builder openDuration(Duration openDuration)
		{
			this.openDuration = openDuration;
			return this;
		}

		public CircuitBreaker build()
		{
			return new CircuitBreaker(this);
		}
	}
}
