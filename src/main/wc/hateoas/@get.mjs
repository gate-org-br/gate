/* global fetch */

import './trigger.js';
import resolve from './resolve.js';
import DataURL from './data-url.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@get", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {parameters} = event.detail;

	let url = parameters[0]
		|| trigger.getAttribute("href")
		|| trigger.getAttribute("action")
		|| trigger.getAttribute("formaction")
		|| trigger.getAttribute("data-action");

	url = resolve(trigger, event.detail.cause, url);

	return fetch(RequestBuilder.build("get", url))
		.then(ResponseHandler.dataURL)
		.then(result => event.success(path, result))
		.catch(error => event.failure(path, error));
});