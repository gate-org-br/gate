/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import DataURL from './data-url.js';
import Extractor from './extractor.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@fill", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {method, action, form, parameters} = event.detail;

	parameters = parameters
		.filter(e => e)
		.map(e => DOM.navigate(trigger, e)
				.orElseTrhow(`Invalid selector: ${e}`));

	let value = parameters[0] || trigger.parentNode.querySelector("input[type=hidden]");
	let label = parameters[1] || trigger.parentNode.querySelector("input[type=text]");

	fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.dataURL)
		.then(dataURL =>
		{
			let result = DataURL.toJSON(dataURL);
			if (label)
				label.value = Extractor.label(result) || "";
			if (value)
				value.value = Extractor.value(result) || "";
			event.success(path, dataURL);
		})
		.catch(error => event.failure(path, error));
});