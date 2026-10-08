import trigger from './trigger-core.js';
import TriggerExtractor from './trigger-extractor.js';

window.addEventListener("mouseover", function (event)
{
	let element = event.target || event.composedPath()[0];
	let type = TriggerExtractor.trigger(element);
	if (!type)
		return false;

	if (type.startsWith("hover(") && type.endsWith(")"))
	{
		const timeout = setTimeout(() => trigger(event, element),
			Number(type.slice(6, -1)) * 1000);
		element.addEventListener("mouseleave",
			() => clearTimeout(timeout), {once: true});
	} else if (type === "hover")
	{
		const timeout = setTimeout(() => trigger(event, element), 1000);
		element.addEventListener("mouseleave",
			() => clearTimeout(timeout), {once: true});
	} else if (type === "mouseenter")
		trigger(event, element);
});
