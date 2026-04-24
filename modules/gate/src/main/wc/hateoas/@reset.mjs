/* global fetch */

import DataURL from './data-url.js';
import DOM from './dom.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';
import './trigger.js';

window.addEventListener("@reset", function (event)
{
	let path = event.composedPath();
	let { method, action, form, parameters: [selector], signal } = event.detail;

	let target = DOM.navigate(event, selector)
		.orElseThrow(`${selector} is not a valid selector`);

	fetch(RequestBuilder.build(method, action, form), { signal })
		.then(response =>
		{
			const contentType = response.headers.get('content-type')
				|| 'text/plain';
			return ResponseHandler.auto(response)
				.then(result =>
				{
					if (target.reset)
						target.reset();
					else if (target.tagName === "INPUT"
						|| target.tagName === "SELECT"
						|| target.tagName === "TEXTAREA")
						target.value = "";
					else
						target.replaceChildren();

					event.success(path, new DataURL(contentType, result).toString());
				});
		})
		.catch(error => event.failure(path, error));
});