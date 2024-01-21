/* global fetch */

import './trigger.js';
import TriggerEvent from './trigger-event.js';

let REGISTRY = new WeakSet();

window.addEventListener("@once", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;

	if (REGISTRY.has(trigger))
		return event.resolve(path);

	REGISTRY.add(trigger);
	event.success(path);
});