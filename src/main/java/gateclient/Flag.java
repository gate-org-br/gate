package gateclient;

public enum Flag
{

	ENTITY("-e", "--entity", "Define the list of entities"),
	CLASSPATH("-cp", "--classpath", "Specify the classpath of the specified entities"),
	GENERATE_DAO("-d", "--dao", "Generate the Dao java file of each specified entity"),
	GENERATE_CONTROL("-c", "--control", "Generate the Control java file of each specified entity");
	private final String shortcut;
	private final String longname;
	private final String description;

	Flag(String shortcut, String longname, String description)
	{
		this.longname = longname;
		this.shortcut = shortcut;
		this.description = description;
	}

	public String getLongname()
	{
		return longname;
	}

	public String getShortcut()
	{
		return shortcut;
	}

	public String getDescription()
	{
		return description;
	}

	@Override
	public String toString()
	{
		return String.format("%s, %s: %s", shortcut, longname, description);
	}
}
