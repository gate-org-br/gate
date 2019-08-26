package gate.lang.HTMLCleaner;

enum Token
{
	EOF(""),
	OPEN_TAG("<"),
	CLOSE_TAG(">"),
	QUOTE("'"),
	DOUBLE_QUOTE("\"");

	private final String string;

	Token(String string)
	{
		this.string = string;
	}

	@Override
	public String toString()
	{
		return string;
	}
}
