/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import EventHandler from './event-handler.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';
import {TriggerSuccessEvent, TriggerFailureEvent, TriggerResolveEvent} from './trigger-event.js';

window.addEventListener("@redirect", function (event)
{
	let path = event.composedPath();
	let {method, action, form} = event.detail;

	return fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.text)
		.then(result = window.location = result)
		.then(() => event.success(path))
		.catch(error => event.failure(path, error));
});