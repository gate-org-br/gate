package code;

import gate.lang.expression.Parameters;
import gate.lang.property.EntityInfo;
import gate.lang.template.Template;
import java.io.File;

public class DaoGenerator
{

	private final EntityInfo entityInfo;

	private static final Template DAO = Template.compile(DaoGenerator.class.getResource("DaoGenerator/dao().gtf"));

	private static final Template SELECT = Template.compile(DaoGenerator.class.getResource("DaoGenerator/select().gtf"));
	private static final Template SEARCH = Template.compile(DaoGenerator.class.getResource("DaoGenerator/search().gtf"));
	private static final Template INSERT = Template.compile(DaoGenerator.class.getResource("DaoGenerator/insert().gtf"));
	private static final Template UPDATE = Template.compile(DaoGenerator.class.getResource("DaoGenerator/update().gtf"));
	private static final Template DELETE = Template.compile(DaoGenerator.class.getResource("DaoGenerator/delete().gtf"));

	public DaoGenerator(Class<?> type)
	{
		this.entityInfo = new EntityInfo(type);
	}

	public String dao()
	{
		return dao(getDefault(entityInfo.getType()));
	}

	public String dao(ClassName className)
	{
		return DAO.evaluate(entityInfo,
			new Parameters()
				.put("className", className));
	}

	public void dao(ClassName className, File file)
	{
		DAO.evaluate(entityInfo,
			new Parameters()
				.put("className", className),
			file);
	}

	public void dao(File file)
	{
		dao(getDefault(entityInfo.getType()), file);
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
		return ClassName.of(PackageName.of(type).resolveSibling("dao"), type.getSimpleName() + "Dao");
	}
}
