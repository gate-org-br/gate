import DOM from './dom.js';
import VALUES from './values.js';
import Return from './@return.js';
import DataURL from './data-url.js';
import GFramePicker from './g-frame-picker.js';
import GFetchPicker from './g-fetch-picker.js';

window.addEventListener("@pick", function pick(event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {action, parameters: [type]} = event.detail;

	if (trigger.tagName === "INPUT" && !trigger.value)
		return event.success(path, new DataURL("application/json", "[]").toString());

	let value = VALUES.get(trigger) || "";
	let picker = type === "frame" ? GFramePicker : GFetchPicker;
	picker.pick(action, trigger.title)
		.then(DataURL.ofJSON)
		.then(dataURL => event.success(path, dataURL))
		.catch(() =>
		{
			if (trigger.tagName === "INPUT")
				trigger.value = value;
			event.resolve(path);
		}).finally(() => trigger.hasAttribute("tabindex") && trigger.focus());
});
