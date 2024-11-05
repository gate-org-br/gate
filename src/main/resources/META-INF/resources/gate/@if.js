/* global fetch */

import './trigger.js';
import DataURL from './data-url.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@if", function (event)
{
	let path = event.composedPath();
	let element = path[0] || event.target;
	let {method, action, parameters: [script], form} = event.detail;

	fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.dataURL)
		.then(result =>
		{
			let dataURL = DataURL.parse(result);
			let func = new Function("result", `return ${script}`).bind(element);

			let arrow = func();
			let test = dataURL.contentType.startsWith("application/json")
				? JSON.stringify(arrow(JSON.parse(dataURL.data))) : arrow(dataURL.data);

			if (test)
				event.success(path, result);
			else
				event.resolve(path);

		})
		.catch(error => event.failure(path, error));
});