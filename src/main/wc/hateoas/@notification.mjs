/* global fetch */

import './trigger.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@notification", function (event)
{
	let path = event.composedPath();
	let {parameters: [title, body]} = event.detail;

	if (!title)
		return event.failure(path, new Error("Missing required parameter: title"));

	if (!body)
		return event.failure(path, new Error("Missing required parameter: body"));

	event.resolve(path);

	Notification.requestPermission().then(permission =>
	{
		if (permission !== "granted")
			return;

		import("./handlebars.js")
			.then(e => e.default)
			.then(Handlebars =>
			{
				title = Handlebars.compile(title)(event.detail.context);
				body = Handlebars.compile(body)(event.detail.context);
				return new Notification(title, {body});
			})
			.then(notification => notification.addEventListener("click", event.success(path), {once: true}));
	});
});