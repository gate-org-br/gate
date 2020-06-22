package gate.error;

public class InvalidServiceException extends Exception
{

	private static final long serialVersionUID = 1L;

	public InvalidServiceException()
	{
		super("Erro ao conectar com o servidor de autenticação");
	}
}
