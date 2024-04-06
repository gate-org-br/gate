package gate.producer;

import gate.type.TempFileManager;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;

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
