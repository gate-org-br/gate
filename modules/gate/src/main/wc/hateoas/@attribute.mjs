/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import DataURL from './data-url.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@attribute", function (event)
{
	let path = event.composedPath();
	let {method, action, form, parameters: [selector], signal} = event.detail;

	let index = selector.lastIndexOf(":");
	if (index === -1)
		throw new Error("Missing attribute name");

	let attribute = selector.substring(index + 1);
	let element = DOM.navigate(event, selector.substring(0, index))
		.orElseThrow(`${selector} is not a valid selector`);

	fetch(RequestBuilder.build(method, action, form), {signal})
		.then(response =>
		{
			const contentType = response.headers.get('content-type');
			return ResponseHandler.auto(response).then(result =>
			{
				if (signal?.aborted || !document.contains(element))
					return event.resolve(path);

				if (result)
					element.setAttribute(attribute, result);
				else
					element.removeAttribute(attribute);

				event.success(path, new DataURL(contentType, result).toString());
			});
		})
		.catch(error => event.failure(path, error));
});