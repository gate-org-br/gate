/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import EventHandler from './event-handler.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';
import {TriggerStartupEvent, TriggerSuccessEvent, TriggerFailureEvent, TriggerResolveEvent} from './trigger-event.js';

window.addEventListener("@afterbegin", function (event)
{
	let path = event.composedPath();
	let parameters = event.detail.parameters;
	let target = DOM.navigate(event, parameters[0])
		.orElseThrow(`${parameters[0]} is not a valid target element`);

	event.target.dispatchEvent(new TriggerStartupEvent(event));
	return fetch(RequestBuilder.build(event.detail.method, event.detail.action, event.detail.form))
		.then(ResponseHandler.text)
		.then(result => target.insertAdjacentHTML("afterbegin", result))
		.then(() => EventHandler.dispatch(path, new TriggerSuccessEvent(event)))
		.catch(error => EventHandler.dispatch(path, new TriggerFailureEvent(event, error)))
		.finally(() => EventHandler.dispatch(path, new TriggerResolveEvent(event)));
});