/* global fetch */
import './trigger.js';
import DOM from './dom.js';
import DataURL from './data-url.js';
import property from './property.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

const REGEX = /^\s*(?<selector>.*?)\s*(?:\s*<\s*(?<propname>.*?))?\s*$/;

window.addEventListener("@fill", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {method, action, form, parameters} = event.detail;

	if (!parameters || !parameters.length)
		parameters = ["this.parent()['input[type=hidden]'] < [0]",
			"this.parent()['input[type=text]'] < [1]"];

	fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.dataURL)
		.then(dataURL =>
		{
			let result = DataURL.toJSON(dataURL);
			for (let i = 0; i < parameters.length; i++)
			{
				const parameter = parameters[i];
				const matcher = REGEX.exec(parameter);
				if (!matcher)
					throw new Error(`Invalid parameter: ${parameter}`);

				const selector = matcher.groups.selector;
				if (selector !== "_")
				{
					const propname = matcher.groups.propname ?? `[${i}]`;
					DOM.navigate(trigger, selector)
						.orElseThrow(`Invalid selector: ${selector}`)
						.value = property(result, propname) ?? "";
				}
			}
			event.success(path, dataURL);
		})
		.catch(error => event.failure(path, error));
});