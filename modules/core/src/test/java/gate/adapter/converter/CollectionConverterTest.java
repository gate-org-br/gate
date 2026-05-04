package gate.adapter.converter;

import gate.lang.property.Property;
import gate.type.ID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

public class CollectionConverterTest
{
	@SuppressWarnings("unused")
	private List<String> strings;

	private static class Bean
	{
		@gate.annotation.Converter(CollectionConverter.class)
		private List<ID> ids;

		@gate.annotation.Converter(CollectionConverter.class)
		private Set<String> strings;
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testShouldAcceptCommaSemicolonAndLineBreakSeparators() throws NoSuchFieldException
	{
		var type = CollectionConverterTest.class.getDeclaredField("strings").getGenericType();
		var converter = new CollectionConverter();

		var value = (List<String>) converter.ofString(type, "one, two;three\nfour");

		Assertions.assertEquals(List.of("one", "two", "three", "four"), value);
	}

	@Test
	public void testShouldWriteOneItemPerLine() throws NoSuchFieldException
	{
		var type = CollectionConverterTest.class.getDeclaredField("strings").getType();
		var converter = new CollectionConverter();

		var string = converter.toString(type, List.of("one", "two", "three"));

		Assertions.assertEquals("one\ntwo\nthree", string);
	}

	@Test
	public void testShouldUseGenericTypeOnAnnotatedProperty()
	{
		var bean = new Bean();
		var property = Property.getProperty(Bean.class, "ids");

		property.setConvertedValue(bean, "1, 2;3\n4");

		Assertions.assertEquals(List.of(ID.valueOf(1), ID.valueOf(2), ID.valueOf(3), ID.valueOf(4)),
				bean.ids);
		Assertions.assertEquals("0000000001\n0000000002\n0000000003\n0000000004",
				property.getConvertedValue(bean));
	}

	@Test
	public void testShouldCreateSetForSetProperties()
	{
		var bean = new Bean();
		var property = Property.getProperty(Bean.class, "strings");

		property.setConvertedValue(bean, "one, two;three\none");

		Assertions.assertEquals(Set.of("one", "two", "three"), bean.strings);
	}
}
