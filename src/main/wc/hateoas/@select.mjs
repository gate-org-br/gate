import DataURL from './data-url.js';
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
		.then(options => GSelectPicker.pick(options, trigger.title)
				.then(result => JSON.stringify(result.value))
				.then(result => new DataURL("application/json", result).toString())
				.then(result => event.success(path, result))
				.catch(() => event.resolve(path)));

});
