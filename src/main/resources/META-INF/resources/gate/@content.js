/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import DataURL from './data-url.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

const SELECTOR = "this.parent('.content, #content')";

window.addEventListener("@content", function (event)
{
	let path = event.composedPath();
	let {method, action, form, signal} = event.detail;
	let element = DOM.navigate(event, SELECTOR)
		.orElseThrow(`${SELECTOR} is not a valid selector`);

	fetch(RequestBuilder.build(method, action, form), {signal})
		.then(ResponseHandler.text)
		.then(result =>
		{
			if (signal?.aborted || !document.contains(element))
				return event.resolve(path);

			element.innerHTML = result;
			event.success(path, DataURL.ofHTML(result));
		})
		.catch(error => event.failure(path, error));
});