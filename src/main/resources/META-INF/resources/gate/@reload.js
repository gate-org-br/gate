/* global fetch */

import './trigger.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@reload", function (event)
{
	let path = event.composedPath();
	let {method, action, form, parameters} = event.detail;

	fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.dataURL)
		.then(result =>
		{
			event.success(path, result);

			let target;
			switch (parameters[0] || "_self")
			{
				case "_self":
					target = window;
					break;
				case "_parent":
					target = window.parent;
					break;
				case "_top":
					target = window.top;
					break;
			}

			let url = target.location.href;
			if (url.endsWith("#"))
				url = url.slice(0, -1);
			target.location = url;

		})
		.catch(error => event.failure(path, error));
});