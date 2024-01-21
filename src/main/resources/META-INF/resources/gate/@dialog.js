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
		dialog.width = dialog.height = size;
	if (height)
		dialog.height = height;


	if (trigger.hasAttribute("data-navigator") || trigger.parentNode.hasAttribute("data-navigator"))
	{
		let triggers = Array.from(trigger.parentNode.children);
		dialog.navbar.targets = triggers.map(e => e.href || e.formaction || e.getAttribute("data-action"));
		for (let index = 0; index < triggers.length; index++)
			if (triggers[index] === trigger)
				dialog.navbar.index = index;
	}

	switch (type)
	{
		case "fetch":
			fetch(RequestBuilder.build(method, action, form))
				.then(ResponseHandler.text)
				.then(result =>
				{
					dialog.show().finally(() => event.success(path, new DataURL('text/html', result).toString()));
					dialog.appendChild(document.createRange().createContextualFragment(result));
				}).catch(error => event.failure(error));
			break;

		case "frame":
			if (event.detail.method === "get")
			{
				dialog.show().finally(() => event.success(path));
				dialog.iframe.src = event.detail.action;
			} else
				fetch(RequestBuilder.build(method, action, form))
					.then(ResponseHandler.text)
					.then(result =>
					{
						dialog.show().finally(() => event.success(path, new DataURL('text/html', result).toString()));
						dialog.iframe.srcDoc = result;

					})
					.catch(error => event.failure(error));
			break;
	}
});