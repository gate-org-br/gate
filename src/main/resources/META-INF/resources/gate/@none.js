/* global fetch */

import './trigger.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@none", function (event)
{
	let path = event.composedPath();
	let {method, action, form} = event.detail;

	return fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.none)
		.then(() => event.success(path))
		.catch(error => event.failure(path, error));
});