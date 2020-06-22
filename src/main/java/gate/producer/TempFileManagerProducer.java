package gate.producer;

import gate.type.TempFileManager;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

/**
 *
 * @author davins
 *
 * Produces and disposes temporary file objects.
 *
 */
public class TempFileManagerProducer
{

	@Produces
	@RequestScoped
	public TempFileManager produces()
	{
		return new TempFileManager();
	}

	public void disposer(@Disposes TempFileManager tempFileManager)
	{
		tempFileManager.close();
	}
}
