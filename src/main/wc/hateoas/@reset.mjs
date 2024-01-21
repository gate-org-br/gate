/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import TriggerEvent from './trigger-event.js';

window.addEventListener("@reset", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {cause, method, action, form, parameters} = event.detail;
	let target = parameters.filter(e => e.startsWith("@"))[0] || "@none";

	trigger.dispatchEvent(new TriggerEvent(cause, method, action, target, form));

	parameters = parameters.filter(e => !e.startsWith("@"))
		.map(e => DOM.navigate(trigger, e).orElseThrow(() => `${e} is not a valid selector`));
	if (!parameters.length)
		parameters = [trigger];

	parameters.forEach(element =>
	{
		switch (element.tagName)
		{
			case "FORM":
				element.reset();
				break;
			case "INPUT":
			case "SELECT":
			case "TEXTAREA":
				element.value = "";
				break;
			default:
				element.innerHTML = "";
		}
	});
});