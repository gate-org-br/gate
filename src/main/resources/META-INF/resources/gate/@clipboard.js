/* global fetch */

import './trigger.js';
import DataURL from './data-url.js';
import Clipboard from './clipboard.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@clipboard", function (event)
{
	let path = event.composedPath();
	let {method, action, form, signal} = event.detail;

	fetch(RequestBuilder.build(method, action, form), {signal})
		.then(ResponseHandler.dataURL)
		.then(result =>
		{
			Clipboard.copy(DataURL.parse(result).data);
			event.success(path, result);
		})
		.catch(error => event.failure(path, error));
});