/* global fetch */

import './trigger.js';

let REGISTRY = new WeakMap();

window.addEventListener("@debounce", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {parameters: [delay]} = event.detail;

	event.resolve(path);

	let prev = REGISTRY.get(trigger);
	if (prev)
		clearTimeout(prev);

	delay = delay ? delay * 1 : 1000;
	const timeout = setTimeout(() =>
	{
		REGISTRY.delete(trigger);
		event.success(path);
	}, delay);

	REGISTRY.set(trigger, timeout);
});