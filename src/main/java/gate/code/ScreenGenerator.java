package gate.code;

import gate.lang.expression.Parameters;
import gate.lang.template.Template;
import java.io.File;

public class ScreenGenerator
{

	private final Class<?> type;

	private static final Template CONTROL = Template.compile(ScreenGenerator.class.getResource("ScreenGenerator/screen().gtf"));

	private static final Template CALL = Template.compile(ScreenGenerator.class.getResource("ScreenGenerator/call().gtf"));
	private static final Template CALL_SEARCH = Template.compile(ScreenGenerator.class.getResource("ScreenGenerator/callSearch().gtf"));
	private static final Template CALL_SELECT = Template.compile(ScreenGenerator.class.getResource("ScreenGenerator/callSelect().gtf"));
	private static final Template CALL_INSERT = Template.compile(ScreenGenerator.class.getResource("ScreenGenerator/callInsert().gtf"));
	private static final Template CALL_UPDATE = Template.compile(ScreenGenerator.class.getResource("ScreenGenerator/callUpdate().gtf"));
	private static final Template CALL_DELETE = Template.compile(ScreenGenerator.class.getResource("ScreenGenerator/callDelete().gtf"));

	public ScreenGenerator(Class<?> type)
	{
		this.type = type;
	}

	public String screen()
	{
		return screen(ScreenGenerator.getDefault(type), ControlGenerator.getDefault(type));
	}

	public String screen(ClassName screen, ClassName control)
	{
		return CONTROL.evaluate(new EntityInfo(type),
			new Parameters()
				.put("screen", screen)
				.put("control", control));
	}

	public void screen(File file)
	{
		screen(ScreenGenerator.getDefault(type), ControlGenerator.getDefault(type), file);
	}

	public void screen(ClassName screen, ClassName control, File file)
	{
		CONTROL.evaluate(new EntityInfo(type),
			new Parameters()
				.put("screen", screen)
				.put("control", control), file);
	}

	public String call()
	{
		return CALL.evaluate(new EntityInfo(type), getDefaultParameters());
	}

	public String callSearch()
	{
		return CALL_SEARCH.evaluate(new EntityInfo(type), getDefaultParameters());
	}

	public String callSelect()
	{
		return CALL_SELECT.evaluate(new EntityInfo(type), getDefaultParameters());
	}

	public String callInsert()
	{
		return CALL_INSERT.evaluate(new EntityInfo(type), getDefaultParameters());
	}

	public String callUpdate()
	{
		return CALL_UPDATE.evaluate(new EntityInfo(type), getDefaultParameters());
	}

	public String callDelete()
	{
		return CALL_DELETE.evaluate(new EntityInfo(type), getDefaultParameters());
	}

	public static ClassName getDefault(Class<?> type)
	{
		return ClassName.of(PackageName.of(type).resolveSibling("modules"), type.getSimpleName() + "Screen");
	}

	public Parameters getDefaultParameters()
	{
		return new Parameters().put("screen", ScreenGenerator.getDefault(type))
			.put("control", ControlGenerator.getDefault(type));
	}

}
