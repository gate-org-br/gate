package gate;

import gate.entity.User;

public record GateContext(User user)
{
	private static final ThreadLocal<GateContext>
			CONTEXT = new ThreadLocal<>();

	public static GateContext get()
	{
		return CONTEXT.get();
	}

	static void init(GateContext context)
	{
		CONTEXT.set(context);
	}

	static void close()
	{
		CONTEXT.remove();
	}
}