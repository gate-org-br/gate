/* global fetch */

import './trigger.js';
import TriggerEvent from './trigger-event.js';

let REGISTRY = new WeakMap();

window.addEventListener("@throttle", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {parameters: [timeout = "1000"]} = event.detail;

	timeout = parseInt(timeout);
	let timestamp = new Date().getTime();
	if (!REGISTRY.has(trigger) || REGISTRY.get(trigger) + timeout < timestamp)
	{
		REGISTRY.set(trigger, timestamp);
		event.success(path);
	} else
		event.resolve(path);
});