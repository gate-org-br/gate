package gate.report;

import gate.lang.json.JsonObject;
import java.util.function.Predicate;

public class ConditionalStyle
{

	private final Predicate<Object> predicate;
	private final String style;

	public ConditionalStyle(Predicate<Object> predicate, String style)
	{
		this.predicate = predicate;
		this.style = style;
	}

	public static ConditionalStyle of(JsonObject object)
	{
		Predicate<Object> predicate = ReportPredicate.of(object.getJsonObject("condition").orElseThrow(() -> new IllegalArgumentException("Condition not specified")));
		String style = object.getString("style").orElseThrow(() -> new IllegalArgumentException("Style not specified"));
		return new ConditionalStyle(predicate, style);
	}

	public Predicate<Object> getPredicate()
	{
		return predicate;
	}

	public String getStyle()
	{
		return style;
	}
}
