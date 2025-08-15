/* global fetch */
import './trigger.js';
import DOM from './dom.js';
import DataURL from './data-url.js';
import property from './property.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@properties", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {method, action, form, parameters = ["value", "label"]} = event.detail;

	fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.dataURL)
		.then(dataURL =>
		{
			let dataset = DataURL.toJSON(dataURL);
			const properties = parameters.map(e => property(dataset, e));
			const result = JSON.stringify(properties);
			event.success(path, new DataURL("application/json", result).toString());
		})
		.catch(error => event.failure(path, error));
});