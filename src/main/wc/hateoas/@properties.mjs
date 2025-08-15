/* global fetch */
import './trigger.js';
import DOM from './dom.js';
import property from './property.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@properties", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {method, action, form, parameters = ["value", "label"]} = event.detail;

	fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.json)
		.then(dataset => event.success(path, parameters.map(e => property(dataset, e))))
		.catch(error => event.failure(path, error));
});