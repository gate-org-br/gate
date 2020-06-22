package gate.type;

import gate.annotation.Icon;

public enum Sex
{
	@Icon("2280")
	MALE("Masculino"),
	@Icon("2281")
	FEMALE("Feminino");
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
