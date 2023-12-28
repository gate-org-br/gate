/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import submit from './submit.js';
import GFilePicker from './g-file-picker.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';
import EventHandler from './event-handler.js';
import {TriggerStartupEvent, TriggerSuccessEvent, TriggerFailureEvent, TriggerResolveEvent} from './trigger-event.js';


function processWindow(event, target)
{
	event.target.dispatchEvent(new TriggerStartupEvent(event));

	if (event.detail.method === "get")
	{
		target.addEventListener("load", () =>
		{
			event.target.dispatchEvent(new TriggerSuccessEvent(event));
			event.target.dispatchEvent(new TriggerResolveEvent(event));
		}, {once: true});

		target.location = event.detail.action;
	} else if (event.detail.method === "post" && event.detail.form)
	{
		target.addEventListener("load", () =>
		{
			event.target.dispatchEvent(new TriggerSuccessEvent(event));
			event.target.dispatchEvent(new TriggerResolveEvent(event));
		}, {once: true});

		submit(event.detail.form, event.detail.method, event.detail.action, event.detail.target);
	} else
	{
		fetch(RequestBuilder.build(event.detail.method, event.detail.action, event.detail.form))
			.then(ResponseHandler.response)
			.then(response =>
			{
				if (response.headers.get('content-type') === "text/html")
					response.text().then(text =>
					{
						let nodes = new DOMParser().parseFromString(text, 'text/html').documentElement.childNodes;
						target.document.documentElement.replaceChildren(...nodes)
						setTimeout(() => event.target.dispatchEvent(new TriggerSuccessEvent(event)), 0);
					});
				else
					GFilePicker.fetch(response)
						.then(() => setTimeout(() => event.target.dispatchEvent(new TriggerSuccessEvent(event)), 0));
			})
			.catch(error => setTimeout(event.target.dispatchEvent(new TriggerFailureEvent(event, error)), 0))
			.finally(setTimeout(() => event.target.dispatchEvent(new TriggerResolveEvent(event)), 0));
	}
}

window.addEventListener("_self", event => processWindow(event, window));
window.addEventListener("_top", event => processWindow(event, window.top));
window.addEventListener("_parent", event => processWindow(event, window.parent));
window.addEventListener("_blank", event => processWindow(event, window.open()));

window.addEventListener("@frame", function (event)
{
	let selector = `[name='${event.detail.parameters[0]}'], #${event.detail.parameters[0]}`;
	let target = DOM.navigate(event, selector).orElseThrow("Invalid target element");
	processWindow(event, target.contentWindow);
});

function processElement(event, callback)
{
	let path = event.composedPath();
	event.target.dispatchEvent(new TriggerStartupEvent(event));
	return fetch(RequestBuilder.build(event.detail.method, event.detail.action, event.detail.form))
		.then(ResponseHandler.text)
		.then(html => DOM.navigate(event, event.detail.parameters[0])
				.map(target => ({target, html}))
				.orElseThrow("Invalid target element"))
		.then(result => callback(result))
		.then(() => EventHandler.dispatch(path, new TriggerSuccessEvent(event)))
		.catch(error => EventHandler.dispatch(path, new TriggerFailureEvent(event, error)))
		.finally(() => EventHandler.dispatch(path, new TriggerResolveEvent(event)));
}

window.addEventListener("@outerHTML", event => processElement(event, result => result.target.outerHTML = result.html));
window.addEventListener("@innerHTML", event => processElement(event, result => result.target.innerHTML = result.html));
window.addEventListener("@beforebegin", event => processElement(event, result => result.target.insertAdjacentHTML("beforebegin", result.html)));
window.addEventListener("@beforeend", event => processElement(event, result => result.target.insertAdjacentHTML("beforeend", result.html)));
window.addEventListener("@afterbegin", event => processElement(event, result => result.target.insertAdjacentHTML("afterbegin", result.html)));
window.addEventListener("@afterend", event => processElement(event, result => result.target.insertAdjacentHTML("afterend", result.html)));
window.addEventListener("@remove", event => processElement(event, result => result.target.remove()));
window.addEventListener("@none", event => processElement(event, () => undefined));