package gateconsole.screen;

import gate.Progress;
import gate.annotation.Asynchronous;
import gate.annotation.Description;
import gate.annotation.Handler;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.annotation.Public;
import gate.base.Screen;
import gate.error.ConversionException;
import gate.handler.JsonTextHandler;
import gate.report.Doc;
import gate.report.Report;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Public
@Icon("2072")
@Name("Demo")
@Description("Demonstração")
public class DemoScreen extends Screen
{

	private Doc.Type type;
	private List<Doc.Type> types = new ArrayList<>();

	public String call()
	{
		return callTabBar();
	}

	@Name("Tab Bar")
	public String callTabBar()
	{
		return "/WEB-INF/views/gateconsole/Demo/ViewTabBar.jsp";
	}

	@Name("Pickers")
	public String callPickers()
	{
		return "/WEB-INF/views/gateconsole/Demo/ViewPickers.jsp";
	}

	@Name("Tooltip")
	public String callTooltip()
	{
		return "/WEB-INF/views/gateconsole/Demo/ViewTooltip.jsp";
	}

	@Name("Mensagens")
	public String callMessage()
	{
		return "/WEB-INF/views/gateconsole/Demo/ViewMessage.jsp";
	}

	@Name("Chart")
	public String callChart()
	{
		return "/WEB-INF/views/gateconsole/Demo/ViewChart.jsp";
	}

	@Name("Context Menu")
	public String callContextMenu()
	{
		return "/WEB-INF/views/gateconsole/Demo/ViewContextMenu.jsp";
	}

	@Name("Select")
	public String callSelect()
	{
		return "/WEB-INF/views/gateconsole/Demo/ViewSelect.jsp";
	}

	@Name("Grid 1")
	public String callGrid1()
	{
		return "/WEB-INF/views/gateconsole/Demo/ViewGrid1.jsp";
	}

	@Name("Grid 2")
	public String callGrid2()
	{
		return "/WEB-INF/views/gateconsole/Demo/ViewGrid2.jsp";
	}

	@Name("Other")
	public String callOther()
	{
		return "/WEB-INF/views/gateconsole/Demo/ViewOther.jsp";
	}

	@Icon("report")
	@Name("Imprimir")
	public Doc callReport()
	{
		Report report = new Report();
		report.addGrid(String.class, IntStream
			.iterate(1, e -> e + 1).limit(100000)
			.mapToObj(e -> String.format("Item %06d", e))
			.collect(Collectors.toList()))
			.add().body(e -> e).head("Item");
		return Doc.create(type, report);
	}

	@Asynchronous
	@Name("Progress")
	public void callProgress()
	{
		Progress.startup(100, "Starting");
		for (int i = 0; i < 100; i++)
		{
			try
			{
				Progress.update("Processing");
				Thread.sleep(50);;
			} catch (InterruptedException ex)
			{
				Progress.cancel(ex.getMessage());
			}

		}
		Progress.commit("Success");
	}

	@Name("Block")
	public String callBlock()
	{
		try
		{
			Thread.sleep(4000);
			return call();
		} catch (InterruptedException ex)
		{
			return ex.getMessage();
		}
	}

	@Handler(JsonTextHandler.class)
	public List<List<Object>> callData() throws ConversionException
	{

		int size = getRequest().getParameter(Integer.class, "size");
		return TestData.INSTANCE.stream().skip(size).limit(10).collect(Collectors.toList());
	}

	public void setType(Doc.Type type)
	{
		this.type = type;
	}

	public List<Doc.Type> getTypes()
	{
		return types;
	}

	public void setTypes(List<Doc.Type> types)
	{
		this.types = types;
	}

	public List<Doc.Type> getOptions()
	{
		return List.of(Doc.Type.PDF, Doc.Type.DOC, Doc.Type.XLS, Doc.Type.CSV);
	}

	private static class TestData extends ArrayList<List<Object>>
	{

		private static final TestData INSTANCE = new TestData();

		private TestData()
		{
			int index = 0;

			List<String> nomes = List.of("Paulo", "Pedro", "Carlos", "Silvio", "José", "Sandro", "Camila", "Davi", "Patrícia", "Renata");
			List<String> sobrenomes = List.of("Carvalho", "Nunes", "Silva", "Gusmão", "Cunha", "Ferreira", "Barbosa", "Barnabé", "Santos", "Azevedo");

			for (String nome : nomes)
				for (String sobrenome : sobrenomes)
					add(List.of(index++, nome + " " + sobrenome, nome.toLowerCase() + sobrenome.toLowerCase() + "@br.com", true));

		}
	}
}
