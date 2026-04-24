/* global fetch */

import DataURL from './data-url.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';
import './trigger.js';

window.addEventListener("@tail", function (event)
{
	let path = event.composedPath();
	let { method, action, parameters: [count = 1], form, signal } = event.detail;

	fetch(RequestBuilder.build(method, action, form), { signal })
		.then(ResponseHandler.json)
		.then(result =>
		{
			if (signal?.aborted)
				return event.resolve(path);

			result = result.slice(-count);
			event.success(path, DataURL.ofJSON(result));
		})
		.catch(error => event.failure(path, error));
});