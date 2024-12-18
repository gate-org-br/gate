/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import DataURL from './data-url.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

function hideable(trigger)
{
	for (let parent = trigger; parent;
			parent = parent.parentNode || parent.host || window.frameElement || window)
		if (parent.hide || parent === window)
			return parent;
	throw new Error("No dialog to hide");
}

window.addEventListener("@hide", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {method, action, form, parameters: [selector]} = event.detail;

	let element = selector
			? DOM.navigate(trigger, selector)
			.orElseThrow(`${selector} is not a valid selector`)
			: hideable(trigger);

	fetch(RequestBuilder.build(method, action, form))
			.then(ResponseHandler.dataURL)
			.then(result =>
			{
				if (element.hide)
					element.hide();
				else if (element === window)
					element.close();
				else
					element.setAttribute("hidden", "");
				event.success(path, result);
			})
			.catch(error => event.failure(path, error));
});