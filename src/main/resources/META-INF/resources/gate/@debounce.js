/* global fetch */

import './trigger.js';

let REGISTRY = new WeakMap();

window.addEventListener("@debounce", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {parameters: [timeout]} = event.detail;

	let prev = REGISTRY.get(trigger);
	if (prev)
	{
		prev.event.resolve(path);
		clearTimeout(prev.timeout);
	}
	REGISTRY.set(trigger, {event, timeout: setTimeout(() => event.success(path), timeout ? timeout * 1 : 500)});
});