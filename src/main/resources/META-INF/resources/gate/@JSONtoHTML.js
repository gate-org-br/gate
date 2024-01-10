/* global fetch */

import './trigger.js';
import DataURL from './data-url.js';
import Formatter from './formatter.js';
import EventHandler from './event-handler.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';
import TriggerEvent from './trigger-event.js';
import {TriggerStartupEvent, TriggerSuccessEvent, TriggerFailureEvent, TriggerResolveEvent} from './trigger-event.js';

window.addEventListener("@JSONtoHTML", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let target = event.detail.parameters[0];
	let {cause, method, action, form} = event.detail;

	event.target.dispatchEvent(new TriggerStartupEvent(event));
	return fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.json)
		.then(Formatter.JSONtoHTML)
		.then(e => new DataURL("text/html", e))
		.then(result => trigger.dispatchEvent(new TriggerEvent(cause, method, result, target)))
		.then(() => EventHandler.dispatch(path, new TriggerSuccessEvent(event)))
		.catch(error => EventHandler.dispatch(path, new TriggerFailureEvent(event, error)))
		.finally(() => EventHandler.dispatch(path, new TriggerResolveEvent(event)));
});