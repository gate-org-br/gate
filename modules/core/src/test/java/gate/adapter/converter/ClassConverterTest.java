package gate.adapter.converter;

public class ClassConverterTest extends AbstractSimpleConverterTest<Class>
{
	@Override protected Class<Class> getType() {return Class.class;}

	@Override protected Class<?> getValue() {return String.class;}

	@Override protected String getString() {return "java.lang.String";}
}
