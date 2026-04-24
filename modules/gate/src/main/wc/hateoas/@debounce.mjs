/* global fetch */

import './trigger.js';

let REGISTRY = new WeakMap();

window.addEventListener("@debounce", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {parameters: [timeout = "1000"]} = event.detail;

	event.resolve(path);

	let prev = REGISTRY.get(trigger);
	if (prev)
		clearTimeout(prev);

	const resolve = setTimeout(() =>
	{
		REGISTRY.delete(trigger);
		event.success(path);
	}, parseInt(timeout));

	REGISTRY.set(trigger, resolve);
});