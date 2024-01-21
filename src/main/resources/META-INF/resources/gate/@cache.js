/* global fetch */

import './trigger.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

const CACHE = new Map();

window.addEventListener("@cache", function (event)
{
	let path = event.composedPath();
	let {method, action} = event.detail;

	if (method !== "get")
		throw new Error(`Attempt to cache ${method} request`);

	if (CACHE.has(action))
		event.success(path, CACHE.get(action));
	else
		fetch(RequestBuilder.build(method, action))
			.then(ResponseHandler.dataURL)
			.then(result => CACHE.set(action, result))
			.then(() => event.success(path, CACHE.get(action)))
			.catch(error => event.failure(path, error));
});

window.addEventListener("@invalidate-cache", function (event)
{
	CACHE.clear();
	event.success(event.composedPath());
});

window.addEventListener("trigger-success", function (event)
{
	if (event.detail.cause.detail.method !== "get")
		CACHE.clear();
});