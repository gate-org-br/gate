/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import Parser from './parser.js';
import DataURL from './data-url.js';
import TriggerEvent from './trigger-event.js';

const CACHE = new Map();

window.addEventListener("@cache", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {cause, method, action, parameters} = event.detail;
	let target = parameters[0];

	if (CACHE.has(action))
		trigger.dispatchEvent(new TriggerEvent(cause, method,
			CACHE.get(action), target));



	if (undostack.has(element) && undostack.get(element).length)
		action = new DataURL("text/html", undostack.get(element).pop()).toString();

	trigger.dispatchEvent(new TriggerEvent(cause, method, action, target));
});