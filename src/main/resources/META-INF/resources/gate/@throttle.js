/* global fetch */

import './trigger.js';
import TriggerEvent from './trigger-event.js';

let REGISTRY = new WeakMap();

window.addEventListener("@throttle", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {cause, method, action, parameters} = event.detail;
	parameters = parameters.filter(e => e).filter(e => e.length);
	let target = parameters.filter(e => e[0] === "@")[0] || '@none';
	let timeout = parameters.filter(e => e[0] !== "@").map(e => Number(e))[0] || 1000;

	let timestamp = new Date().getTime();
	if (!REGISTRY.has(trigger) || REGISTRY.get(trigger) + timeout < timestamp)
	{
		REGISTRY.set(trigger, timestamp);
		trigger.dispatchEvent(new TriggerEvent(cause, method, action, target));
	}
});