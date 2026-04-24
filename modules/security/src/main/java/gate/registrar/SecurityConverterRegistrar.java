package gate.registrar;

import gate.converter.*;
import gate.converter.custom.*;
import gate.security.Captcha;
import gate.security.hash.BCrypt;
import gate.security.hash.MD5;
import gate.type.SafeHTML;
import gate.type.SafeName;
import gate.type.SafeStyle;
import gate.type.SafeText;

import java.util.Map;

public class SecurityConverterRegistrar implements ConverterRegistrar
{

	@Override
	public void register(Map<Class<?>, Converter> registry)
	{
		registry.put(Captcha.class, new CaptchaConverter());
		registry.put(MD5.class, new MD5Converter());
		registry.put(BCrypt.class, new BCryptConverter());
		registry.put(SafeStyle.class, new SafeStyleConverter());
		registry.put(SafeHTML.class, new SafeHTMLConverter());
		registry.put(SafeText.class, new SafeTextConverter());
		registry.put(SafeName.class, new SafeNameConverter());
	}
}
