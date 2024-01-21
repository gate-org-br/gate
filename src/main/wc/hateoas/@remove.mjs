/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import EventHandler from './event-handler.js';
import RequestBuilder from './request-builder.js';
import {TriggerSuccessEvent, TriggerFailureEvent, TriggerResolveEvent} from './trigger-event.js';

window.addEventListener("@remove", function (event)
{
	let path = event.composedPath();
	let {method, action, parameters: [selector], form} = event.detail;
	let element = DOM.navigate(event, selector).orElseThrow(`${selector} is not a valid selector`);

	return fetch(RequestBuilder.build(method, action, form))
		.then(() => element.remove())
		.then(() => EventHandler.dispatch(path, new TriggerSuccessEvent(event)))
		.catch(error => EventHandler.dispatch(path, new TriggerFailureEvent(event, error)))
		.finally(() => EventHandler.dispatch(path, new TriggerResolveEvent(event)));
});
