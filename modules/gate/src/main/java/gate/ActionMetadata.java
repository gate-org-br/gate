package gate;

import java.lang.reflect.Method;

import gate.annotation.Alert;
import gate.annotation.Color;
import gate.annotation.Confirm;
import gate.annotation.Description;
import gate.annotation.Emoji;
import gate.annotation.Icon;
import gate.annotation.Name;
import gate.annotation.Tooltip;
import gate.base.Screen;

public record ActionMetadata(
	String name,
	String description,
	gate.icon.Icon icon,
	gate.icon.Emoji emoji,
	String color,
	String tooltip,
	String confirm,
	String alert
	)
	{

	public static ActionMetadata of(Class<Screen> type, Method method)
	{
		var name = Name.Extractor.extract(method).or(() -> Name.Extractor.extract(type)).orElse("unnamed");
		var description = Description.Extractor.extract(method).or(() -> Description.Extractor.extract(type)).orElse(null);
		var icon = Icon.Extractor.extract(method).or(() -> Icon.Extractor.extract(type)).orElse(gate.icon.Icons.UNKNOWN);
		var emoji = Emoji.Extractor.extract(method).or(() -> Emoji.Extractor.extract(type)).orElse(null);
		var color = Color.Extractor.extract(method).or(() -> Color.Extractor.extract(type)).orElse(null);
		var tooltip = Tooltip.Extractor.extract(method).or(() -> Tooltip.Extractor.extract(type)).orElse(null);
		var confirm = Confirm.Extractor.extract(method).or(() -> Confirm.Extractor.extract(type)).orElse(null);
		var alert = Alert.Extractor.extract(method).or(() -> Alert.Extractor.extract(type)).orElse(null);
		return new ActionMetadata(
			name,
			description,
			icon,
			emoji,
			color,
			tooltip,
			confirm,
			alert
		);
	}
}
