/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@inner-html", function (event)
{
	let path = event.composedPath();
	let {method, action, parameters: [selector], form} = event.detail;
	let element = DOM.navigate(event, selector).orElseThrow(`${selector} is not a valid selector`);

	fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.text)
		.then(result =>
		{
			element.innerHTML = result;

			element.querySelectorAll("script").forEach(e =>
			{
				const script = document.createElement("script");
				for (const attr of e.attributes)
					script.setAttribute(attr.name, attr.value);
				script.textContent = e.textContent;
				e.replaceWith(script);
			});

			event.success(path);
		})
		.catch(error => event.failure(path, error));
});