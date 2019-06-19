package gateclient;

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
		return CONTROL.evaluate(entityInfo);
	}

	public void control(File file)
	{
		CONTROL.evaluate(entityInfo, file);
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
}
