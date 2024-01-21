/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import DataURL from './data-url.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@before-end", function (event)
{
	let path = event.composedPath();
	let {method, action, parameters: [selector], form} = event.detail;
	let element = DOM.navigate(event, selector).orElseThrow(`${selector} is not a valid selector`);

	fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.text)
		.then(result =>
		{
			let fragment = document.createRange()
				.createContextualFragment(result);
			element.appendChild(fragment);
			event.success(path, new DataURL('text/html', result).toString());
		})
		.catch(error => event.failure(path, error));
});