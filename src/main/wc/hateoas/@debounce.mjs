/* global fetch */

import './trigger.js';
import TriggerEvent from './trigger-event.js';

let REGISTRY = new WeakMap();

window.addEventListener("@debounce", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {cause, method, action, parameters} = event.detail;

	parameters = parameters.filter(e => e).filter(e => e.length);
	let target = parameters.filter(e => e[0] === "@")[0] || '@none';
	let timeout = parameters.filter(e => e[0] !== "@").map(e => Number(e))[0] || 500;

	clearTimeout(REGISTRY.get(trigger));
	REGISTRY.set(trigger, setTimeout(() => trigger.dispatchEvent(new TriggerEvent(cause, method, action, target)), timeout));
});