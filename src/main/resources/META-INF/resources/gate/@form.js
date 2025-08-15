/* global fetch */

import './trigger.js';
import GFormDialog from './g-form-dialog.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@form", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {method, action, form, parameters: [width, height]} = event.detail;

	let options = {};
	options.caption = trigger.title;
	options.width = width;
	options.height = height || width;

	fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.json)
		.then(form => GFormDialog.edit(form, options))
		.then(result => event.success(path, result))
		.catch(error => event.failure(path, error));
});