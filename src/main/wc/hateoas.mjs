/* global fetch */

import './trigger.js';
import GBlock from './g-block.js';
import navigate from './navigate.js';
import GFilePicker from './g-file-picker.js';
import RequestBuilder from './request-builder.js';
import GMessageDialog from './g-message-dialog.js';
import ResponseHandler from './response-handler.js';

function processWindow(event, target)
{
	event.preventDefault();

	GBlock.show("...");

	if (event.detail.method === "get")
	{
		target.addEventListener("load", GBlock.hide, {once: true});
		target.location = event.detail.action;
	} else
		fetch(RequestBuilder.build(event.detail.method, event.detail.action, event.detail.form))
			.then(ResponseHandler.self)
			.then(response =>
				response.headers.get('content-type') === "text/html"
					? response.text().then(text => target.document.documentElement
						.replaceChildren(...new DOMParser().parseFromString(text, 'text/html').documentElement.childNodes))
					: GFilePicker.fetch(response)
			)
			.catch(GMessageDialog.error)
			.finally(GBlock.hide);
}

window.addEventListener("_self", event => processWindow(event, window));
window.addEventListener("_top", event => processWindow(event, window.top));
window.addEventListener("_parent", event => processWindow(event, window.parent));
window.addEventListener("_blank", event => processWindow(event, window.open()));

window.addEventListener("@frame", event =>
	navigate(event, `[name='${event.detail.parameters[0]}'], #${event.detail.parameters[0]}`)
		.then(target => processWindow(event, target.contentWindow))
		.catch(() => {
			throw new Error("Invalid target element");
		}));

function processElement(event)
{
	GBlock.show("...");
	return fetch(RequestBuilder.build(event.detail.method, event.detail.action, event.detail.form))
		.then(ResponseHandler.text)
		.catch(GMessageDialog.error)
		.then(html => navigate(event, event.detail.parameters[0]).then(target => ({target, html})))
		.catch(() => {
			throw new Error("Invalid target element");
		})
		.finally(() => GBlock.hide());
}

window.addEventListener("@outerHTML", event => processElement(event).then(result => result.target.outerHTML = result.html));
window.addEventListener("@innerHTML", event => processElement(event).then(result => result.target.innerHTML = result.html));
window.addEventListener("@beforebegin", event => processElement(event).then(result => result.target.insertAdjacentHTML("beforebegin", result.html)));
window.addEventListener("@beforeend", event => processElement(event).then(result => result.target.insertAdjacentHTML("beforeend", result.html)));
window.addEventListener("@afterbegin", event => processElement(event).then(result => result.target.insertAdjacentHTML("afterbegin", result.html)));
window.addEventListener("@afterend", event => processElement(event).then(result => result.target.insertAdjacentHTML("afterend", result.html)));
window.addEventListener("@remove", event => processElement(event).then(result => result.target.remove()));
window.addEventListener("@none", event => processElement(event).then(() => undefined));