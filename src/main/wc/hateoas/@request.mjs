/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import DataURL from './data-url.js';
import EventHandler from './event-handler.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';
import TriggerEvent from './trigger-event.js';
import {TriggerStartupEvent, TriggerSuccessEvent, TriggerFailureEvent, TriggerResolveEvent} from './trigger-event.js';

const methods = ["get", "post", "put", "delete", "patch"];

window.addEventListener("@request", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {parameters, cause} = event.detail;
	let target = parameters[0] || "@none";
	let method = parameters.slice(1).filter(methods.includes)[0] || "post";
	let action = parameters.slice(1).filter(e => !methods.includes(e))[0] || cause.detail.action;

	let data = DataURL.of(event.detail.action).data;

	event.target.dispatchEvent(new TriggerStartupEvent(event));
	return fetch(RequestBuilder.build(method, action, data))
		.then(ResponseHandler.dataURL)
		.then(action => trigger.dispatchEvent(new TriggerEvent(cause, method, action, target)))
		.then(() => EventHandler.dispatch(path, new TriggerSuccessEvent(event)))
		.catch(error => EventHandler.dispatch(path, new TriggerFailureEvent(event, error)))
		.finally(() => EventHandler.dispatch(path, new TriggerResolveEvent(event)));
});