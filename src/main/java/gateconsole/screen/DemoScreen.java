package gateconsole.screen;

import gate.Progress;
import gate.annotation.Asynchronous;
import gate.annotation.Description;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.annotation.Public;
import gate.base.Screen;
import gate.report.Doc;
import gate.report.Report;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Public
@Icon("2072")
@Name("Demo")
@Description("Demonstração")
public class DemoScreen extends Screen
{

	private Doc.Type type;

	public String call()
	{
		return "/WEB-INF/views/gateconsole/Demo/View.jsp";
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

	public void setType(Doc.Type type)
	{
		this.type = type;
	}
}
