package gate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;


class GateContextTest
{
	GateContext gateContext = new GateContext();

	@AfterEach
	void clearContext()
	{
		gateContext.clear();
	}

	@Test
	void setAndGet()
	{
		gateContext.set(String.class, "user");
		gateContext.set(Locale.class, Locale.US);

		assertEquals("user", gateContext.get(String.class).orElseThrow());
		assertEquals(Locale.US, gateContext.get(Locale.class).orElseThrow());
		assertTrue(gateContext.has(String.class));
		assertTrue(gateContext.has(Locale.class));
	}

	@Test
	void remove()
	{
		gateContext.set(String.class, "user");

		gateContext.remove(String.class);

		assertTrue(gateContext.get(String.class).isEmpty());
		assertFalse(gateContext.has(String.class));
	}

	@Test
	void clear()
	{
		gateContext.set(String.class, "user");
		gateContext.set(Locale.class, Locale.US);

		gateContext.clear();

		assertTrue(gateContext.get(String.class).isEmpty());
		assertTrue(gateContext.get(Locale.class).isEmpty());
		assertFalse(gateContext.has(String.class));
		assertFalse(gateContext.has(Locale.class));
	}

	@Test
	void nullValueRemoves()
	{
		gateContext.set(String.class, "user");

		gateContext.set(String.class, null);

		assertTrue(gateContext.get(String.class).isEmpty());
		assertFalse(gateContext.has(String.class));
	}
}