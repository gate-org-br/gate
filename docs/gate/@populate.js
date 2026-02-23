import DOM from './dom.js';
import DataURL from './data-url.js';
import Populator from './populator.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@populate", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;

	let {method, action, form, signal} = event.detail;
	let [selector, value = "value", label = "label"] = event.detail.parameters;

	let element = DOM.navigate(trigger, selector).orElseThrow(`${selector} is not a valid selector`);

	fetch(RequestBuilder.build(method, action, form), {signal})
		.then(ResponseHandler.json)
		.then(result =>
		{
			new Populator(result).populate(element, value, label);
			if (element.hasAttribute("data-value"))
				element.value = element.getAttribute("data-value");
			event.success(path, DataURL.ofJSON(result));
		})
		.catch(error => event.failure(path, error));
});

