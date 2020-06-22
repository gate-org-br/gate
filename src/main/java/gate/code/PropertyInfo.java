package gate.code;

import gate.lang.property.Property;
import gate.type.DataFile;
import gate.type.mime.MimeData;
import gate.type.mime.MimeDataFile;
import gate.type.mime.MimeFile;
import gate.type.mime.MimeText;
import gate.type.mime.MimeTextFile;
import java.util.stream.Collectors;

class PropertyInfo
{
	
	private final Property property;

	PropertyInfo(Property property)
	{
		this.property = property;
	}

	public String getName()
	{
		return property.getAttributes().stream().skip(1).map((e) -> e.toString()).collect(Collectors.joining("."));
	}

	public String getDisplayName()
	{
		String displayName = property.getDisplayName();
		if (displayName == null) displayName = getName();
		return displayName;
	}

	public ClassName getClassName()
	{
		return ClassName.of(property.getRawType());
	}

	public String getColumnName()
	{
		return property.getColumnName();
	}

	public String getGetter()
	{
		return property.getAttributes().stream().skip(1).map((e) -> e.toString()).map((e) -> "get" + Character.toUpperCase(e.charAt(0)) + e.substring(1) + "()").collect(Collectors.joining("."));
	}

	public String getSetter()
	{
		return property.getAttributes().stream().skip(1).limit(1).map((e) -> e.toString()).map((e) -> "set" + Character.toUpperCase(e.charAt(0)) + e.substring(1)).collect(Collectors.joining("."));
	}

	public String getInputType()
	{
		if (property.getRawType().isEnum() || property.getRawType() == Boolean.class || property.getRawType() == Boolean.TYPE) return "select";
		else if (property.getRawType() == DataFile.class || property.getRawType() == MimeData.class || property.getRawType() == MimeText.class || property.getRawType() == MimeFile.class || property.getRawType() == MimeTextFile.class || property.getRawType() == MimeDataFile.class) return "file";
		else return "text";
	}
	
}
