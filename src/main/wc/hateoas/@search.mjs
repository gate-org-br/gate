import DataURL from './data-url.js';
import GSearchPicker from './g-search-picker.js';

window.addEventListener("@search", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {action, parameters: [filter = "label"]} = event.detail;
	let value = trigger.tagName === "INPUT" ? trigger.value : null;

	GSearchPicker.pick(action, filter, trigger.title, value)
		.then(result => JSON.stringify(result.value))
		.then(result => new DataURL("application/json", result).toString())
		.then(result => event.success(path, result))
		.catch(() => event.resolve(path));
});
