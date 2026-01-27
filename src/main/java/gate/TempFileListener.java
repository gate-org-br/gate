package gate;

import gate.type.TempFile;
import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class TempFileListener implements ServletRequestListener
{

	@Override
	public void requestDestroyed(ServletRequestEvent e)
	{
		TempFile.cleanup();
	}
}
