package gate.thymeleaf.processors.tag;

import gate.Call;
import gate.Calls;
import gate.type.Attributes;
import gate.type.RequestCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Stream;

@ApplicationScoped
public class PathProcessor extends TagProcessor
{

	@Inject
	Calls calls;

	public PathProcessor()
	{
		super("path");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		if ("g-path".equals(element.getElementCompleteName()))
			return;

		var command = new RequestCommand(
				extract(element, handler, "module").orElse(null),
				extract(element, handler, "screen").orElse(null),
				extract(element, handler, "action").orElse(null));

		var request = ((IWebContext) context).getExchange().getRequest();

		if (command.isDefault())
			command = new RequestCommand(
					request.getParameterValue("MODULE"),
					request.getParameterValue("SCREEN"),
					request.getParameterValue("ACTION"));
		else
			command = command
					.with(new RequestCommand(request.getParameterValue("MODULE"),
							request.getParameterValue("SCREEN"),
							request.getParameterValue("ACTION"))
							.or(RequestCommand
									.ofPath(Optional.ofNullable(request.getPathWithinApplication())
											.filter(e -> e.startsWith("/Gate/"))
											.map(e -> e.substring("/Gate".length()))
											.orElse(null))));

		StringJoiner string = new StringJoiner("");

		if (command.module() != null)
		{
			calls.get(new RequestCommand(command.module(), null, null))
					.flatMap(PathProcessor::getText)
					.ifPresent(string::add);

			if (command.screen() != null)
			{
				calls.get(new RequestCommand(command.module(), command.screen(), null))
						.flatMap(PathProcessor::getText)
						.ifPresent(string::add);

				if (command.action() != null)
					calls.get(new RequestCommand(command.module(), command.screen(), command.action()))
							.flatMap(PathProcessor::getText)
							.ifPresent(string::add);

			}
		}

		Attributes attributes = new Attributes();
		Stream.of(element.getAllAttributes())
				.filter(e -> e.getValue() != null)
				.forEach(e -> attributes.put(e.getAttributeCompleteName(), e.getValue()));
		handler.replaceWith("<g-path " + attributes + ">" + string + "</g-path>", false);
	}

	private static Optional<String> getText(Call call)
	{
		StringJoiner string = new StringJoiner("");
		if (call.metadata().name() != null)
		{
			string.add("<label>");
			if (call.metadata().icon() != null)
				string.add("<g-icon>" + call.metadata().icon() + "</g-icon>");
			string.add(call.metadata().name());
			string.add("</label>");
		} else if (call.metadata().icon() != null)
			string.add("<g-icon>" + call.metadata().icon() + "</g-icon>");

		return string.length() != 0 ? Optional.of(string.toString()) : Optional.empty();
	}
}