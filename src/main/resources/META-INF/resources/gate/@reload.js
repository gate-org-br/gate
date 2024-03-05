/* global fetch */

import './trigger.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@reload", function (event)
{
	let path = event.composedPath();
	let {method, action, form} = event.detail;

	fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.dataURL)
		.then(result =>
		{
			event.success(path, result);
			window.location = window.location.href;
		})
		.catch(error => event.failure(path, error));
});