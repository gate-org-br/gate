/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import EventHandler from './event-handler.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';
import {TriggerSuccessEvent, TriggerFailureEvent, TriggerResolveEvent} from './trigger-event.js';

function hide(trigger)
{
	for (let parent = trigger; parent;
		parent = parent.parentNode || parent.host || window.frameElement)
		if (parent.hide)
			return parent.hide();
}

window.addEventListener("@hide", function (event)
{
	event.preventDefault();
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {method, action, form} = event.detail;

	if (!action || action === '#')
		return hide(trigger);

	return fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.none)
		.then(() => EventHandler.dispatch(path, new TriggerSuccessEvent(event)))
		.then(() => hide(trigger))
		.catch(error => EventHandler.dispatch(path, new TriggerFailureEvent(event, error)))
		.finally(() => EventHandler.dispatch(path, new TriggerResolveEvent(event)));
});