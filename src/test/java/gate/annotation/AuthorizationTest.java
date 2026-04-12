package gate.annotation;

import gate.annotation.authorizationtest.PackageScreen;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AuthorizationTest
{
	@Test
	void shouldOverrideCoordinatesWithModuleScreenAndActionAnnotations() throws NoSuchMethodException
	{
		Method method = PackageScreen.class.getDeclaredMethod("select");

		var authorization = Authorization.Extractor.extract(method, "base.module", "base.screen", "base.action");

		assertEquals("package.module", authorization.module());
		assertEquals("AnnotatedScreen", authorization.screen());
		assertEquals("Select", authorization.action());
	}

	@Test
	void shouldPreserveOriginalCoordinatesWhenPartialAnnotationsAreMissing() throws NoSuchMethodException
	{
		Method method = PackageScreen.class.getDeclaredMethod("defaultAction");

		var authorization = Authorization.Extractor.extract(method, "base.module", "base.screen", "base.action");

		assertEquals("package.module", authorization.module());
		assertEquals("AnnotatedScreen", authorization.screen());
		assertEquals("base.action", authorization.action());
	}

	@Test
	void shouldPreferAuthorizationAnnotationOverPartialAnnotations() throws NoSuchMethodException
	{
		Method method = PackageScreen.class.getDeclaredMethod("replaced");

		var authorization = Authorization.Extractor.extract(method, "base.module", "base.screen", "base.action");

		assertNull(authorization.module());
		assertNull(authorization.screen());
		assertEquals("ReplaceAll", authorization.action());
	}
}
