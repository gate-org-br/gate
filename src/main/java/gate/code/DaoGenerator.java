package gate.code;

import gate.lang.expression.Parameters;
import gate.lang.template.Template;
import java.io.File;

public class DaoGenerator
{

	private final Class<?> type;

	private static final Template DAO = Template.compile(DaoGenerator.class.getResource("DaoGenerator/dao().gtf"));

	private static final Template SELECT = Template.compile(DaoGenerator.class.getResource("DaoGenerator/select().gtf"));
	private static final Template SEARCH = Template.compile(DaoGenerator.class.getResource("DaoGenerator/search().gtf"));
	private static final Template INSERT = Template.compile(DaoGenerator.class.getResource("DaoGenerator/insert().gtf"));
	private static final Template UPDATE = Template.compile(DaoGenerator.class.getResource("DaoGenerator/update().gtf"));
	private static final Template DELETE = Template.compile(DaoGenerator.class.getResource("DaoGenerator/delete().gtf"));

	public DaoGenerator(Class<?> type)
	{
		this.type = type;
	}

	public String dao()
	{
		return dao(getDefault(type));
	}

	public String dao(ClassName className)
	{
		return DAO.evaluate(new EntityInfo(type), new Parameters().put("dao", className));
	}

	public void dao(ClassName className, File file)
	{
		DAO.evaluate(new EntityInfo(type), new Parameters().put("dao", className), file);
	}

	public void dao(File file)
	{
		dao(getDefault(type), file);
	}

	public String search()
	{
		return SEARCH.evaluate(new EntityInfo(type), new Parameters().put("dao", getDefault(type)));
	}

	public String select()
	{
		return SELECT.evaluate(new EntityInfo(type), new Parameters().put("dao", getDefault(type)));
	}

	public String insert()
	{
		return INSERT.evaluate(new EntityInfo(type), new Parameters().put("dao", getDefault(type)));
	}

	public String update()
	{
		return UPDATE.evaluate(new EntityInfo(type), new Parameters().put("dao", getDefault(type)));
	}

	public String delete()
	{
		return DELETE.evaluate(new EntityInfo(type), new Parameters().put("dao", getDefault(type)));
	}

	public static ClassName getDefault(Class<?> type)
	{
		return ClassName.of(PackageName.of(type).resolveSibling("dao"), type.getSimpleName() + "Dao");
	}
}
