/* global fetch */

import './trigger.js';
import resolve from './resolve.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@notification", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {method, action, form, parameters: [title, body]} = event.detail;

	if (!title)
		throw new Error("Missing required parameter: title");

	if (Notification.permission === "denied")
		return event.resolve(path);

	(Notification.permission === "granted"
		? Promise.resolve("granted")
		: Notification.requestPermission())
		.then(permission =>
		{
			if (permission === "denied")
				return event.resolve(path);

			if (body)
			{
				form = null;
				method = "get";
				action = resolve(trigger, event.detail.context, `data:text/plain,${body}`);
			}

			if (!action)
				return event.resolve(path);

			return fetch(RequestBuilder.build(method, action, form))
				.then(ResponseHandler.text)
				.then(response =>
				{
					const controller = new AbortController();
					const notification = new Notification(title, {body: response});
					notification.addEventListener("close", () => controller.abort() | event.resolve(path), {signal: controller.signal});
					notification.addEventListener("click", () => controller.abort() | event.success(path), {signal: controller.signal});
				})
				.catch(error => event.failure(path, error));
		});
});