/* global fetch */

import './trigger.js';
import TriggerEvent from './trigger-event.js';

let REGISTRY = new WeakSet();

window.addEventListener("@once", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;

	if (!REGISTRY.has(trigger))
	{
		REGISTRY.add(trigger);
		let {cause, method, action, parameters} = event.detail;

		let target = parameters[0];
		if (!target || !target.startsWith("@"))
			throw new Error("Required target parameter missing for @once trigger");
		trigger.dispatchEvent(new TriggerEvent(cause, method, action, target));
	}
});