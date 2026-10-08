package gate.error;

import java.io.Serial;

public class NotFoundException extends Exception
{
	@Serial
	private static final long serialVersionUID = 1L;

	public NotFoundException()
	{
		super("Not found");
	}

	public NotFoundException(String message)
	{
		super(message);
	}
}
