package gate.thymeleaf.processors.tag.anchor;

import gate.Command;
import gate.thymeleaf.Expression;
import gate.thymeleaf.Model;
import gate.thymeleaf.processors.tag.ModelProcessor;
import gate.type.Attributes;
import gate.util.Parameters;

public abstract class AnchorProcessor extends ModelProcessor
{

	public AnchorProcessor(String name)
	{
		super(name);
	}

	@Override
	protected void doProcess(Model model)
	{
		Expression expression = Expression.of(model.getContext());
		Parameters parameters = new Parameters();
		if (model.has("arguments"))
			Parameters.parse(model.get("arguments")).entrySet()
				.forEach(entry -> parameters.put(entry.getKey(),
				expression.evaluate(entry.getValue().toString())));

		model.stream()
			.filter(e -> e.getValue() != null)
			.filter(e -> e.getAttributeCompleteName().startsWith("_"))
			.forEach(e -> parameters.put(e.getAttributeCompleteName().substring(1), expression.evaluate(e.getValue())));

		Attributes attributes = new Attributes();
		model.stream()
			.filter(e -> e.getValue() != null)
			.filter(e -> !e.getAttributeCompleteName().startsWith("_"))
			.filter(e -> !"module".equals(e.getAttributeCompleteName()))
			.filter(e -> !"screen".equals(e.getAttributeCompleteName()))
			.filter(e -> !"action".equals(e.getAttributeCompleteName()))
			.filter(e -> !"method".equals(e.getAttributeCompleteName()))
			.filter(e -> !"target".equals(e.getAttributeCompleteName()))
			.filter(e -> !"arguments".equals(e.getAttributeCompleteName()))
			.filter(e -> !"condition".equals(e.getAttributeCompleteName()))
			.filter(e -> !"otherwise".equals(e.getAttributeCompleteName()))
			.forEach(e -> attributes.put(e.getAttributeCompleteName(), e.getValue()));

		Command command = Command.of(model.request(),
			model.get("module"),
			model.get("screen"),
			model.get("action"));

		if (!model.has("style"))
			command.getColor().ifPresent(e -> attributes.put("style", "color: " + e));

		if (!model.has("data-tooltip"))
			command.getTooltip().ifPresent(e -> attributes.put("data-tooltip", e));

		if (!model.has("data-confirm"))
			command.getConfirm().ifPresent(e -> attributes.put("data-confirm", e));

		if (!model.has("data-alert"))
			command.getAlert().ifPresent(e -> attributes.put("data-alert", e));

		if (!model.has("title"))
		{
			command.getDescription().ifPresent(e -> attributes.put("title", e));
			command.getName().ifPresent(e -> attributes.put("title", e));
		}

		process(model, command, attributes, parameters);
	}

	protected abstract void process(Model model, Command command, Attributes attributes, Parameters parameters);
}
