import DataURL from './data-url.js';
import EventHandler from './event-handler.js';
import RequestBuilder from './request-builder.js';
import GMessageDialog from './g-message-dialog.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@message", function (event)
{
	event.preventDefault();
	event.stopPropagation();
	let {method, action, form, parameters} = event.detail;

	let type = parameters.filter(e => ["success", "warning", "error", "info"].includes(e))[0] || "success";
	let duration = parameters.filter(e => /^\d+$/.test(e)).map(parseInt)[0];

	let path = event.composedPath();
	return fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.text)
		.then(text =>
		{
			GMessageDialog.show(type, text, duration);
			event.success(path, new DataURL("text/plain", text).toString());
		})
		.catch(error => event.error(path, error));
});
