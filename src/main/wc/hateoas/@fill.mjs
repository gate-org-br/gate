/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import Extractor from './extractor.js';
import EventHandler from './event-handler.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';
import {TriggerStartupEvent, TriggerSuccessEvent,
	TriggerFailureEvent, TriggerResolveEvent} from './trigger-event.js';

window.addEventListener("@fill", function (event)
{
	let path = event.composedPath();
	let trigger = event.composedPath()[0] || event.target;
	let {method, action, form, parameters} = event.detail;

	parameters = parameters
		.filter(e => e)
		.map(e => DOM.navigate(trigger, e)
				.orElseTrhow(`Invalid selector: ${e}`));

	let value = parameters[0] || trigger.parentNode.querySelector("input[type=hidden]");
	if (!value)
		throw new Error("Value input not found");

	let label = parameters[1] || trigger.parentNode.querySelector("input[type=text]");
	if (!label)
		throw new Error("Label input not found");

	event.target.dispatchEvent(new TriggerStartupEvent(event));
	fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.json)
		.then(result =>
		{
			label.value = Extractor.label(result);
			value.value = Extractor.value(result);
		})
		.then(() => EventHandler.dispatch(path, new TriggerSuccessEvent(event)))
		.catch(error => EventHandler.dispatch(path, new TriggerFailureEvent(event, error)))
		.finally(() => EventHandler.dispatch(path, new TriggerResolveEvent(event)));
});