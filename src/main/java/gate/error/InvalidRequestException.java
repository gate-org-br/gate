package gate.error;

public class InvalidRequestException extends RuntimeException
{

	private static final long serialVersionUID = 1L;

	public InvalidRequestException(String module, String screen, String action)
	{
		super(String.format("Requisição inválida: MODULE=%s, SCREEN=%s, ACTION=%s",
			module, screen, action));
	}
}
