/* global fetch, import */

import './trigger.js';
import DOM from './dom.js';
import DataURL from './data-url.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@template", function (event)
{
	let path = event.composedPath();
	let {method, action, parameters: [selector], form} = event.detail;
	let element = DOM.navigate(event, selector).orElseThrow(`${selector} is not a valid selector`);

	import("./handlebars.js")
		.then(e => e.default)
		.then(Handlebars =>
		{
			Handlebars.registerHelper("add", (number, value) => number + value);
			Handlebars.registerHelper("sub", (number, value) => number - value);
			Handlebars.registerHelper("mul", (number, value) => number * value);
			Handlebars.registerHelper("div", (number, value) => number / value);
			return Handlebars.compile(element.innerHTML.replace(/<!--{{([^}]+)}}-->/g, '{{$1}}'));
		}).then(template => fetch(RequestBuilder.build(method, action, form))
			.then(ResponseHandler.json)
			.then(result =>
			{

				result = template(result);
				return result;
			})
			.then(result => event.success(path, new DataURL('text/html', result).toString()))
			.catch(error => event.failure(path, error)));
});