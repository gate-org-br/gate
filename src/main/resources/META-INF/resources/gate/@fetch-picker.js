import DOM from './dom.js';
import VALUES from './values.js';
import Return from './@return.js';
import DataURL from './data-url.js';
import GFetchPicker from './g-fetch-picker.js';

window.addEventListener("@fetch-picker", function pick(event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {action, parameters} = event.detail;
	parameters = parameters.map(e => e !== '_' ? DOM.navigate(trigger, e).orElseThrow(`${e} is not a valid selector`) : null);

	if (trigger.tagName === "INPUT" && !trigger.value)
		return Return.update(parameters, []);

	let value = VALUES.get(trigger) || "";
	GFetchPicker.pick(action, trigger.title)
		.then(values => Return.update(parameters, values))
		.then(DataURL.ofJSON)
		.then(result => event.success(path, result))
		.catch(() =>
		{
			if (trigger.tagName === "INPUT")
				trigger.value = value;
			event.resolve(path);
		});
});
