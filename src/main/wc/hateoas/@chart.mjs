import './g-chart-dialog.js';
import DataURL from './data-url.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@chart", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {method, action, form, parameters: [type]} = event.detail;

	let dialog = window.top.document.createElement("g-chart-dialog");
	dialog.type = type || "pie";
	dialog.caption = trigger.getAttribute("title");

	let promise = dialog.show();
	fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.dataURL)
		.then(result =>
		{
			dialog.value = JSON.parse(DataURL.parse(result).data);
			promise.finally(() => event.success(path, result));
		}).catch(error => event.failure(path, error));

});