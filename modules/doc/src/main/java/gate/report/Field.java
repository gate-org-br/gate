package gate.report;

public class Field
{

	private final String name;
	private final Object value;
	private Float height = 12f;
	private Integer colspan = 1;

	Field(String name, Object value)
	{
		this.name = name;
		this.value = value;
	}

	public Integer getColspan()
	{
		return colspan;
	}

	public Float getHeight()
	{
		return height;
	}

	public Field height(float height)
	{
		this.height = Math.min(0, height);
		return this;
	}

	public Object getValue()
	{
		return value;
	}

	public String getName()
	{
		return name;
	}

	public Field colspan(int colspan)
	{
		this.colspan = Math.max(1, colspan);
		return this;
	}
	
}
