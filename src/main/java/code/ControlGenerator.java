package code;

import gate.lang.expression.Parameters;
import gate.lang.property.EntityInfo;
import gate.lang.template.Template;
import java.io.File;

public class ControlGenerator
{

	private final EntityInfo entityInfo;

	private static final Template CONTROL = Template.compile(ControlGenerator.class.getResource("ControlGenerator/control().gtf"));

	private static final Template SELECT = Template.compile(ControlGenerator.class.getResource("ControlGenerator/select().gtf"));
	private static final Template SEARCH = Template.compile(ControlGenerator.class.getResource("ControlGenerator/search().gtf"));
	private static final Template INSERT = Template.compile(ControlGenerator.class.getResource("ControlGenerator/insert().gtf"));
	private static final Template UPDATE = Template.compile(ControlGenerator.class.getResource("ControlGenerator/update().gtf"));
	private static final Template DELETE = Template.compile(ControlGenerator.class.getResource("ControlGenerator/delete().gtf"));

	public ControlGenerator(Class<?> type)
	{
		this.entityInfo = new EntityInfo(type);
	}

	public String control()
	{
		return control(getDefault(entityInfo.getType()));
	}

	public String control(ClassName className)
	{
		return CONTROL.evaluate(entityInfo,
			new Parameters()
				.put("className", className));
	}

	public void control(ClassName className, File file)
	{
		CONTROL.evaluate(entityInfo,
			new Parameters()
				.put("className", className),
			file);
	}

	public void control(File file)
	{
		control(getDefault(entityInfo.getType()), file);
	}

	public String search()
	{
		return SEARCH.evaluate(entityInfo);
	}

	public String select()
	{
		return SELECT.evaluate(entityInfo);
	}

	public String insert()
	{
		return INSERT.evaluate(entityInfo);
	}

	public String update()
	{
		return UPDATE.evaluate(entityInfo);
	}

	public String delete()
	{
		return DELETE.evaluate(entityInfo);
	}

	public static ClassName getDefault(Class<?> type)
	{
		return ClassName.of(PackageName.of(type).resolveSibling("control"), type.getSimpleName() + "Control");
	}

}
