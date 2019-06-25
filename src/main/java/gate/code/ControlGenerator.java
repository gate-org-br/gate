package gate.code;

import gate.lang.expression.Parameters;
import gate.lang.template.Template;
import java.io.File;

public class ControlGenerator
{

	private final Class<?> type;

	private static final Template CONTROL = Template.compile(ControlGenerator.class.getResource("ControlGenerator/control().gtf"));

	private static final Template SELECT = Template.compile(ControlGenerator.class.getResource("ControlGenerator/select().gtf"));
	private static final Template SEARCH = Template.compile(ControlGenerator.class.getResource("ControlGenerator/search().gtf"));
	private static final Template INSERT = Template.compile(ControlGenerator.class.getResource("ControlGenerator/insert().gtf"));
	private static final Template UPDATE = Template.compile(ControlGenerator.class.getResource("ControlGenerator/update().gtf"));
	private static final Template DELETE = Template.compile(ControlGenerator.class.getResource("ControlGenerator/delete().gtf"));

	public ControlGenerator(Class<?> type)
	{
		this.type = type;
	}

	public String control()
	{
		return control(ControlGenerator.getDefault(type), DaoGenerator.getDefault(type));
	}

	public String control(ClassName control, ClassName dao)
	{
		return CONTROL.evaluate(new EntityInfo(type),
			new Parameters()
				.put("control", control)
				.put("dao", dao));
	}

	public void control(File file)
	{
		control(ControlGenerator.getDefault(type), DaoGenerator.getDefault(type), file);
	}

	public void control(ClassName control, ClassName dao, File file)
	{
		CONTROL.evaluate(new EntityInfo(type),
			new Parameters()
				.put("control", control)
				.put("dao", dao), file);
	}

	public String search()
	{
		return SEARCH.evaluate(new EntityInfo(type), new Parameters().put("dao", DaoGenerator.getDefault(type)));
	}

	public String select()
	{
		return SELECT.evaluate(new EntityInfo(type), new Parameters().put("dao", DaoGenerator.getDefault(type)));
	}

	public String insert()
	{
		return INSERT.evaluate(new EntityInfo(type), new Parameters().put("dao", DaoGenerator.getDefault(type)));
	}

	public String update()
	{
		return UPDATE.evaluate(new EntityInfo(type), new Parameters().put("dao", DaoGenerator.getDefault(type)));
	}

	public String delete()
	{
		return DELETE.evaluate(new EntityInfo(type), new Parameters().put("dao", DaoGenerator.getDefault(type)));
	}

	public static ClassName getDefault(Class<?> type)
	{
		return ClassName.of(PackageName.of(type).resolveSibling("control"), type.getSimpleName() + "Control");
	}

}
