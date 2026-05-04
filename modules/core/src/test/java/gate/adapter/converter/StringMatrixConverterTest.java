package gate.adapter.converter;

public class StringMatrixConverterTest extends AbstractSimpleConverterTest<String[][]>
{
	@Override protected Class<String[][]> getType() {return String[][].class;}

	@Override protected String[][] getValue() {return new String[][]{{"a", "b"}, {"c", "d"}};}

	@Override protected String getString()
	{
		return "\"a\";\"b\"" + System.lineSeparator()
				+ "\"c\";\"d\"" + System.lineSeparator();
	}
}
