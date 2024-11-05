/* global fetch */

import './trigger.js';
import DataURL from './data-url.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@exec", function (event)
{
	let path = event.composedPath();
	let element = path[0] || event.target;
	let {method, action, parameters: [script], form} = event.detail;

	fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.dataURL)
		.then(result =>
		{
			let func = new Function("result", `return ${script}`).bind(element);
			let arrow = func();
			let dataURL = DataURL.parse(result);
			arrow(dataURL.contentType.startsWith("application/json")
				? JSON.parse(dataURL.data)
				: dataURL.data);
			event.success(path, result);
		})
		.catch(error => event.failure(path, error));
});