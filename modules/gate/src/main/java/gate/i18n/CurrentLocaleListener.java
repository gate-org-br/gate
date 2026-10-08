package gate.i18n;

import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
class CurrentLocaleListener implements ServletRequestListener
{
	@Override
	public void requestInitialized(ServletRequestEvent event) {CurrentLocale.set(event.getServletRequest().getLocale());}

	@Override
	public void requestDestroyed(ServletRequestEvent event) {CurrentLocale.clear();}
}