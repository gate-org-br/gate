/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import EventHandler from './event-handler.js';
import TriggerEvent from './trigger-event.js';
import {TriggerStartupEvent, TriggerSuccessEvent, TriggerFailureEvent, TriggerResolveEvent} from './trigger-event.js';

const stack = new WeakMap();

window.addEventListener("@push", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let target = DOM.navigate(trigger, event.detail.parameters[0])
		.orElseThrow(`${event.detail.parameters[0]} is not a valid element`);

	if (!stack.has(target))
		stack.set(target, []);
	stack.get(target).push(target.innerHTML);

	if (event.detail.parameters[1])
		trigger.dispatchEvent(new TriggerEvent(event.detail.cause,
			event.detail.method,
			event.detail.action,
			event.detail.parameters[1]));
});

window.addEventListener("@mark", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let target = DOM.navigate(trigger, event.detail.parameters[0])
		.orElseThrow(`${event.detail.parameters[0]} is not a valid element`);

	stack.set(target, [target.innerHTML]);

	if (event.detail.parameters[1])
		trigger.dispatchEvent(new TriggerEvent(event.detail.cause,
			event.detail.method,
			event.detail.action,
			event.detail.parameters[1]));
});


window.addEventListener("@pull", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let target = DOM.navigate(trigger, event.detail.parameters[0])
		.orElseThrow(`${event.detail.parameters[0]} is not a valid element`);

	if (!stack.has(target)
		|| !stack.get(target).length)
		throw new Error("Empty element stack");
	target.innerHTML = stack.get(target).pop();

	if (event.detail.parameters[1])
		trigger.dispatchEvent(new TriggerEvent(event.detail.cause,
			event.detail.method,
			event.detail.action,
			event.detail.parameters[1]));
});
