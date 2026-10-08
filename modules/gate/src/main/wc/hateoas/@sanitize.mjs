/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import DataURL from './data-url.js';
import sanitize from './sanitize.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@sanitize", function (event)
{
	let path = event.composedPath();
	let {method, action, form, signal} = event.detail;

	fetch(RequestBuilder.build(method, action, form), {signal})
		.then(ResponseHandler.text)
		.then(sanitize)
		.then(DataURL.ofHTML)
		.then(result => event.success(path, result))
		.catch(error => event.failure(path, error));
});