package gate.annotation;

import gate.entity.User;
import gate.report.Chart;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NameTest
{

	@Test
	void shouldExtractNameFromAnnotatedClass()
	{
		assertInPortuguese(() ->
				assertEquals("Usuário", Name.Extractor.extract(User.class).orElseThrow()));
	}

	@Test
	void shouldReturnLiteralWhenStringIsNotAReference()
	{
		assertEquals("literal", Name.Extractor.extract("literal").orElseThrow());
	}

	@Test
	void shouldResolveNameFromFieldReferenceString()
	{
		assertInPortuguese(() -> assertEquals("Login do Usuário",
				Name.Extractor.extract("gate.entity.User:username").orElseThrow()));
	}

	@Test
	void shouldResolveNameFromMethodReferenceString()
	{
		assertInPortuguese(() -> assertEquals("Nome Localizado de Metodo",
				Name.Extractor.extract("gate.annotation.NameTest:testMethodName()").orElseThrow()));
	}

	@Test
	void shouldExtractNameFromEnumConstant()
	{
		assertInPortuguese(() ->
				assertEquals("Pizza", Name.Extractor.extract(Chart.Format.PIE).orElseThrow()));
	}

	@Test
	void shouldFollowCopyNameAnnotation()
	{
		assertInPortuguese(() ->
				assertEquals("Usuário", Name.Extractor.extract(CopiedUserName.class).orElseThrow()));
	}

	@Test
	void shouldResolveNameFromBlankAnnotationUsingMetadataBundle()
	{
		assertInPortuguese(() ->
				assertEquals("Nome Localizado de Classe",
						Name.Extractor.extract(LocalizedName.class).orElseThrow()));
	}

	@Test
	void shouldReturnEmptyForNull()
	{
		assertTrue(Name.Extractor.extract(null).isEmpty());
	}

	@Name
	public void testMethodName()
	{
	}

	@CopyName(User.class)
	private static class CopiedUserName
	{
	}

	@Name
	private static class LocalizedName
	{
	}

	private static void assertInPortuguese(Runnable assertion)
	{
		Locale defaultLocale = Locale.getDefault();
		try
		{
			Locale.setDefault(Locale.forLanguageTag("pt-BR"));
			assertion.run();
		} finally
		{
			Locale.setDefault(defaultLocale);
		}
	}
}
