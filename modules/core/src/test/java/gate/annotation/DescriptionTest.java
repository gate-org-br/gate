package gate.annotation;

import mock.UserMock;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DescriptionTest
{

	@Test
	void shouldExtractDescriptionFromAnnotatedField() throws NoSuchFieldException
	{
		assertInPortuguese(() -> assertEquals("O campo LOGIN deve possuir no máximo 64 caracteres.",
				Description.Extractor.extract(UserMock.class.getDeclaredField("username")).orElseThrow()));
	}

	@Test
	void shouldReturnLiteralWhenStringIsNotAReference()
	{
		assertEquals("literal", Description.Extractor.extract("literal").orElseThrow());
	}

	@Test
	void shouldResolveDescriptionFromFieldReferenceString()
	{
		assertInPortuguese(() -> assertEquals("O campo LOGIN deve possuir no máximo 64 caracteres.",
				Description.Extractor.extract("mock.UserMock:username").orElseThrow()));
	}

	@Test
	void shouldResolveDescriptionFromMethodReferenceString()
	{
		assertInPortuguese(() -> assertEquals("Descricao Localizada de Metodo",
				Description.Extractor.extract("gate.annotation.DescriptionTest:testMethodDescription()").orElseThrow()));
	}

	@Test
	void shouldExtractDescriptionFromEnumConstant()
	{
		assertEquals("Enum Description",
				Description.Extractor.extract(LocalEnum.VALUE).orElseThrow());
	}

	@Test
	void shouldFollowCopyDescriptionAnnotation()
	{
		assertEquals("Copied Description",
				Description.Extractor.extract(CopiedUserDescription.class).orElseThrow());
	}

	@Test
	void shouldResolveDescriptionFromBlankAnnotationUsingMetadataBundle()
	{
		assertInPortuguese(() ->
				assertEquals("Descricao Localizada de Classe",
						Description.Extractor.extract(LocalizedDescription.class).orElseThrow()));
	}

	@Test
	void shouldReturnEmptyForNull()
	{
		assertTrue(Description.Extractor.extract(null).isEmpty());
	}

	@Description
	public void testMethodDescription()
	{
	}

	@Description("Copied Description")
	private static class DescriptionSource
	{
	}

	@CopyDescription(DescriptionSource.class)
	private static class CopiedUserDescription
	{
	}

	@Description
	private static class LocalizedDescription
	{
	}

	private enum LocalEnum
	{
		@Description("Enum Description")
		VALUE
	}

	private static void assertInPortuguese(ThrowingRunnable assertion)
	{
		Locale defaultLocale = Locale.getDefault();
		try
		{
			Locale.setDefault(Locale.forLanguageTag("pt-BR"));
			assertion.run();
		} catch (Exception ex)
		{
			throw new RuntimeException(ex);
		} finally
		{
			Locale.setDefault(defaultLocale);
		}
	}

	@FunctionalInterface
	private interface ThrowingRunnable
	{
		void run() throws Exception;
	}
}
