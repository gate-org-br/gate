/* global fetch */

import './trigger.js';

window.addEventListener("@log", function (event)
{
	const path = event.composedPath();
	const trigger = path[0] || event.target;
	const {method, action, form, parameters: [label] = [], context} = event.detail;

	event.resolve(path);
	console.log(event.toLog(label));
	event.success(path);
});