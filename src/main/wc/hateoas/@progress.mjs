import process from './process.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

let sequence = 1;
window.addEventListener("@progress", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;

	let processName = trigger.title || "Progresso";
	let processId = trigger.id || "proccess@" + sequence++;

	process(processId,
		processName,
		event.detail.method,
		event.detail.action,
		event.detail.form)
		.then(ResponseHandler.dataURL)
		.then(result => event.success(path, result))
		.catch(error => event.failure(path, error));
});
