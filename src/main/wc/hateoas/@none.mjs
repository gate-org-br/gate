/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import EventHandler from './event-handler.js';
import RequestBuilder from './request-builder.js';
import {TriggerStartupEvent, TriggerSuccessEvent, TriggerFailureEvent, TriggerResolveEvent} from './trigger-event.js';

window.addEventListener("@none", function (event)
{
	let path = event.composedPath();
	event.target.dispatchEvent(new TriggerStartupEvent(event));
	return fetch(RequestBuilder.build(event.detail.method, event.detail.action, event.detail.form))
		.then(() => EventHandler.dispatch(path, new TriggerSuccessEvent(event)))
		.catch(error => EventHandler.dispatch(path, new TriggerFailureEvent(event, error)))
		.finally(() => EventHandler.dispatch(path, new TriggerResolveEvent(event)));
});