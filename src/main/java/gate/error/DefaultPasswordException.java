package gate.error;

public class DefaultPasswordException extends AuthenticationException
{

	private static final long serialVersionUID = 1L;

	public DefaultPasswordException()
	{
		super("Sua senha ainda é a senha padrão e deve ser trocada antes de proseguir");
	}
}
