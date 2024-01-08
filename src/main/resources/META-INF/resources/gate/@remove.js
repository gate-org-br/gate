/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import EventHandler from './event-handler.js';
import RequestBuilder from './request-builder.js';
import {TriggerStartupEvent, TriggerSuccessEvent, TriggerFailureEvent, TriggerResolveEvent} from './trigger-event.js';

window.addEventListener("@remove", function (event)
{
	let path = event.composedPath();
	let parameters = event.detail.parameters;
	let target = DOM.navigate(event, parameters[0])
		.orElseThrow(`${parameters[0]} is not a valid target element`);

	event.target.dispatchEvent(new TriggerStartupEvent(event));
	return fetch(RequestBuilder.build(event.detail.method, event.detail.action, event.detail.form))
		.then(() => target.remove())
		.then(() => EventHandler.dispatch(path, new TriggerSuccessEvent(event)))
		.catch(error => EventHandler.dispatch(path, new TriggerFailureEvent(event, error)))
		.finally(() => EventHandler.dispatch(path, new TriggerResolveEvent(event)));
});
