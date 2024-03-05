import DOM from './dom.js';
import trigger from './trigger.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@trigger", function (event)
{
	let path = event.composedPath();
	let {cause, method, action, parameters: [selector], form} = event.detail;

	fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.dataURL)
		.then(result =>
		{
			event.success(path, result);
			DOM.navigate(event, selector).ifPresent(element => trigger(cause, element));
		})
		.catch(error => event.failure(path, error));
});