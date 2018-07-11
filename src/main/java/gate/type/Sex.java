package gate.type;

public enum Sex
{
	MALE("Masculino"), FEMALE("Feminino");
	private final String string;

	Sex(String string)
	{
		this.string = string;
	}

	@Override
	public String toString()
	{
		return string;
	}

}
