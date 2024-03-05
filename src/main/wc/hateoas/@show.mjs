/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import DataURL from './data-url.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@show", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {method, action, form, parameters: [selector]} = event.detail;
	let element = DOM.navigate(trigger, selector).orElseThrow(`${selector} is not a valid selector`);

	fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.dataURL)
		.then(result =>
		{
			if (element.show)
				element.show();
			else
				element.removeAttribute("hidden");
			event.success(path, result);
		})
		.catch(error => event.failure(path, error));
});