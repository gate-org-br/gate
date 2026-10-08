/* global fetch */

import './trigger.js';
import DataURL from './data-url.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@cast", function (event)
{
	let path = event.composedPath();
	let element = path[0] || event.target;
	let {method, action, parameters: [contentType], form, signal} = event.detail;

	fetch(RequestBuilder.build(method, action, form), {signal})
		.then(ResponseHandler.dataURL)
		.then(result =>
		{
			let dataURL = DataURL.parse(result);
			event.success(path, new DataURL(contentType, dataURL.data).toString());

		})
		.catch(error => event.failure(path, error));
});