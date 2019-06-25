package gate.code;

import gate.lang.template.Template;
import java.io.File;

public class DMLGenerator
{

	private final Class<?> type;

	private static final Template SELECT = Template.compile(DMLGenerator.class.getResource("DMLGenerator/select().gtf"));
	private static final Template SEARCH = Template.compile(DMLGenerator.class.getResource("DMLGenerator/search().gtf"));

	public DMLGenerator(Class<?> type)
	{
		this.type = type;
	}

	public String search()
	{
		return SEARCH.evaluate(new EntityInfo(type));
	}

	public void search(File file)
	{
		SEARCH.evaluate(new EntityInfo(type), file);
	}

	public String select()
	{
		return SELECT.evaluate(new EntityInfo(type));
	}

	public void select(File file)
	{
		SELECT.evaluate(new EntityInfo(type), file);
	}

	public static ClassName getDefault(Class<?> type)
	{
		return ClassName.of(PackageName.of(type).resolveSibling("dao"), type.getSimpleName() + "Dao");
	}
}
