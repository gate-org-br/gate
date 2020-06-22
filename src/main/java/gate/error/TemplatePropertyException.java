package gate.error;

/**
 * Signals that the class doesn't have a property of a specified name.
 *
 * @author davins
 */
public class TemplatePropertyException extends TemplateException
{

	private final Class<?> type;
	private final String property;

	public TemplatePropertyException(Class<?> type, String property)
	{
		super(String.format("Nenhuma propriedade de nome %s encontrada no contexto especificado",
			property));
		this.type = type;
		this.property = property;
	}

	public Class<?> getType()
	{
		return type;
	}

	public String getProperty()
	{
		return property;
	}
}
