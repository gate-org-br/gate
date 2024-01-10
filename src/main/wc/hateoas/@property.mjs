/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import EventHandler from './event-handler.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';
import {TriggerStartupEvent, TriggerSuccessEvent, TriggerFailureEvent, TriggerResolveEvent} from './trigger-event.js';

window.addEventListener("@property", function (event)
{
	let path = event.composedPath();
	let [target, property = "value"] = event.detail.parameters;

	event.target.dispatchEvent(new TriggerStartupEvent(event));
	target = DOM.navigate(event, target)
		.orElseThrow(`${target} is not a valid target element`);

	fetch(RequestBuilder.build(event.detail.method, event.detail.action, event.detail.form))
		.then(ResponseHandler.auto)
		.then(result => target[property] = result)
		.then(() => EventHandler.dispatch(path, new TriggerSuccessEvent(event)))
		.catch(error => EventHandler.dispatch(path, new TriggerFailureEvent(event, error)))
		.finally(() => EventHandler.dispatch(path, new TriggerResolveEvent(event)));
});