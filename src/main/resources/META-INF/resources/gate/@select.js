import DataURL from './data-url.js';
import GTreePicker from './g-tree-picker.js';
import GSelectPicker from './g-select-picker.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@select", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {method, action, form} = event.detail;

	fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.json)
		.catch(error =>
		{
			event.failure(path, error);
			throw error;
		})
		.then(options =>
		{
			if (options.length
				&& options[0].value
				&& options[0].label
				&& options[0].children
				&& Object.keys(options[0]).length === 3)
				GTreePicker.pick(options, trigger.title)
					.then(e => [e.value.value, e.value.label])
					.then(DataURL.ofJSON)
					.then(result => event.success(path, result))
					.catch(() => event.resolve(path));
			else
				GSelectPicker.pick(options, trigger.title)
					.then(e => e.value)
					.then(DataURL.ofJSON)
					.then(result => event.success(path, result))
					.catch(() => event.resolve(path));
		}).finally(() => trigger.hasAttribute("tabindex") && trigger.focus());

});
