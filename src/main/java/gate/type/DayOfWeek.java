package gate.type;

import gate.annotation.Icon;

@Icon("2009")
public enum DayOfWeek
{
	Sunday("Domingo"), Monday("Segunda"), Tuesday("Terça"), Wednesday("Quarta"), Thursday("Quinta"),
	Friday("Sexta"), Saturday("Sábado");

	private final String string;

	DayOfWeek(String string)
	{
		this.string = string;
	}

	@Override
	public String toString()
	{
		return string;
	}
}
