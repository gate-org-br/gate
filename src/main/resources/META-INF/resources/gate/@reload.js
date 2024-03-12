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
			let url = window.location.href;
			if (url.endsWith("#"))
				url = url.slice(0, -1);
			window.location = url;
		})
		.catch(error => event.failure(path, error));
});