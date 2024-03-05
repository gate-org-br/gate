/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import DataURL from './data-url.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@value", function (event)
{
	let path = event.composedPath();
	let {method, action, form, parameters: [selector]} = event.detail;

	let target = DOM.navigate(event, selector).orElseThrow(`${selector} is not a valid selector`);

	fetch(RequestBuilder.build(method, action, form)).then(response =>
	{
		if (!response)
			return Promise.resolve();
		if (response.ok)
		{
			let contentType = response.headers.get('content-type');
			if (contentType.startsWith("text/"))
				response = response.text();
			else if (contentType === "application/json")
				response = response.json();
			else
				response = response.blob();

			return response.then(result =>
			{
				target.value = result;
				event.success(path, new DataURL(contentType, result).toString());
			});
		}

		return response.text().then(error => Promise.reject(new Error(error)));
	}).catch(error => event.failure(path, error));
});