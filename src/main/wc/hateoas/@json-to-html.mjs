/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import DataURL from './data-url.js';
import Formatter from './formatter.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@json-to-html", function (event)
{
	let path = event.composedPath();
	let {method, action, form} = event.detail;

	fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.json)
		.then(Formatter.JSONtoHTML)
		.then(e => new DataURL("text/html", e))
		.then(result => event.success(path, result))
		.catch(error => event.failure(path, error));
});