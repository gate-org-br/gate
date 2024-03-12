import './g-stack-frame.js';
import DataURL from './data-url.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@stack", function (event)
{
	let path = event.composedPath();
	let {method, action, form} = event.detail;
	let stack = window.top.document.createElement("g-stack-frame");

	let promise = stack.show();
	if (event.detail.parameters[0] || "fetch" === "fetch")
	{
		stack.setAttribute("data-loading", "");
		fetch(RequestBuilder.build(method, action, form))
			.then(ResponseHandler.text)
			.then(result =>
			{
				promise.finally(() => event.success(path, new DataURL('text/html', result).toString()));
				stack.appendChild(document.createRange().createContextualFragment(result));
			})
			.catch(error =>
			{
				stack.hide();
				event.failure(error);
			}).finally(() => stack.removeAttribute("data-loading"));
	} else if (event.detail.method === "get")
	{
		promise.finally(() => event.success(path));
		stack.iframe.src = event.detail.action;
	} else
	{
		stack.setAttribute("data-loading", "");
		fetch(RequestBuilder.build(method, action, form))
			.then(ResponseHandler.text)
			.then(result =>
			{
				promise.finally(() => event.success(path, new DataURL('text/html', result).toString()));
				stack.iframe.srcDoc = result;
			})
			.catch(error =>
			{
				stack.hide();
				event.failure(error);
			}).finally(() => stack.removeAttribute("data-loading"));
	}
});

