package gate.lang.property;


import gate.icon.Icon;

public record Metadata(
		String name,
		String description,
		String tooltip,
		String placeholder,
		String mask,
		String color,
		String code,
		Icon icon
)
{
	public static final Metadata EMPTY = new Metadata(null, null, null,
			null, null, null, null, null);


}