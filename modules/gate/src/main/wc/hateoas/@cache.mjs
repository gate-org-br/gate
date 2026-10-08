/* global fetch */

import './trigger.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

const CACHE = new Map();

window.addEventListener("@cache", function (event) {
	let path = event.composedPath();
	let {method, action, parameters: [timeout = 300000], signal} = event.detail;

	const milliseconds = Number(timeout);
	if (isNaN(milliseconds) || milliseconds < 1)
		throw new Error(`Invalid timeout: ${timeout}`);

	if (method !== "get")
		throw new Error(`Attempt to cache ${method} request`);

	if (CACHE.has(action))
		return event.success(path, CACHE.get(action));

	fetch(RequestBuilder.build(method, action), {signal})
		.then(ResponseHandler.dataURL)
		.then(result =>
		{
			CACHE.set(action, result);
			setTimeout(() => CACHE.delete(action), milliseconds);
			event.success(path, result);
		})
		.catch(error => event.failure(path, error));
});

window.addEventListener("trigger-success", function (event) {
	if (event.detail.cause.detail.method !== "get")
		CACHE.clear();
});
