package gate.registrar;

import gate.lang.property.metadata.*;
import gate.security.Captcha;
import gate.security.hash.BCrypt;
import gate.security.hash.MD5;
import gate.type.SafeHTML;
import gate.type.SafeName;
import gate.type.SafeStyle;
import gate.type.SafeText;

import java.util.Map;

public class SecurityMetadataRegistrar implements MetadataRegistrar
{
	@Override
	public void register(Map<Class<?>, Metadata> registry)
	{
		registry.put(MD5.class, new MD5Metadata());
		registry.put(BCrypt.class, new BCryptMetadata());
		registry.put(SafeStyle.class, new SafeStyleMetadata());
		registry.put(SafeHTML.class, new SafeHTMLMetadata());
		registry.put(SafeText.class, new SafeTextMetadata());
		registry.put(SafeName.class, new SafeNameMetadata());
	}
}
