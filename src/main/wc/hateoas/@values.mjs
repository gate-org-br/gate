/* global fetch */
import './trigger.js';
import property from './property.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

function map(object, parameters)
{
	return parameters && parameters.length
		? parameters.map(e => property(object, e))
		: Object.values(object);
}

window.addEventListener("@values", function (event)
{
	let path = event.composedPath();
	let {method, action, form, parameters} = event.detail;

	fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.json)
		.then(object => Array.isArray(object)
				? object.map(e => map(e, parameters))
				: map(object, parameters))
		.then(array => event.success(path, array))
		.catch(error => event.failure(path, error));
});