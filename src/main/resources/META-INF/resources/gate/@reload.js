/* global fetch */

import './trigger.js';
import DOM from './dom.js';
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

			switch (parameters[0] || "_self")
			{
				case "_self":
					window.location =
						window.location.href.endsWith("#") ?
						window.location.href.slice(0, -1)
						: window.location.href;
					break;
				case "_parent":
					window.parent.location =
						window.parent.location.href.endsWith("#") ?
						window.parent.location.href.slice(0, -1)
						: window.parent.location.href;
					break;
				case "_top":
					window.top.location =
						window.top.location.href.endsWith("#") ?
						window.top.location.href.slice(0, -1)
						: window.top.location.href;
					break;
				default:
					let element = DOM.navigate(event, parameters[0])
						.orElseThrow(`${parameters[0]} is not a valid selector`);
					if (element.reload)
						element.reload();
					break;
			}


		})
		.catch(error => event.failure(path, error));
});