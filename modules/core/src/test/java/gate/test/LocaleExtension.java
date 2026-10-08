package gate.test;

import gate.i18n.CurrentLocale;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Locale;

public class LocaleExtension implements BeforeEachCallback, AfterEachCallback
{
	private static final Locale LOCALE = Locale.forLanguageTag("pt-BR");

	@Override
	public void beforeEach(ExtensionContext ctx)
	{
		CurrentLocale.set(LOCALE);
	}

	@Override
	public void afterEach(ExtensionContext ctx)
	{
		CurrentLocale.clear();
	}
}
