/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@remove", function (event)
{
	let path = event.composedPath();
	let {method, action, parameters: [selector], form} = event.detail;
	let element = DOM.navigate(event, selector).orElseThrow(`${selector} is not a valid selector`);

	return fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.none)
		.then(() => element.remove())
		.then(() => event.success(path))
		.catch(error => event.failure(path, error));
});
