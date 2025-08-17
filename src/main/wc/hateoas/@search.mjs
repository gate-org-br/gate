import VALUES from './values.js';
import DataURL from './data-url.js';
import GSearchPicker from './g-search-picker.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@search", function (event)
{
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {method, action, form, parameters: [filter = "filter", ...columns]} = event.detail;

	if (trigger.tagName === "INPUT" && !trigger.value)
		return event.success(path, new DataURL("application/json", "{}").toString());

	let cancel = VALUES.get(trigger) || "";

	const caption = trigger.title;
	let text = trigger.tagName === "INPUT" ? trigger.value : null;

	const [base, query] = action.split("?");
	const fetcher = text =>
	{
		const params = new URLSearchParams(query || "");
		params.set(filter, text);
		const url = `${base}?${params.toString()}`;
		return fetch(RequestBuilder.build(method, url, form))
			.then(ResponseHandler.json);
	};

	GSearchPicker.pick(fetcher, {text, caption, columns})
		.then(result => result.value)
		.then(DataURL.ofJSON)
		.then(result => event.success(path, result))
		.catch(() =>
		{
			if (trigger.tagName === "INPUT")
				trigger.value = cancel;
			event.resolve(path);
		}).finally(() => trigger.hasAttribute("tabindex") && trigger.focus());
});
