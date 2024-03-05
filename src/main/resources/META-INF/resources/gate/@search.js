import VALUES from './values.js';
import DataURL from './data-url.js';
import GSearchPicker from './g-search-picker.js';

window.addEventListener("@search", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {action, parameters: [filter = "label"]} = event.detail;

	if (trigger.tagName === "INPUT" && !trigger.value)
		return event.success(path, new DataURL("application/json", "{}").toString());

	let value = trigger.tagName === "INPUT" ? trigger.value : null;
	let cancel = VALUES.get(trigger) || "";
	GSearchPicker.pick(action, filter, trigger.title, value)
		.then(result => result.value)
		.then(DataURL.ofJSON)
		.then(result => event.success(path, result))
		.catch(() =>
		{
			if (trigger.tagName === "INPUT")
				trigger.value = cancel;
			event.resolve(path);
		});
});
