/* global fetch */

import './trigger.js';
import DataURL from './data-url.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@notification", function (event)
{
	let path = event.composedPath();
	let {method, action, form, parameters: [title]} = event.detail;
	return fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.text)
		.then(response =>
		{
			if (Notification.permission === "denied")
				return;

			(Notification.permission === "granted"
				? Promise.resolve("granted")
				: Notification.requestPermission())
				.then(permission =>
				{
					if (permission === "denied")
						return;

					const controller = new AbortController();
					const notification = title ? new Notification(title, {body: response})
						: new Notification(response);
					notification.addEventListener("close", () => controller.abort() | event.resolve(path), {signal: controller.signal});
					notification.addEventListener("click", () => controller.abort() | event.success(path, DataURL.ofText(response)), {signal: controller.signal});
				});
		})
		.catch(error => event.failure(path, error));
});