/* global fetch */

import './trigger.js';
import TriggerEvent from './trigger-event.js';

let REGISTRY = new WeakMap();

window.addEventListener("@throttle", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {parameters: [timeout = "1000"]} = event.detail;

	let now = Date.now();
	timeout = parseInt(timeout);
	let prev = REGISTRY.get(trigger) || 0;
	if (now - prev > timeout)
	{
		REGISTRY.set(trigger, now);
		event.success(path);
	} else
		event.resolve(path);
});
