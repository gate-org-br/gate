import './g-dialog.js';
import DataURL from './data-url.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@dialog", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {method, action, form, parameters} = event.detail;

	let dialog = window.top.document.createElement("g-dialog");
	dialog.caption = trigger.getAttribute("title");

	let type = parameters.filter(e => e === "fetch" || e === "frame")[0] || "fetch";
	let size = parameters.filter(e => e !== "fetch" && e !== "frame")[0];
	let height = parameters.filter(e => e => e !== "fetch" && e !== "frame")[1] || size;

	if (size)
		dialog.setAttribute("style", `width: ${size}; height: ${height || size}`);

	if (trigger.hasAttribute("data-navigator") || trigger.parentNode.hasAttribute("data-navigator"))
	{
		let triggers = Array.from(trigger.parentNode.children);
		dialog.navbar.targets = triggers.map(e => e.href || e.formaction || e.getAttribute("data-action"));
		for (let index = 0; index < triggers.length; index++)
			if (triggers[index] === trigger)
				dialog.navbar.index = index;
	}

	let promise = dialog.show();
	if (type === "fetch")
	{
		dialog.setAttribute("data-loading", "");
		fetch(RequestBuilder.build(method, action, form))
			.then(ResponseHandler.text)
			.then(result =>
			{
				promise.finally(() => event.success(path, new DataURL('text/html', result).toString()));
				dialog.appendChild(document.createRange().createContextualFragment(result));
			})
			.catch(error =>
			{
				dialog.hide();
				event.failure(path, error);
			}).finally(() => dialog.removeAttribute("data-loading"));
	} else if (event.detail.method === "get")
	{
		promise.finally(() => event.success(path));
		dialog.iframe.src = event.detail.action;
	} else
	{
		dialog.setAttribute("data-loading", "");
		fetch(RequestBuilder.build(method, action, form))
			.then(ResponseHandler.text)
			.then(result =>
			{
				promise.finally(() => event.success(path, new DataURL('text/html', result).toString()));
				dialog.iframe.srcDoc = result;
			})
			.catch(error =>
			{
				dialog.hide();
				event.failure(path, error);
			}).finally(() => dialog.removeAttribute("data-loading"));
	}
});