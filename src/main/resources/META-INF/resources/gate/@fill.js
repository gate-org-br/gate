/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import Parser from './parser.js';
import DataURL from './data-url.js';
import Extractor from './extractor.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';


window.addEventListener("@fill", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {method, action, form, parameters} = event.detail;

	if (!parameters || !parameters.length)
		parameters = [trigger.parentNode.querySelector("input[type='hidden']"),
			trigger.parentNode.querySelector("input[type='text']")];
	else
		parameters = parameters
			.map(e => e !== "_" ? DOM.navigate(trigger, e)
					.orElseThrow(`Invalid selector: ${e}`) : null);

	fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.dataURL)
		.then(dataURL =>
		{
			let result = DataURL.toJSON(dataURL);
			for (let i = 0; i < parameters.length; i++)
				if (parameters[i])
					parameters[i].value = Parser.unquote(Extractor.value(result, i));
			event.success(path, dataURL);
		})
		.catch(error => event.failure(path, error));
});