/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import DataURL from './data-url.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@before-begin", function (event)
{
	let path = event.composedPath();
	let {method, action, parameters: [selector], form, signal} = event.detail;
	let element = DOM.navigate(event, selector).orElseThrow(`${selector} is not a valid selector`);

	fetch(RequestBuilder.build(method, action, form), {signal})
		.then(ResponseHandler.text)
		.then(result =>
		{
			if (signal?.aborted || !document.contains(element))
				return event.resolve(path);

			element.insertAdjacentHTML("beforebegin", result);
			event.success(path, DataURL.ofHTML(result));
		})
		.catch(error => event.failure(path, error));
});