/* global fetch */

import './trigger.js';
import resolve from './resolve.js';
import DataURL from './data-url.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';
import TriggerExtractor from './trigger-extractor.js';

window.addEventListener("@post", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {action, method, form, parameters} = event.detail;

	let url = parameters[0] || TriggerExtractor.action(trigger);

	url = resolve(trigger, event.detail.context, url);

	return fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.dataURL)
		.then(DataURL.parse)
		.then(result => fetch(RequestBuilder.build("post", url, result.data, result.contentType)))
		.then(ResponseHandler.dataURL)
		.then(result => event.success(path, result))
		.catch(error => event.failure(path, error));
});