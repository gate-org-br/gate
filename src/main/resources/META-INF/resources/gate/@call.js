import DataURL from './data-url.js';
import DOM from './dom.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';
import './trigger.js';

window.addEventListener("@call", function (event)
{
	let path = event.composedPath();
	let { method, action, form, parameters: [selector], signal } = event.detail;

	let index = selector.lastIndexOf(":");
	if (index === -1)
		throw new Error("Missing function name");

	let func = selector.substring(index + 1);
	let target = DOM.navigate(event, selector.substring(0, index))
		.orElseThrow(`${selector} is not a valid selector`);

	fetch(RequestBuilder.build(method, action, form), { signal })
		.then(response =>
		{
			const contentType = response.headers.get('content-type')
				|| 'text/plain';
			return ResponseHandler.auto(response)
				.then(result =>
				{
					if (typeof target[func] !== 'function')
						throw new Error(`Function ${func} is not defined on ${selector}`);

					target[func]();
					event.success(path, new DataURL(contentType, result).toString());
				});
		})
		.catch(error => event.failure(path, error));
});