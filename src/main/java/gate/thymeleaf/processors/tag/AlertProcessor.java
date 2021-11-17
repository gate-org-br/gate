package gate.thymeleaf.processors.tag;

import gate.thymeleaf.Model;
import java.util.StringJoiner;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AlertProcessor extends ModelProcessor
{

	public AlertProcessor()
	{
		super("alert");
	}

	@Override
	protected void doProcess(Model model)
	{
		var messages = model.screen().getMessages();

		if (!messages.isEmpty())
		{
			StringJoiner string = new StringJoiner(System.lineSeparator());

			string.add("<script>");
			string.add("window.addEventListener('load',function(){");
			messages.stream()
				.map(e -> e.replace('\'', '"'))
				.map(e -> "alert('" + e + "');")
				.forEach(string::add);
			string.add("let href = window.location.href;");
			string.add("href = href.replace(/messages=[a-zA-Z0-9+\\/=%]*&/, '');");
			string.add("href = href.replace(/&messages=[a-zA-Z0-9+\\/=%]*/, '');");
			string.add("href = href.replace(/[?]messages=[a-zA-Z0-9+\\/=%]*/, '');");
			string.add("window.history.replaceState({}, document.title, href);");
			string.add("});");
			string.add("</script>");

			model.replaceAll(string.toString());
		} else
			model.removeAll();
	}

}
