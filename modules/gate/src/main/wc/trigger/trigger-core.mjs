import TriggerEvent, {TriggerStartupEvent} from './trigger-event.js';
import TriggerExtractor from './trigger-extractor.js';
import resolve from './resolve.js';

const CONTROLLERS = new WeakMap();

export default function trigger(cause, element, context, action)
{
	if (element.hasAttribute("data-loading"))
		return;

	if (TriggerExtractor.isNative(element))
		return element.click();

	let form = TriggerExtractor.form(element);

	let method = TriggerExtractor.method(element);
	action = action || TriggerExtractor.action(element);
	let target = TriggerExtractor.target(element);

	if (cause.ctrlKey && cause.type === "click")
		target = "_blank";

	if (!target.startsWith("_") && !target.startsWith("@"))
		target = `@frame(${target})`;

	if (!form
		|| element.hasAttribute("formnovalidate")
		|| form.hasAttribute("novalidate")
		|| form.reportValidity())
	{
		action = resolve(element, context || {}, action);
		if (action === null)
			return;

		CONTROLLERS.get(element)?.abort();
		CONTROLLERS.set(element, new AbortController());

		let event = TriggerEvent.of(cause, method, action, form, target, context,
			CONTROLLERS.get(element).signal);
		element.dispatchEvent(new TriggerStartupEvent(event));
		element.dispatchEvent(event);
	}
}
