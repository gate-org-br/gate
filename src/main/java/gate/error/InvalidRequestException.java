package gate.error;

public class InvalidRequestException extends Exception
{

	private static final long serialVersionUID = 1L;

	public InvalidRequestException()
	{
		super("Requisição Inválida");
	}
}
