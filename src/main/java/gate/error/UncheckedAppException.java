package gate.error;

import gate.annotation.Catcher;
import gate.catcher.UncheckedAppExceptionCatcher;

@Catcher(UncheckedAppExceptionCatcher.class)
public class UncheckedAppException extends RuntimeException
{

	public UncheckedAppException(AppException cause)
	{
		super(cause);
	}

	@Override
	public AppException getCause()
	{
		return (AppException) super.getCause();
	}
}
