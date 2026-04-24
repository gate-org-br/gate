/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@reload", function (event)
{
	let path = event.composedPath();
	let {method, action, form, parameters, signal} = event.detail;

	fetch(RequestBuilder.build(method, action, form), {signal})
		.then(ResponseHandler.dataURL)
		.then(result =>
		{
			event.success(path, result);

			let target = null;


			if (parameters[0])
			{
				switch (parameters[0])
				{
					case "_self":
						target = window.location;
						break;
					case "_parent":
						target = window.parent.location;
						break;
					case "_top":
						target = window.top.location;
						break;
					default:
						target = DOM.navigate(event, parameters[0])
							.orElseThrow(`${parameters[0]} is not a valid selector`);
						break;
				}
			} else
				target = path.find(el => el && typeof el.reload === "function")
					|| window.location;

			if (typeof target.reload === "function")
				target.reload();
		})
		.catch(error => event.failure(path, error));
});