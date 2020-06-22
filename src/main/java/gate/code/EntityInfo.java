package gate.code;

import gate.lang.property.Entity;
import gate.lang.property.Property;
import gate.type.DataFile;
import gate.type.mime.MimeData;
import gate.type.mime.MimeDataFile;
import gate.type.mime.MimeFile;
import gate.type.mime.MimeText;
import gate.type.mime.MimeTextFile;
import java.util.List;
import java.util.stream.Collectors;

class EntityInfo
{

	private final Class<?> type;
	private final String tableName;
	private final PropertyInfo id;
	private final ClassName className;
	private final List<PropertyInfo> properties;

	public EntityInfo(Class<?> type)
	{
		this.type = type;
		this.className = ClassName.of(type);
		this.tableName = Entity.getTableName(type);
		this.id = new PropertyInfo(Property.getProperty(type, Entity.getId(type)));
		this.properties = Entity.getProperties(type, e -> !e.isEntityId()).stream()
			.map(e -> new PropertyInfo(e)).collect(Collectors.toList());
	}

	public Class<?> getType()
	{
		return type;
	}

	public ClassName getClassName()
	{
		return className;
	}

	public String getTableName()
	{
		return tableName;
	}

	public PropertyInfo getId()
	{
		return id;
	}

	public String getDisplayName()
	{
		return Entity.getDisplayName(type);
	}

	public List<PropertyInfo> getProperties()
	{
		return properties;
	}

	public boolean isUpload()
	{
		return Entity.getProperties(type)
			.stream().map(e -> e.getRawType())
			.anyMatch(e -> e == DataFile.class
			|| e == MimeData.class
			|| e == MimeText.class
			|| e == MimeFile.class
			|| e == MimeTextFile.class
			|| e == MimeDataFile.class);
	}
}
