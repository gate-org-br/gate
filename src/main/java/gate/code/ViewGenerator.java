package gate.code;

import gate.lang.expression.Parameters;
import gate.lang.template.Template;
import java.io.File;

public class ViewGenerator
{

	private final Class<?> type;

	private static final Template VIEW = Template.compile(ViewGenerator.class.getResource("ViewGenerator/view().gtf"));
	private static final Template VIEW_SEARCH = Template.compile(ViewGenerator.class.getResource("ViewGenerator/viewSearch().gtf"));
	private static final Template VIEW_SELECT = Template.compile(ViewGenerator.class.getResource("ViewGenerator/viewSelect().gtf"));
	private static final Template VIEW_INSERT = Template.compile(ViewGenerator.class.getResource("ViewGenerator/viewInsert().gtf"));
	private static final Template VIEW_UPDATE = Template.compile(ViewGenerator.class.getResource("ViewGenerator/viewUpdate().gtf"));
	private static final Template VIEW_RESULT = Template.compile(ViewGenerator.class.getResource("ViewGenerator/viewResult().gtf"));

	public ViewGenerator(Class<?> type)
	{
		this.type = type;
	}

	public String view()
	{
		return VIEW.evaluate(new EntityInfo(type), getDefaultParameters());
	}

	public void view(File file)
	{
		VIEW.evaluate(new EntityInfo(type), getDefaultParameters(), file);
	}

	public String viewSearch()
	{
		return VIEW_SEARCH.evaluate(new EntityInfo(type), getDefaultParameters());
	}

	public void viewSearch(File file)
	{
		VIEW_SEARCH.evaluate(new EntityInfo(type), getDefaultParameters(), file);
	}

	public String viewSelect()
	{
		return VIEW_SELECT.evaluate(new EntityInfo(type), getDefaultParameters());
	}

	public void viewSelect(File file)
	{
		VIEW_SELECT.evaluate(new EntityInfo(type), getDefaultParameters(), file);
	}

	public String viewInsert()
	{
		return VIEW_INSERT.evaluate(new EntityInfo(type), getDefaultParameters());
	}

	public void viewInsert(File file)
	{
		VIEW_INSERT.evaluate(new EntityInfo(type), getDefaultParameters(), file);
	}

	public String viewUpdate()
	{
		return VIEW_UPDATE.evaluate(new EntityInfo(type), getDefaultParameters());
	}

	public void viewUpdate(File file)
	{
		VIEW_UPDATE.evaluate(new EntityInfo(type), getDefaultParameters(), file);
	}

	public String viewResult()
	{
		return VIEW_RESULT.evaluate(new EntityInfo(type), getDefaultParameters());
	}

	public void viewResult(File file)
	{
		VIEW_RESULT.evaluate(new EntityInfo(type), getDefaultParameters(), file);
	}

	public static ClassName getDefault(Class<?> type)
	{
		return ClassName.of(PackageName.of(type).resolveSibling("modules"), type.getSimpleName() + "View");
	}

	public Parameters getDefaultParameters()
	{
		return new Parameters().put("screen", ViewGenerator.getDefault(type));
	}

}
