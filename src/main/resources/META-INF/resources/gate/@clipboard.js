/* global fetch */

import './trigger.js';
import DataURL from './data-url.js';
import Clipboard from './clipboard.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@clipboard", function (event)
{
	let path = event.composedPath();
	let {method, action, form} = event.detail;

	fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.dataURL)
		.then(result =>
		{
			Clipboard.copy(DataURL.parse(result).data);
			event.success(path, result);
		})
		.catch(error => event.failure(path, error));
});