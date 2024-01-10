/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import Parser from './parser.js';
import DataURL from './data-url.js';
import TriggerEvent from './trigger-event.js';

const undostack = new WeakMap();

function getElement(trigger, parameter)
{
	if (!parameter)
		throw new Error("Missing @push target trigger");

	let selector = Parser.method(parameter).parameters[0];
	if (!selector)
		throw new Error("Missing @push target selector");

	return DOM.navigate(trigger, selector)
		.orElseThrow(new Error("Invalid @push target selector"));
}

window.addEventListener("@push", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {cause, method, action, parameters} = event.detail;

	let target = parameters[0];

	let element = getElement(trigger, target);

	if (!undostack.has(element))
		undostack.set(element, []);

	undostack.get(element).push(element.innerHTML);
	trigger.dispatchEvent(new TriggerEvent(cause, method, action, target));
});

window.addEventListener("@pull", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {cause, method, action, parameters} = event.detail;

	let target = parameters[0];

	let element = getElement(trigger, target);

	if (undostack.has(element) && undostack.get(element).length)
		action = new DataURL("text/html", undostack.get(element).pop()).toString();

	trigger.dispatchEvent(new TriggerEvent(cause, method, action, target));
});

window.addEventListener("@free", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {cause, method, action, parameters} = event.detail;

	let target = parameters[0];
	let element = getElement(trigger, target);
	undostack.delete(element);
	trigger.dispatchEvent(new TriggerEvent(cause, method, action, target));
});