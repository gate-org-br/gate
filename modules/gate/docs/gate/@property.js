/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import DataURL from './data-url.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@property", function (event)
{
	let path = event.composedPath();
	let {method, action, form, parameters: [selector], signal} = event.detail;

	let index = selector.lastIndexOf(":");
	if (index === -1)
		throw new Error("Missing property name");

	let property = selector.substring(index + 1);
	let target = DOM.navigate(event, selector.substring(0, index))
		.orElseThrow(`${selector} is not a valid selector`);

	fetch(RequestBuilder.build(method, action, form), {signal})
		.then(ResponseHandler.json)
		.then(result =>
		{
			target[property] = result;
			event.success(path, DataURL.ofJSON(result));
		}).catch(error => event.failure(path, error));
});